package command;

import argument.AbstractArgumentRestriction;
import argument.Flag;
import util.AbstractExecutionContext;
import util.AbstractMessage;
import util.ParseError;

import java.util.ArrayList;
import java.util.List;


public class SimpleCommandParser<ArgumentList> implements AbstractCommandParser<ArgumentList>
{
    @Override
    @SuppressWarnings("unchecked")
    public ArgumentList parseSimple(String[] args, CommandArgumentList arguments, AbstractExecutionContext context) throws ParseError {
        ArgumentList argList = (ArgumentList) arguments.newInstance();

        boolean allowFlags = true;
        CommandArgumentList.ArgPair nextArgument = null;
        List<Object> varargs = new ArrayList<>();
        try {
            var argIterator = arguments.arguments.iterator();
            Class<ArgumentList> clazz = (Class<ArgumentList>) arguments.getType();
            for (var arg : args) {
                if (arg.equals("--")) {
                    allowFlags = false;
                }
                else if (allowFlags && arg.startsWith("-") && !arguments.flags.isEmpty()) {
                    for (int index = 1; index < arg.length(); index++) {
                        var name = String.valueOf(arg.charAt(index));
                        var flag = arguments.flags.get(name);
                        if (flag == null) {
                            if (index != 1)
                                throw new ParseError(new AbstractMessage.StringMessage("Can't find flag '" + name + "'"));
                            break;
                        }

                        if (flag.annotation().action().equals(Flag.Action.StoreTrue)) {
                            var field = clazz.getField(name);
                            field.set(argList, true);
                        }
                        else if (flag.annotation().action().equals(Flag.Action.StoreValue)) {
                            var field = clazz.getField(name);
                            var value = arg.substring(index + 1);
                            var result = flag.argument().parseSimple(value, context);
                            field.set(argList, result);
                            arg = "";
                            break;
                        }
                        else
                            throw new ParseError(new AbstractMessage.StringMessage("Internal flag store error"));
                    }
                    if (arg.isEmpty())
                        continue;
                }

                if (nextArgument == null || !nextArgument.annotation().vararg()) {
                    if (!argIterator.hasNext())
                        throw new ParseError(new AbstractMessage.StringMessage("Unexpected argument '" + arg + "'"));
                    nextArgument = argIterator.next();
                }

                var result = nextArgument.argument().parseSimple(arg, context);
                var field = clazz.getField(nextArgument.name());

                if (nextArgument.restrictions() != null)
                    for (var rest : nextArgument.restrictions())
                        ((AbstractArgumentRestriction<Object>)rest).assertRestriction(result, context);

                if (nextArgument.annotation().vararg()) {
                    varargs.add(result);
                } else {
                    field.setAccessible(true);
                    field.set(argList, result);
                }
            }

            if (argIterator.hasNext()) {
                nextArgument = argIterator.next();
                if (!nextArgument.annotation().optional() && !nextArgument.annotation().vararg())
                    throw new ParseError(new AbstractMessage.StringMessage("Expected argument " + nextArgument.name()));
            }

            if (nextArgument != null && nextArgument.annotation().vararg()) {
                var field = clazz.getField(nextArgument.name());
                field.setAccessible(true);
                var original = java.lang.reflect.Array.newInstance(field.getType().getComponentType(), 0);
                field.set(argList, varargs.toArray((Object[])original));
            }
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ParseError(new AbstractMessage.StringMessage("Internal error for " + nextArgument + ": " + e));
        }

        return argList;
    }

    @Override
    public List<String> tabCompleteSimple(String[] args, CommandArgumentList arguments, AbstractExecutionContext context) {
        return List.of();
    }
}
