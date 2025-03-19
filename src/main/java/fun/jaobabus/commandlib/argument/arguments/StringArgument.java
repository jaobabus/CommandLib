package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.List;

public class StringArgument extends AbstractArgument.Parametrized<String>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("String value");

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public List<String> tapComplete(String fragment, AbstractExecutionContext context) {
        return List.of();
    }

    @Override
    public String parseSimple(String arg, AbstractExecutionContext context) throws ParseError {
        return arg;
    }
}
