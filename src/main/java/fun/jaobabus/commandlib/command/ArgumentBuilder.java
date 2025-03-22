package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.*;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.context.ContextualBuilder;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ArgumentBuilder<ArgumentList, ExecutionContext extends AbstractExecutionContext>
{
    private final List<ArgumentDescriptor<?, ?>> originalStream;
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

    public List<ArgumentDescriptor<?, ?>> getOriginalStream() {
        return originalStream;
    }

    public CommandArgumentList build()
    {
        CommandArgumentList argList = createArgumentList();

        var builder = new ContextualBuilder(originalStream);
        argList.contextualArguments = builder.build();

        boolean optionalReached = false;
        boolean varargReached = false;
        for (var argument : originalStream)
        {
            if (argument.action.equals(Argument.Action.FlagStoreTrue)
                    || argument.action.equals(Argument.Action.FlagStoreValue)
                    || argument.action.equals(Argument.Action.FlagAppendValue)) {
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
    private <T, AC extends BaseArgumentContext>
    ArgumentDescriptor<T, AC> parseArgument(Field field,
                                            ArgumentRegistry registry,
                                            ArgumentRestrictionRegistry restrictionsRegistry)
    {
        ArgumentDescriptor<T, AC> descriptor = new ArgumentDescriptor<>();

        Argument annotation = field.getAnnotation(Argument.class);
        descriptor.name = field.getName();
        descriptor.action = annotation.action();
        if (field.isAnnotationPresent(Argument.Phrase.class))
            descriptor.help.phrase = field.getAnnotation(Argument.Phrase.class).phrase();
        if (field.isAnnotationPresent(Argument.Help.class))
            descriptor.help.help = field.getAnnotation(Argument.Help.class).help();
        descriptor.defaultValue = annotation.defaultValue();
        descriptor.field = field;

        var argId = clazz.getName() + "." + field.getName();
        AbstractArgument<T, AC> argument = null;
        switch (annotation.action()) {
            case VarArg:
                argument = (AbstractArgument<T, AC>)registry.getArgument(field.getType().getComponentType());
                if (argument == null)
                    throw new RuntimeException("Unregistered argument type " + field.getType().getComponentType() + " for " + argId);
            case Optional:
            case Argument:
                if (argument == null)
                    argument = (AbstractArgument<T, AC>)registry.getArgument(field.getType());
                if (argument == null)
                    throw new RuntimeException("Unregistered argument type " + field.getType() + " for " + argId);
                break;
            case FlagAppendValue:
                argument = (AbstractArgument<T, AC>)registry.getArgument(field.getType().getComponentType());
                if (argument == null)
                    throw new RuntimeException("Unregistered argument type " + field.getType().getComponentType() + " for " + argId);
            case FlagStoreTrue:
            case FlagStoreValue:
                if (argument == null)
                    argument = (AbstractArgument<T, AC>)registry.getArgument(field.getType());
                if (argument == null)
                    throw new RuntimeException("Unregistered argument type " + field.getType() + " for " + argId);
                break;
            default:
                throw new RuntimeException("Unknown argument action");
        }
        descriptor.argument = argument;

        if (field.isAnnotationPresent(ArgumentRestriction.class)) {
            for (var restrictionAnnotation : field.getAnnotationsByType(ArgumentRestriction.class)) {
                AbstractArgumentRestriction<T> rest = AbstractRestrictionFactory.execute(restrictionAnnotation.restriction(), restrictionAnnotation.path(), registry, restrictionsRegistry);
                descriptor.restrictions.add(rest);
            }
        }

        return descriptor;
    }

    private CommandArgumentList createArgumentList() {
        return new CommandArgumentList(originalStream) {
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
