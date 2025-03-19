package fun.jaobabus.commandlib.argument.restrictions;

import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.arguments.DefaultArguments;
import fun.jaobabus.commandlib.command.ArgumentBuilder;
import fun.jaobabus.commandlib.command.CommandArgumentList;
import fun.jaobabus.commandlib.command.SimpleCommandParser;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.GenericGetter;
import fun.jaobabus.commandlib.util.ParseError;


public interface AbstractRestrictionFactory<ArgumentList>
{
    String getName();

    CommandArgumentList getArgumentList();

    AbstractArgumentRestriction<?> execute(ArgumentList input);


    abstract class Parametrized<ArgumentList> implements AbstractRestrictionFactory<ArgumentList>
    {
        protected final CommandArgumentList argumentList;

        public Parametrized() {
            this(DefaultArguments.getDefaultArgumentsRegistry(), DefaultRestrictions.getDefaultRegistry());
        }

        public Parametrized(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
            this(null, registry, restRegistry);
        }

        public Parametrized(Class<ArgumentList> clazz, ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
            if (clazz == null)
                clazz = GenericGetter.get(getClass());
            var builder = new ArgumentBuilder<ArgumentList>(clazz);
            builder.fillOriginalStream(registry, restRegistry);
            argumentList = builder.build();
        }

        @Override
        public CommandArgumentList getArgumentList() {
            return argumentList;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> AbstractArgumentRestriction<T> execute(String restriction,
                                                     ArgumentRegistry registry,
                                                     ArgumentRestrictionRegistry restrictionsRegistry)
    {
        var restName = restriction.split(" ")[0];
        var factory = (AbstractRestrictionFactory<Object>)restrictionsRegistry.getRestriction(restName);
        if (factory == null)
            throw new RuntimeException("Restriction " + restName + " not found");

        var argParser = new SimpleCommandParser<>();
        Object parsed;

        try {
            var context = new AbstractExecutionContext();
            context.executor = "<ArgumentBuilder>";
            var args = restriction.substring(restName.length() + 1).split(" ");
            parsed = argParser.parseSimple(args, factory.getArgumentList(), context);
        }
        catch (ParseError e) {
            throw new RuntimeException(e);
        }

        return (AbstractArgumentRestriction<T>) factory.execute(parsed);
    }
}
