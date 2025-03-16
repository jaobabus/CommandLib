package command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import argument.*;
import argument.arguments.ArgumentRegistry;
import argument.restrictions.ArgumentRestrictionRegistry;
import argument.restrictions.AbstractRestrictionFactory;
import util.AbstractExecutionContext;
import util.ParseError;


public class ArgumentBuilder
{
    public static <ArgumentList> CommandArgumentList build(Class<ArgumentList> clazz,
                                                           ArgumentRegistry registry,
                                                           ArgumentRestrictionRegistry restrictionsRegistry)
    {
        CommandArgumentList argList = createArgumentList(clazz);

        boolean optionalReached = false;
        boolean varargReached = false;
        for (Field field : clazz.getDeclaredFields()) {

            if (field.isAnnotationPresent(Flag.class) || field.isAnnotationPresent(Argument.class)) {
                String name = field.getName();

                AbstractArgumentRestriction<?>[] restrictions = null;

                if (field.isAnnotationPresent(ArgumentRestriction.class)) {
                    var annotations = field.getAnnotationsByType(ArgumentRestriction.class);
                    List<AbstractArgumentRestriction<?>> restrictionsList = new ArrayList<>();
                    for (var annotation : annotations) {
                        var restName = annotation.restriction().split(" ")[0];
                        @SuppressWarnings("unchecked")
                        var factory = (AbstractRestrictionFactory<Object>)restrictionsRegistry.getRestriction(restName);
                        var argParser = new SimpleCommandParser<>();
                        Object parsed = null;

                        try {
                            var context = new AbstractExecutionContext();
                            context.executor = "<ArgumentBuilder>";
                            var args = annotation.restriction().substring(restName.length() + 1).split(" ");
                            parsed = argParser.parseSimple(args, factory.getArgumentList(), context);
                        }
                        catch (ParseError e) {
                            throw new RuntimeException(e);
                        }

                        restrictionsList.add(factory.execute(parsed));
                    }
                    restrictions = restrictionsList.toArray(new AbstractArgumentRestriction[] {});
                }

                if (field.isAnnotationPresent(Flag.class)) {
                    Flag annotation = field.getAnnotation(Flag.class);
                    AbstractArgument<?> arg = registry.getArgument(field.getType());
                    if (arg == null)
                        throw new RuntimeException("Flag for " + field.getType() + " not found");

                    argList.flags.put(name, new CommandArgumentList.FlagPair(arg, annotation, restrictions));
                } else if (field.isAnnotationPresent(Argument.class)) {
                    Argument annotation = field.getAnnotation(Argument.class);
                    AbstractArgument<?> arg = (!annotation.vararg()
                            ? registry.getArgument(field.getType())
                            : registry.getArgument(field.getType().getComponentType()));
                    if (arg == null)
                        throw new RuntimeException("Argument for " + field.getType() + " not found");

                    if (annotation.optional()) {
                        optionalReached = true;
                        if (varargReached)
                            throw new RuntimeException("Invalid argument " + name + " after vararg argument");
                    } else if (annotation.vararg()) {
                        varargReached = true;
                    } else if (optionalReached) {
                        throw new RuntimeException("Invalid non-optional argument " + name + " after optional argument");
                    } else if (varargReached) {
                        throw new RuntimeException("Invalid non-vararg argument " + name + " after vararg argument");
                    }

                    argList.arguments.add(new CommandArgumentList.ArgPair(name, arg, annotation, restrictions));
                }
            }
        }

        return argList;
    }

    private static <T> CommandArgumentList createArgumentList(Class<T> clazz) {
        return new CommandArgumentList() {
            @Override
            public Object newInstance() {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Cannot instantiate " + clazz.getName(), e);
                }
            }

            @Override
            public Class<?> getType() {
                return clazz;
            }
        };
    }

}
