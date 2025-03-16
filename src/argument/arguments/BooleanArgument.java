package argument.arguments;

import argument.AbstractArgument;
import util.AbstractExecutionContext;
import util.AbstractMessage;
import util.ParseError;

import java.util.List;

public class BooleanArgument extends AbstractArgument.Parametrized<Boolean>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("Boolean value");

    @Override
    public AbstractArgument.ParseMode getParseMode() {
        return AbstractArgument.ParseMode.SpaceTerminated;
    }

    @Override
    public String getPhase() {
        return "boolean";
    }

    @Override
    public String getShortUsage() {
        return "true";
    }

    @Override
    public AbstractMessage getHelp() {
        return help;
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
