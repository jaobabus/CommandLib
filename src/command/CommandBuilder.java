package command;

import argument.AbstractArgumentRestriction;
import argument.arguments.ArgumentRegistry;
import argument.restrictions.AbstractRestrictionFactory;
import argument.restrictions.ArgumentRestrictionRegistry;
import org.jetbrains.annotations.NotNull;
import util.AbstractExecutionContext;
import util.AbstractMessage;
import util.ParseError;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandBuilder
{
    public interface StandAloneCommand
    {
        String getName();
        String aliasOf();

        AbstractMessage execute(String[] args, AbstractExecutionContext context) throws ParseError;
        List<String> tabComplete(String[] args, AbstractExecutionContext context);
    }

    public static Map<String, StandAloneCommand> build(Class<?> container,
                                                       ArgumentRegistry registry,
                                                       ArgumentRestrictionRegistry restrictionsRegistry)
    {
        Map<String, StandAloneCommand> commands = new HashMap<>();
        Map<String, StandAloneCommand> allCommands = new HashMap<>();
        Map<String, String> allAliases = new HashMap<>();
        for (var field : container.getDeclaredFields()) {
            if (field.isAnnotationPresent(Command.class)) {
                List<AbstractArgumentRestriction<?>> restrictions = new ArrayList<>();

                if (field.isAnnotationPresent(CommandRestriction.class)) {
                    var annotations = field.getAnnotationsByType(CommandRestriction.class);
                    for (var annotation : annotations) {
                        restrictions.add((AbstractArgumentRestriction<?>)
                                AbstractRestrictionFactory.execute(annotation.restriction(), registry, restrictionsRegistry));
                    }
                }

                var annotation = field.getAnnotation(Command.class);
                StandAloneCommand command;
                try {
                    command = getStandAloneCommand(container, field, annotation, restrictions.toArray(new AbstractArgumentRestriction[]{}), registry, restrictionsRegistry);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                allCommands.put(field.getName(), command);

                if (annotation.aliasOf().isEmpty())
                    commands.put(field.getName(), command);
                else
                    allAliases.put(field.getName(), command.aliasOf());
            }
        }

        for (var pair : allAliases.entrySet()) {
            if (!allCommands.containsKey(pair.getValue()))
                throw new RuntimeException("Alias error: Command " + pair.getValue() + " not found");
            commands.put(pair.getKey(), allCommands.get(pair.getValue()));
        }

        return commands;
    }

    private static @NotNull StandAloneCommand getStandAloneCommand(Object container,
                                                                   Field field,
                                                                   Command annotation,
                                                                   AbstractArgumentRestriction<?>[] restrictions,
                                                                   ArgumentRegistry registry,
                                                                   ArgumentRestrictionRegistry restrictionsRegistry)
            throws IllegalAccessException {
        AbstractCommand<?> cmd;
        try {
            cmd = (AbstractCommand<?>)
                    field.getType()
                    .getDeclaredConstructor(ArgumentRegistry.class, ArgumentRestrictionRegistry.class)
                    .newInstance(registry, restrictionsRegistry);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return new StandAloneCommand() {
            static final DefaultSimpleTabCompleter defaultTabCompleter = new DefaultSimpleTabCompleter();

            @Override
            public String getName() {
                return field.getName();
            }

            @Override
            public String aliasOf() {
                return (annotation.aliasOf().isEmpty() ? null : annotation.aliasOf());
            }

            @SuppressWarnings("unchecked")
            @Override
            public AbstractMessage execute(String[] args, AbstractExecutionContext context) throws ParseError {
                for (var rest : restrictions)
                    ((AbstractArgumentRestriction<Object>)rest).assertRestriction(cmd, context);
                var parsed = cmd.getParser().parseSimple(args, cmd.getArgumentList(), context);
                return ((AbstractCommand<Object>)cmd).execute(parsed, context);
            }

            @Override
            public List<String> tabComplete(String[] args, AbstractExecutionContext context) {
                return defaultTabCompleter.tabComplete(args, context, cmd.getArgumentList());
            }
        };
    }
}
