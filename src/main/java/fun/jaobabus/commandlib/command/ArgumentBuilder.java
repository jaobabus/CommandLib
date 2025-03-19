package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.*;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ArgumentBuilder<ArgumentList>
{
    private final List<ArgumentDescriptor> originalStream;
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

    public List<ArgumentDescriptor> getOriginalStream() {
        return originalStream;
    }

    public CommandArgumentList build()
    {
        CommandArgumentList argList = createArgumentList();

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

    private ArgumentDescriptor parseArgument(Field field,
                                             ArgumentRegistry registry,
                                             ArgumentRestrictionRegistry restrictionsRegistry)
    {
        ArgumentDescriptor descriptor = new ArgumentDescriptor();

        Argument annotation = field.getAnnotation(Argument.class);
        descriptor.name = field.getName();
        descriptor.action = annotation.action();
        if (field.isAnnotationPresent(Argument.Phrase.class))
            descriptor.help.phrase = field.getAnnotation(Argument.Phrase.class).phrase();
        if (field.isAnnotationPresent(Argument.Help.class))
            descriptor.help.help = field.getAnnotation(Argument.Help.class).help();

        AbstractArgument<?> argument = null;
        switch (annotation.action()) {
            case VarArg:
                argument = registry.getArgument(field.getType().getComponentType());
            case Optional:
            case Argument:
                if (argument == null)
                    argument = registry.getArgument(field.getType());
                break;
            case FlagStoreTrue:
            case FlagStoreValue:
                argument = registry.getArgument(field.getType());
                break;
            default:
                throw new RuntimeException("Unknown argument action");
        }
        descriptor.argument = argument;

        if (field.isAnnotationPresent(ArgumentRestriction.class)) {
            for (var restrictionAnnotation : field.getAnnotationsByType(ArgumentRestriction.class)) {
                var rest = AbstractRestrictionFactory.execute(restrictionAnnotation.restriction(), registry, restrictionsRegistry);
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
