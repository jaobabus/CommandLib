package fun.jaobabus.commandlib.argument;

import fun.jaobabus.commandlib.argument.restrictions.RestrictionError;
import fun.jaobabus.commandlib.command.Command;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;

import java.util.List;


public interface AbstractArgumentRestriction<ArgumentType>
{
    String getName();
    boolean checkRestriction(ArgumentType argument, AbstractExecutionContext context);
    void assertRestriction(ArgumentType argument, AbstractExecutionContext context) throws RestrictionError;
    String formatRestriction(ArgumentType argument, AbstractExecutionContext context);
    void processTabComplete(String source, List<ArgumentType> complete, AbstractExecutionContext context);

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
        public String getName() { return "Empty"; }

        @Override
        public boolean checkRestriction(Object argument, AbstractExecutionContext context) { return true; }

        @Override
        public void assertRestriction(Object argument, AbstractExecutionContext context) throws RestrictionError {}

        public String formatRestriction(Object argument, AbstractExecutionContext context) { return "true"; }

        @Override
        public void processTabComplete(String source, List<Object> complete, AbstractExecutionContext context) {}
    }
}
