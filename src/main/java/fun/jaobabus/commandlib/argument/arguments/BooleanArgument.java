package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.ArrayList;
import java.util.List;

public class BooleanArgument<ExecutionContext extends AbstractExecutionContext>
        extends AbstractArgument.Parametrized<Boolean, ExecutionContext>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("Boolean value");

    @Override
    public AbstractArgument.ParseMode getParseMode() {
        return AbstractArgument.ParseMode.SpaceTerminated;
    }

    @Override
    public List<Boolean> tapComplete(String fragment, ExecutionContext context) {
        return new ArrayList<>(List.of(true, false));
    }

    @Override
    public Boolean parseSimple(String arg, ExecutionContext context) throws ParseError {
        try {
            if (arg.equals("0") || arg.equals("1")) {
                return Integer.parseInt(arg) == 1;
            }
            else {
                return Boolean.parseBoolean(arg);
            }
        }
        catch (NumberFormatException e) {
            throw new ParseError(new AbstractMessage.StringMessage(e.toString()));
        }
    }

    @Override
    public String dumpSimple(Boolean arg, ExecutionContext context) {
        return arg.toString();
    }
}
