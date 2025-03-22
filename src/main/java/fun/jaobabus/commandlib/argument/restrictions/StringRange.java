package fun.jaobabus.commandlib.argument.restrictions;

import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class StringRange extends AbstractRestrictionFactory.Parametrized<String, StringRange.Arguments>
{
    @Override
    public AbstractArgumentRestriction<String> execute(StringRange.Arguments input, String path) {
        var possible = new HashSet<>(Arrays.asList(input.possible));
        return new AbstractArgumentRestriction.Parametrized<>() {
            @Override
            public String getName() {
                return "StringRange";
            }

            @Override
            public String getPath() {
                return path;
            }

            @Override
            public boolean checkRestriction(String value, AbstractExecutionContext context) {
                return possible.contains(value);
            }

            @Override
            public String formatRestriction(String value, AbstractExecutionContext context) {
                return String.format("%s in [%s]", value, String.join(", ", input.possible));
            }

            @Override
            public void processTabComplete(String source, List<String> list, AbstractExecutionContext abstractExecutionContext) {
                list.clear();
                list.addAll(possible.stream().filter(entry -> entry.startsWith(source)).toList());
            }
        };
    }

    public static class Arguments {
        @Argument(action = Argument.Action.VarArg)
        public String[] possible;
    }

}
