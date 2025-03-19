package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.List;

public class BooleanArgument extends AbstractArgument.Parametrized<Boolean>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("Boolean value");

    @Override
    public AbstractArgument.ParseMode getParseMode() {
        return AbstractArgument.ParseMode.SpaceTerminated;
    }

    @Override
    public List<String> tapComplete(String fragment, AbstractExecutionContext context) {
        return List.of("true", "false");
    }

    @Override
    public Boolean parseSimple(String arg, AbstractExecutionContext context) throws ParseError {
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
}
