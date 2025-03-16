package argument.arguments;

import argument.AbstractArgument;
import util.AbstractExecutionContext;
import util.AbstractMessage;
import util.ParseError;

import java.util.List;

public class FloatArgument extends AbstractArgument.Parametrized<Integer>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("64 bit integer value");

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public String getPhase() {
        return "integer";
    }

    @Override
    public String getShortUsage() {
        return "123";
    }

    @Override
    public AbstractMessage getHelp() {
        return help;
    }

    @Override
    public List<String> tapComplete(String fragment, AbstractExecutionContext context) {
        return List.of("0");
    }

    @Override
    public Integer parseSimple(String arg, AbstractExecutionContext context) throws ParseError {
        try {
            if (arg.startsWith("0x") || arg.startsWith("0X")) {
                return Integer.parseInt(arg.substring(2), 16);
            }
            else if (arg.startsWith("0b") || arg.startsWith("0B")) {
                return Integer.parseInt(arg.substring(2), 2);
            }
            else if (arg.startsWith("0")) {
                return Integer.parseInt(arg.substring(1), 8);
            }
            else {
                return Integer.parseInt(arg);
            }
        }
        catch (NumberFormatException e) {
            throw new ParseError(new AbstractMessage.StringMessage(e.toString()));
        }
    }
}
