package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.GenericGetter;


public interface AbstractCommand<ArgumentList>
{
    CommandArgumentList getArgumentList();

    AbstractMessage execute(ArgumentList input,
                            AbstractExecutionContext context);

    SimpleCommandParser<ArgumentList> getParser();

    abstract class Parametrized<ArgumentList> implements AbstractCommand<ArgumentList>
    {
        protected final SimpleCommandParser<ArgumentList> defaultParser;
        protected final CommandArgumentList argumentList;

        public Parametrized(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
            this(null, registry, restRegistry);
        }

        public Parametrized(Class<ArgumentList> clazz, ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
            defaultParser = new SimpleCommandParser<>();
            if (clazz == null)
                clazz = GenericGetter.get(getClass());
            argumentList = ArgumentBuilder.build(clazz, registry, restRegistry);
        }

        @Override
        public SimpleCommandParser<ArgumentList> getParser() {
            return defaultParser;
        }

        @Override
        public CommandArgumentList getArgumentList() {
            return argumentList;
        }
    }

}
