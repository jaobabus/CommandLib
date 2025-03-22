package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.GenericGetter;


public interface AbstractCommand<ArgumentList, ExecutionContext extends AbstractExecutionContext>
{
    CommandArgumentList<ExecutionContext> getArgumentList();

    AbstractMessage execute(ArgumentList input,
                            ExecutionContext context);

    SimpleCommandParser<ArgumentList, ExecutionContext> getParser();

    abstract class Parametrized<ArgumentList, ExecutionContext extends AbstractExecutionContext>
            implements AbstractCommand<ArgumentList, ExecutionContext>
    {
        protected final SimpleCommandParser<ArgumentList, ExecutionContext> defaultParser;
        protected final CommandArgumentList<ExecutionContext> argumentList;

        public Parametrized(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
            this(null, registry, restRegistry);
        }

        public Parametrized(Class<ArgumentList> clazz, ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
            defaultParser = new SimpleCommandParser<>();
            if (clazz == null)
                clazz = GenericGetter.get(getClass());
            var builder = new ArgumentBuilder<ArgumentList, ExecutionContext>(clazz);
            builder.fillOriginalStream(registry, restRegistry);
            argumentList = builder.build();
        }

        @Override
        public SimpleCommandParser<ArgumentList, ExecutionContext> getParser() {
            return defaultParser;
        }

        @Override
        public CommandArgumentList<ExecutionContext> getArgumentList() {
            return argumentList;
        }
    }

}
