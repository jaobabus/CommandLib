package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.*;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ArgumentBuilder<ArgumentList, ExecutionContext extends AbstractExecutionContext>
{
    private final List<ArgumentDescriptor<?, ExecutionContext>> originalStream;
    private final Class<ArgumentList> clazz;

    public ArgumentBuilder(Class<ArgumentList> clazz)
    {
        originalStream = new ArrayList<>(clazz.getFields().length);
        this.clazz = clazz;
    }

    public void fillOriginalStream(ArgumentRegistry registry,
                                   ArgumentRestrictionRegistry restrictionsRegistry)
    {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Argument.class))
                originalStream.add(parseArgument(field, registry, restrictionsRegistry));
        }
    }

    public List<ArgumentDescriptor<?, ExecutionContext>> getOriginalStream() {
        return originalStream;
    }

    public CommandArgumentList<ExecutionContext> build()
    {
        CommandArgumentList<ExecutionContext> argList = createArgumentList();

        boolean optionalReached = false;
        boolean varargReached = false;
        for (var argument : originalStream)
        {
            if (argument.action.equals(Argument.Action.FlagStoreTrue)
                    || argument.action.equals(Argument.Action.FlagStoreValue)) {
                argList.flags.put(argument.name, argument);
            }
            else if (argument.action.equals(Argument.Action.Argument)) {
                if (optionalReached || varargReached) {
                    throw new RuntimeException("Unexpected argument after optional or vararg");
                }
                argList.arguments.add(argument);
            }
            else if (argument.action.equals(Argument.Action.Optional)) {
                optionalReached = true;
                if (varargReached) {
                    throw new RuntimeException("Unexpected optional after vararg");
                }
                argList.arguments.add(argument);
            }
            else if (argument.action.equals(Argument.Action.VarArg)) {
                varargReached = true;
                argList.arguments.add(argument);
            }
            else {
                throw new RuntimeException("Unimplemented action");
            }
        }

        return argList;
    }

    @SuppressWarnings("unchecked")
    private <T> ArgumentDescriptor<T, ExecutionContext> parseArgument(Field field,
                                                                      ArgumentRegistry registry,
                                                                      ArgumentRestrictionRegistry restrictionsRegistry)
    {
        ArgumentDescriptor<T, ExecutionContext> descriptor = new ArgumentDescriptor<>();

        Argument annotation = field.getAnnotation(Argument.class);
        descriptor.name = field.getName();
        descriptor.action = annotation.action();
        if (field.isAnnotationPresent(Argument.Phrase.class))
            descriptor.help.phrase = field.getAnnotation(Argument.Phrase.class).phrase();
        if (field.isAnnotationPresent(Argument.Help.class))
            descriptor.help.help = field.getAnnotation(Argument.Help.class).help();
        descriptor.defaultValue = annotation.defaultValue();

        var argId = clazz.getName() + "." + field.getName();
        AbstractArgument<T, ExecutionContext> argument = null;
        switch (annotation.action()) {
            case VarArg:
                argument = (AbstractArgument<T, ExecutionContext>)registry.getArgument(field.getType().getComponentType());
                if (argument == null)
                    throw new RuntimeException("Unregistered argument type " + field.getType().getComponentType() + " for " + argId);
            case Optional:
            case Argument:
                if (argument == null)
                    argument = (AbstractArgument<T, ExecutionContext>)registry.getArgument(field.getType());
                if (argument == null)
                    throw new RuntimeException("Unregistered argument type " + field.getType() + " for " + argId);
                break;
            case FlagStoreTrue:
            case FlagStoreValue:
                argument = (AbstractArgument<T, ExecutionContext>)registry.getArgument(field.getType());
                if (argument == null)
                    throw new RuntimeException("Unregistered argument type " + field.getType() + " for " + argId);
                break;
            default:
                throw new RuntimeException("Unknown argument action");
        }
        descriptor.argument = argument;

        if (field.isAnnotationPresent(ArgumentRestriction.class)) {
            for (var restrictionAnnotation : field.getAnnotationsByType(ArgumentRestriction.class)) {
                AbstractArgumentRestriction<T> rest = AbstractRestrictionFactory.execute(restrictionAnnotation.restriction(), registry, restrictionsRegistry);
                descriptor.restrictions.add(rest);
            }
        }

        return descriptor;
    }

    private CommandArgumentList<ExecutionContext> createArgumentList() {
        return new CommandArgumentList<ExecutionContext>(originalStream) {
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
