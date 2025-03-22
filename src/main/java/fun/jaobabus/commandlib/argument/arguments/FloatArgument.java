package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.context.DummyArgumentContext;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.ArrayList;
import java.util.List;

public class FloatArgument
        extends AbstractArgument.Parametrized<Double, DummyArgumentContext>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("64 bit float value");

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public List<Double> tapComplete(String fragment, DummyArgumentContext context) {
        return new ArrayList<>(List.of(0d));
    }

    @Override
    public Double parseSimple(String arg, DummyArgumentContext context) throws ParseError {
        try {
            return Double.parseDouble(arg);
        }
        catch (NumberFormatException e) {
            throw new ParseError(new AbstractMessage.StringMessage(e.toString()));
        }
    }

    @Override
    public String dumpSimple(Double arg, DummyArgumentContext context) {
        return arg.toString();
    }
}
