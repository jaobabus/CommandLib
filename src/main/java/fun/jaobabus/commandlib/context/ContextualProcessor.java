package fun.jaobabus.commandlib.context;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
    Example:
        class TFileContext
        {
            String path;
            String pattern;
            String result; // bad, but example
        }

        @LinkTo(source = "value", target = "path.path")
        String path;

        @LinkTo(source = "value", target = "path.pattern")
        String pattern;

        @LinkFrom(source = "path")
        @LinkTo(source = "path.result", target = "content.input")
        TemplateFile file;

        @LinkFrom(source = "content") // for example for tab complete
        String value;

    Workflow:
        <cmd> some-path some-pattern file-{}.txt
        create TFileContext as ctx to path
        some-path -> path.path
        some-pattern -> path.pattern
        path -> file; path.result -> content.input
        content -> value

 */
public class ContextualProcessor<ArgumentType, ArgumentContext extends BaseArgumentContext>
{
    String sourceContextName = "";
    String targetContextName = "";
    String executionContextName;
    Class<BaseArgumentContext> argumentContextClass;
    Map<String, String> targets; // Paths in context
    AbstractArgument<ArgumentType, ArgumentContext> argument;
    Set<String> dependencyArguments;
    boolean vararg;

    public void makeSourceContextIfAbsent(AbstractExecutionContext ec) throws ParseError
    {
        if (!sourceContextName.isEmpty() && ec.getContextualValue(sourceContextName) == null) {
            try {
                var ctx = argumentContextClass.getDeclaredConstructor().newInstance();
                if (executionContextName != null) {
                    var field = ctx.getClass().getField(executionContextName);
                    field.set(ctx, ec);
                }
                ctx.dependencyArguments = dependencyArguments;
                ec.setContextualValue(sourceContextName, ctx);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException | NoSuchFieldException e) {
                throw new ParseError(AbstractMessage.fromString(e.toString()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ArgumentContext getContextFor(AbstractExecutionContext ec) throws ParseError {
        if (!sourceContextName.isEmpty()) {
            var ctx = (ArgumentContext)ec.getContextualValue(sourceContextName);
            if (ctx.appliedDependencyArguments.size() != ctx.dependencyArguments.size())
                throw new ParseError(
                        AbstractMessage.fromString("Used incomplete argument context, missing arguments "
                                + String.join(", ",
                                ctx.dependencyArguments
                                        .stream()
                                        .filter(d -> !ctx.appliedDependencyArguments.contains(d))
                                        .toList())));
            return ctx;
        }
        return null;
    }

    public ArgumentType parseWithContext(String stringValue,
                                         AbstractExecutionContext ec)
            throws ParseError
    {
        var ctx = getContextFor(ec);
        var res = argument.parseSimple(stringValue, ctx);
        extractValuesFor(res, ctx, ec);
        return res;
    }

    public List<ArgumentType> getTabCompletes(String source,
                                              AbstractExecutionContext ec)
            throws ParseError
    {
        // We needed only @LinkFrom content
        var ctx = getContextFor(ec);
        return getTabCompletes(source, ctx);
    }

    public List<ArgumentType> getTabCompletes(String source,
                                              ArgumentContext ac)
            throws ParseError
    {
        return argument.tapComplete(source, ac);
    }

    public String dumpWithContext(ArgumentType value,
                                  AbstractExecutionContext ec)
    {
        try {
            var ctx = getContextFor(ec);
            return argument.dumpSimple(value, ctx);
        } catch (ParseError e) {
            return "Error " + e;
        }
    }

    public void extractValuesFor(ArgumentType value, ArgumentContext ac, AbstractExecutionContext ec)
    {
        for (var sourcePath : targets.keySet()) {
            var fullTargetPath = targets.get(sourcePath);
            var targetPath = fullTargetPath.substring(fullTargetPath.indexOf('.') + 1);
            var targetContext = ec.getContextualValue(fullTargetPath.substring(0, fullTargetPath.indexOf('.')));
            var targetName = targetPath.contains(".")
                    ? targetPath.substring(targetPath.lastIndexOf('.'))
                    : targetPath;
            String parentPath = targetPath.contains(".")
                    ? targetPath.substring(0, targetPath.lastIndexOf('.'))
                    : "";
            var targetParent = getTarget(targetContext, parentPath);
            try {
                Field field = targetParent.getClass().getDeclaredField(targetName);
                field.setAccessible(true);

                Object valueSource = ec;
                if (sourcePath.startsWith("<value>")) {
                    if (sourcePath.startsWith("<value>."))
                        sourcePath = sourcePath.substring(sourcePath.indexOf('.') + 1);
                    else
                        sourcePath = "";
                    valueSource = value;
                }

                var toValue = getTarget(valueSource, sourcePath);
                if (vararg) {
                    @SuppressWarnings("unchecked")
                    List<Object> arr = (List<Object>) field.get(targetParent);
                    if (arr == null) {
                        arr = new ArrayList<>();
                        field.set(targetParent, arr);
                    }
                    arr.add(toValue);
                }
                else
                    field.set(targetParent, toValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Object getTarget(Object instance, String targetPath)
    {
        if (targetPath.isEmpty())
            return instance;
        var r = targetPath.split("\\.", 2);
        var targetName = r[0];
        var nextPath = (r.length > 1 ? r[1] : null);
        try {
            var field = instance.getClass().getDeclaredField(targetName);
            field.setAccessible(true);
            if (nextPath != null)
                return getTarget(field.get(instance), nextPath);
            else
                return field.get(instance);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
