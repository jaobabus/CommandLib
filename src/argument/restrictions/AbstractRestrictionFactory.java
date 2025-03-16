package argument.restrictions;

import argument.AbstractArgumentRestriction;
import argument.arguments.ArgumentRegistry;
import argument.arguments.DefaultArguments;
import command.ArgumentBuilder;
import command.CommandArgumentList;
import util.GenericGetter;


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
}
