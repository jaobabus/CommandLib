package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.ArrayList;
import java.util.List;

public class StringArgument<ExecutionContext extends AbstractExecutionContext>
        extends AbstractArgument.Parametrized<String, ExecutionContext>
{
    public static final AbstractMessage help = new AbstractMessage.StringMessage("String value");

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public List<String> tapComplete(String fragment, ExecutionContext context) {
        return new ArrayList<>(List.of());
    }

    @Override
    public String parseSimple(String arg, ExecutionContext context) throws ParseError {
        return arg;
    }

    @Override
    public String dumpSimple(String arg, ExecutionContext context) {
        return arg;
    }
}
