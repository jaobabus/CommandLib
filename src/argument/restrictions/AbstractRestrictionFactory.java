package argument.restrictions;

import argument.AbstractArgumentRestriction;
import argument.ArgumentRestriction;
import argument.arguments.ArgumentRegistry;
import argument.arguments.DefaultArguments;
import command.ArgumentBuilder;
import command.CommandArgumentList;
import command.SimpleCommandParser;
import util.AbstractExecutionContext;
import util.GenericGetter;
import util.ParseError;


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
            argumentList = ArgumentBuilder.build(clazz, registry, restRegistry);
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
