package fun.jaobabus.commandlib.argument;

import fun.jaobabus.commandlib.argument.restrictions.RestrictionError;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.GenericGetter;
import lombok.Getter;

import java.util.List;


public interface AbstractArgumentRestriction<ArgumentType>
{
    String getName();
    String getPath();
    boolean checkRestriction(ArgumentType argument, AbstractExecutionContext context);
    void assertRestriction(ArgumentType argument, AbstractExecutionContext context) throws RestrictionError;
    String formatRestriction(ArgumentType argument, AbstractExecutionContext context);
    void processTabComplete(String source, List<ArgumentType> complete, AbstractExecutionContext context);
    Class<?> getType();

    @Getter
    abstract class Parametrized<T> implements AbstractArgumentRestriction<T>
    {
        Class<?> type;

        public Parametrized()
        {
            type = GenericGetter.get(this.getClass());
        }

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
        public String getPath() { return ""; }

        @Override
        public boolean checkRestriction(Object argument, AbstractExecutionContext context) { return true; }

        @Override
        public void assertRestriction(Object argument, AbstractExecutionContext context) throws RestrictionError {}

        public String formatRestriction(Object argument, AbstractExecutionContext context) { return "true"; }

        @Override
        public void processTabComplete(String source, List<Object> complete, AbstractExecutionContext context) {}

        @Override
        public Class<?> getType() { return Object.class; }
    }
}
