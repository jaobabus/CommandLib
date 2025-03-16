package argument.arguments;

import argument.AbstractArgument;
import util.AbstractExecutionContext;
import util.AbstractMessage;
import util.ParseError;

import java.util.List;

public class StringArgument extends AbstractArgument.Parametrized<String>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("String value");

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public String getPhase() {
        return "String";
    }

    @Override
    public String getShortUsage() {
        return "text";
    }

    @Override
    public AbstractMessage getHelp() {
        return help;
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
