package fun.jaobabus.commandlib.context;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentDescriptor;

import java.lang.reflect.Field;
import java.util.*;

public class ContextualBuilder
{
    private final List<ArgumentDescriptor<?, ?>> originalStream;

    public ContextualBuilder(List<ArgumentDescriptor<?, ?>> originalStream)
    {
        this.originalStream = originalStream;
    }

    public static <T, AC extends BaseArgumentContext>
    void parseArgument(Map<String, ContextualProcessor<?, ?>> processorMap,
                       Map<String, Set<String>> dependencies,
                       ArgumentDescriptor<T, AC> argument)
    {
        var field = argument.field;
        var processor = new ContextualProcessor<T, AC>();
        var contextClass = argument.argument.getContextClass();
        boolean required = parseArgumentAnnotations(argument, dependencies, processor, field, contextClass);

        if (argument.argument.getContextClass().equals(Object.class) && !required) {
            System.out.println("[WARN] Context destination not specified for " + argument.name);
        }
        else if (argument.argument.getContextClass().equals(Object.class) && required) {
            System.out.println("[ERROR] Required context not exists for " + argument.name);
        }

        if (required)
            processorMap.put(argument.name, processor);
    }

    @SuppressWarnings("unchecked")
    public static <T, AC extends BaseArgumentContext>
    boolean parseArgumentAnnotations(ArgumentDescriptor<T, AC> argument,
                                     Map<String, Set<String>> dependencies,
                                     ContextualProcessor<T, AC> processor,
                                     Field field,
                                     Class<AC> contextClass) {
        boolean required = false;
        processor.targets = new HashMap<>();
        processor.argument = argument.argument;
        processor.argumentContextClass = (Class<BaseArgumentContext>)contextClass;
        processor.dependencyArguments = dependencies.computeIfAbsent(argument.name, k -> new HashSet<>());
        var action = argument.field.getAnnotation(Argument.class).action();
        processor.vararg = action == Argument.Action.FlagAppendValue || action == Argument.Action.VarArg;

        for (var ctxField : contextClass.getDeclaredFields()) {
            if (ctxField.isAnnotationPresent(ExecutionContext.class)) {
                ctxField.setAccessible(true);
                required = true;
                processor.executionContextName = ctxField.getName();
            }
        }

        if (field.isAnnotationPresent(LinkTo.class)) {
            var annotations = field.getAnnotationsByType(LinkTo.class);
            required = true;
            // if (!annotation.source().equals("<value>")) {
            //     can't check contains source in source context
            //     var source = contextClass.getField(annotation.source());
            // }
            for (var annotation : annotations) {
                dependencies.computeIfAbsent(annotation.target().split("\\.", 2)[0], k -> new HashSet<>())
                        .add(argument.name);
                processor.targets.put(annotation.source(), annotation.target());
            }
        }
        if (field.isAnnotationPresent(LinkFrom.class)) {
            var annotation = field.getAnnotation(LinkFrom.class);
            required = true;
            // can't check contains source in source context
            // var source = contextClass.getField(annotation.source());
            processor.sourceContextName = annotation.source();
        }

        argument.processor = processor;
        return required;
    }

    public Map<String, ContextualProcessor<?, ?>> build()
    {
        Map<String, Set<String>> dependencies = new HashMap<>();
        Map<String, ContextualProcessor<?, ?>> processorMap = new HashMap<>();
        for (var arg : originalStream) {
            parseArgument(processorMap, dependencies, arg);
        }
        return processorMap;
    }

}
