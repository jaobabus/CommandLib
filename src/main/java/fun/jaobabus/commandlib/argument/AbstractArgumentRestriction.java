package fun.jaobabus.commandlib.argument;

import fun.jaobabus.commandlib.argument.restrictions.RestrictionError;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;


public interface AbstractArgumentRestriction<ArgumentType>
{
    boolean checkRestriction(ArgumentType argument, AbstractExecutionContext context);
    void assertRestriction(ArgumentType argument, AbstractExecutionContext context) throws RestrictionError;
    String formatRestriction(ArgumentType argument, AbstractExecutionContext context);

    abstract class Parametrized<T> implements AbstractArgumentRestriction<T>
    {
        @Override
        public void assertRestriction(T argument, AbstractExecutionContext context) throws RestrictionError {
            if (!checkRestriction(argument, context))
                throw new RestrictionError(new AbstractMessage.StringMessage("Restriction '" + formatRestriction(argument, context) + "' failed"));
        }
    }

    class Empty implements AbstractArgumentRestriction<Object>
    {
        @Override
        public boolean checkRestriction(Object argument, AbstractExecutionContext context) { return true; }

        @Override
        public void assertRestriction(Object argument, AbstractExecutionContext context) throws RestrictionError {}

        @Override
        public String formatRestriction(Object argument, AbstractExecutionContext context) { return "true"; }
    }
}
