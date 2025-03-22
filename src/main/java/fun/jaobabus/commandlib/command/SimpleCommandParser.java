package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentDescriptor;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SimpleCommandParser<ArgumentList, ExecutionContext extends AbstractExecutionContext>
        implements AbstractCommandParser<ArgumentList, ExecutionContext>
{
    @Override
    @SuppressWarnings("unchecked")
    public ArgumentList parseSimple(String[] args, CommandArgumentList<ExecutionContext> arguments, ExecutionContext context) throws ParseError {
        ArgumentList argList = (ArgumentList) arguments.newInstance();

        boolean allowFlags = true;
        ArgumentDescriptor<?, ExecutionContext> nextArgument = null;
        List<Object> varargs = new ArrayList<>();
        Set<String> usedFlags = new HashSet<>();
        try {
            var argIterator = arguments.arguments.iterator();
            Class<ArgumentList> clazz = (Class<ArgumentList>) arguments.getType();

            for (var flagKey : arguments.flags.keySet()) {
                var flag = arguments.flags.get(flagKey);
                if (flag.action.equals(Argument.Action.FlagStoreTrue)) {
                    var field = clazz.getField(flagKey);
                    field.set(argList, false);
                }
            }

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

                        if (flag.action.equals(Argument.Action.FlagStoreTrue)) {
                            var field = clazz.getField(name);
                            field.set(argList, true);
                        }
                        else if (flag.action.equals(Argument.Action.FlagStoreValue)) {
                            var field = clazz.getField(name);
                            var value = arg.substring(index + 1);
                            var result = flag.argument.parseSimple(value, context);
                            field.set(argList, result);
                            arg = "";
                            break;
                        }
                        else
                            throw new ParseError(new AbstractMessage.StringMessage("Internal flag store error"));
                        usedFlags.add(name);
                    }
                    if (arg.isEmpty())
                        continue;
                }

                if (nextArgument == null || !nextArgument.action.equals(Argument.Action.VarArg)) {
                    if (!argIterator.hasNext())
                        throw new ParseError(new AbstractMessage.StringMessage("Unexpected argument '" + arg + "'"));
                    nextArgument = argIterator.next();
                }

                var result = nextArgument.argument.parseSimple(arg, context);
                var field = clazz.getField(nextArgument.name);

                if (!nextArgument.restrictions.isEmpty())
                    for (var rest : nextArgument.restrictions)
                        ((AbstractArgumentRestriction<Object>)rest).assertRestriction(result, context);

                if (nextArgument.action.equals(Argument.Action.VarArg)) {
                    varargs.add(result);
                } else {
                    field.setAccessible(true);
                    field.set(argList, result);
                }
            }

            while (argIterator.hasNext()) {
                nextArgument = argIterator.next();
                var field = clazz.getField(nextArgument.name);
                if (nextArgument.action == Argument.Action.Optional) {
                    if (!nextArgument.defaultValue.isEmpty()) {
                        var result = nextArgument.argument.parseSimple(nextArgument.defaultValue, context);
                        field.setAccessible(true);
                        field.set(argList, result);
                    }
                }
                else if (nextArgument.action == Argument.Action.VarArg) {
                    if (!nextArgument.defaultValue.isEmpty()) {
                        String[] defaultArgs = nextArgument.defaultValue.split(" ");
                        List<Object> defaultVarargs = new ArrayList<>(defaultArgs.length);
                        for (var arg : defaultArgs)
                            defaultVarargs.add(nextArgument.argument.parseSimple(arg, context));
                        field.setAccessible(true);
                        field.set(argList, defaultVarargs);
                    }
                }
                else
                    throw new ParseError(new AbstractMessage.StringMessage("Expected argument " + nextArgument.name));
            }

            if (nextArgument != null && nextArgument.action == Argument.Action.VarArg) {
                var field = clazz.getField(nextArgument.name);
                field.setAccessible(true);
                var original = java.lang.reflect.Array.newInstance(field.getType().getComponentType(), 0);
                field.set(argList, varargs.toArray((Object[])original));
            }

            for (var flagKey : arguments.flags.keySet()) {
                if (usedFlags.contains(flagKey))
                    continue;
                var flag = arguments.flags.get(flagKey);
                var field = clazz.getField(flagKey);
                var result = flag.argument.parseSimple(flag.defaultValue, context);
                field.setAccessible(true);
                field.set(argList, result);
            }
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ParseError(new AbstractMessage.StringMessage("Internal error for " + nextArgument + ": " + e));
        }

        return argList;
    }

    @Override
    public List<String> tabCompleteSimple(String[] args, CommandArgumentList<ExecutionContext> arguments, ExecutionContext context) {
        return List.of();
    }
}
