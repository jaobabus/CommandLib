package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.context.DummyArgumentContext;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.ArrayList;
import java.util.List;

public class BooleanArgument
        extends AbstractArgument.Parametrized<Boolean, DummyArgumentContext>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("Boolean value");

    @Override
    public AbstractArgument.ParseMode getParseMode() {
        return AbstractArgument.ParseMode.SpaceTerminated;
    }

    @Override
    public List<Boolean> tapComplete(String fragment, DummyArgumentContext context) {
        return new ArrayList<>(List.of(true, false));
    }

    @Override
    public Boolean parseSimple(String arg, DummyArgumentContext context) throws ParseError {
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
    public String dumpSimple(Boolean arg, DummyArgumentContext context) {
        return arg.toString();
    }
}
