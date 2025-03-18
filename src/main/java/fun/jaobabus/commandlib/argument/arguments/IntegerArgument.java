package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.List;

public class IntegerArgument extends AbstractArgument.Parametrized<Long>
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
    public Long parseSimple(String arg, AbstractExecutionContext context) throws ParseError {
        try {
            long sign = 1L;
            if (arg.startsWith("-")) {
                sign = -1L;
                arg = arg.substring(1);
            }
            if (arg.startsWith("0x") || arg.startsWith("0X")) {
                return Long.parseLong(arg.substring(2), 16) * sign;
            }
            else if (arg.startsWith("0b") || arg.startsWith("0B")) {
                return Long.parseLong(arg.substring(2), 2) * sign;
            }
            else if (arg.startsWith("0") && !arg.equals("0")) {
                return Long.parseLong(arg.substring(1), 8) * sign;
            }
            else {
                return Long.parseLong(arg) * sign;
            }
        }
        catch (NumberFormatException e) {
            throw new ParseError(new AbstractMessage.StringMessage(e.toString()));
        }
    }
}
