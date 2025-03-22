package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.commandlib.context.DummyArgumentContext;

import java.util.ArrayList;
import java.util.List;

public class IntegerArgument
        extends AbstractArgument.Parametrized<Long, DummyArgumentContext>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("64 bit integer value");

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public List<Long> tapComplete(String fragment, DummyArgumentContext context) {
        return new ArrayList<>(List.of(0L));
    }

    @Override
    public Long parseSimple(String arg, DummyArgumentContext context) throws ParseError {
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

    @Override
    public String dumpSimple(Long arg, DummyArgumentContext context) {
        return String.valueOf(arg);
    }
}
