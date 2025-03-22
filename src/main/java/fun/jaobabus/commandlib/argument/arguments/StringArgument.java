package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.context.DummyArgumentContext;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.ArrayList;
import java.util.List;

public class StringArgument
        extends AbstractArgument.Parametrized<String, DummyArgumentContext>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("String value");

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public List<String> tapComplete(String fragment, DummyArgumentContext context) {
        return new ArrayList<>(List.of());
    }

    @Override
    public String parseSimple(String arg, DummyArgumentContext context) throws ParseError {
        return arg;
    }

    @Override
    public String dumpSimple(String arg, DummyArgumentContext context) {
        return arg;
    }
}
