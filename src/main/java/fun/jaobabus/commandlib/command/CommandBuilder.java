package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandBuilder<ExecutionContext extends AbstractExecutionContext>
{
    public interface StandAloneCommand<ExecutionContext extends AbstractExecutionContext>
    {
        String getName();
        String aliasOf();

        AbstractMessage execute(String[] args, ExecutionContext context) throws ParseError;
        List<String> tabComplete(String[] args, ExecutionContext context);
    }

    public class CommandDescription<AL>
    {
        public static class Help {
            public String phrase;
            public String help;
        }

        public String name;
        public AbstractCommand<AL, ExecutionContext> command;
        public List<AbstractArgumentRestriction<AbstractCommand<AL, ExecutionContext>>> restrictions;
        public Help help = new Help();
    }

    Class<?> clazz;
    @Getter
    List<CommandDescription<?>> originalStream;

    public CommandBuilder(Class<?> clazz)
    {
        this.clazz = clazz;
        originalStream = new ArrayList<>(clazz.getFields().length);
    }

    public void fillOriginalStream(ArgumentRegistry registry,
                                   ArgumentRestrictionRegistry restrictionsRegistry)
    {
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Command.class)) {
                originalStream.add(parseCommand(field, registry, restrictionsRegistry));
            }
        }
    }

    private <T> CommandDescription<T> parseCommand(Field field,
                                                   ArgumentRegistry registry,
                                                   ArgumentRestrictionRegistry restrictionsRegistry)
    {
        List<AbstractArgumentRestriction<AbstractCommand<T, ExecutionContext>>> restrictions = new ArrayList<>();

        if (field.isAnnotationPresent(CommandRestriction.class)) {
            var annotations = field.getAnnotationsByType(CommandRestriction.class);
            for (var annotation : annotations) {
                restrictions.add(AbstractRestrictionFactory.execute(annotation.restriction(), "", registry, restrictionsRegistry));
            }
        }

        var desc = new CommandDescription<T>();
        desc.name = field.getName();
        desc.restrictions = restrictions;
        desc.command = getCommand(field, registry, restrictionsRegistry);

        if (field.isAnnotationPresent(Command.Phrase.class))
            desc.help.phrase = field.getAnnotation(Command.Phrase.class).phrase();
        if (field.isAnnotationPresent(Command.Help.class))
            desc.help.help = field.getAnnotation(Command.Help.class).help();

        return desc;
    }

    public Map<String, StandAloneCommand<ExecutionContext>> build()
    {
        Map<String, StandAloneCommand<ExecutionContext>> commands = new HashMap<>();
        Map<String, CommandDescription<?>> originalCommands = new HashMap<>();

        for (var cmd : originalStream) {
            commands.put(cmd.name, getStandAloneCommand(cmd, ""));
            originalCommands.put(cmd.name, cmd);
        }

        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Command.class)) {
                var annotation = field.getAnnotation(Command.class);
                if (!annotation.aliasOf().isEmpty()) {
                    var originalCommand = originalCommands.get(field.getName());
                    if (originalCommand == null)
                        throw new RuntimeException("Unknown command to alias " + field.getName());
                    var command = getStandAloneCommand(originalCommand, annotation.aliasOf());
                    commands.put(field.getName(), command);
                }
            }
        }

        return commands;
    }

    @SuppressWarnings("unchecked")
    <T> AbstractCommand<T, ExecutionContext> getCommand(Field field,
                                                        ArgumentRegistry registry,
                                                        ArgumentRestrictionRegistry restrictionsRegistry)
    {
        AbstractCommand<T, ExecutionContext> cmd;
        try {
            cmd = (AbstractCommand<T, ExecutionContext>)
                    field.getType()
                            .getDeclaredConstructor(ArgumentRegistry.class, ArgumentRestrictionRegistry.class)
                            .newInstance(registry, restrictionsRegistry);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Exception while compile " + field, e);
        }
        return cmd;
    }

    private <T> StandAloneCommand<ExecutionContext> getStandAloneCommand(CommandDescription<T> description,
                                                                         String aliasOf)
    {
        return new StandAloneCommand<>() {
            final DefaultSimpleTabCompleter<ExecutionContext> defaultTabCompleter = new DefaultSimpleTabCompleter<>();

            @Override
            public String getName() {
                return description.name;
            }

            @Override
            public String aliasOf() {
                return (aliasOf.isEmpty() ? null : aliasOf);
            }

            @Override
            public AbstractMessage execute(String[] args, ExecutionContext context) throws ParseError {
                for (var rest : description.restrictions)
                    rest.assertRestriction(description.command, context);
                var parsed = description.command.getParser().parseSimple(args, description.command.getArgumentList(), context);
                return description.command.execute(parsed, context);
            }

            @Override
            public List<String> tabComplete(String[] args, ExecutionContext context) {
                return defaultTabCompleter.tabComplete(args, context, description.command.getArgumentList());
            }
        };
    }
}
