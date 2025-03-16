package argument.restrictions;

import argument.AbstractArgumentRestriction;
import argument.Argument;
import util.AbstractExecutionContext;


public class IntRange extends AbstractRestrictionFactory.Parametrized<IntRange.Arguments>
{
    @Override
    public String getName() {
        return "IntRange";
    }

    @Override
    public AbstractArgumentRestriction<?> execute(Arguments input) {
        return new AbstractArgumentRestriction.Parametrized<Long>() {
            @Override
            public boolean checkRestriction(Long value, AbstractExecutionContext context) {
                return input.start <= value && value <= input.end;
            }

            @Override
            public String formatRestriction(Long value, AbstractExecutionContext context) {
                return String.format("%d <= %d <= %d", input.start, value, input.end);
            }
        };
    }

    public static class Arguments {
        @Argument
        public Long start;

        @Argument
        public Long end;
    }

}
