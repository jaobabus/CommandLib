package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentDescriptor;
import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.*;


public class SimpleCommandParser<ArgumentList, ExecutionContext extends AbstractExecutionContext>
        implements AbstractCommandParser<ArgumentList, ExecutionContext>
{
    @Override
    @SuppressWarnings("unchecked")
    public ArgumentList parseSimple(String[] args, CommandArgumentList arguments, ExecutionContext context) throws ParseError {
        arguments.initContext(context);
        ArgumentList argList = (ArgumentList) arguments.newInstance();

        boolean allowFlags = true;
        ArgumentDescriptor<?, ?> nextArgument = null;
        List<Object> varargs = new ArrayList<>();
        Set<String> usedFlags = new HashSet<>();
        Map<String, List<Object>> flagsVarargs = new HashMap<>();
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
                            var result = parseArgument(flag, value, context);
                            field.set(argList, result);
                            arg = "";
                            usedFlags.add(name);
                            break;
                        }
                        else if (flag.action.equals(Argument.Action.FlagAppendValue)) {
                            var value = arg.substring(index + 1);
                            var result = parseArgument(flag, value, context);
                            flagsVarargs.computeIfAbsent(name, p -> new ArrayList<>()).add(result);
                            arg = "";
                            usedFlags.add(name);
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

                var result = parseArgument(nextArgument, arg, context);
                var field = clazz.getField(nextArgument.name);

                if (!nextArgument.restrictions.isEmpty())
                    for (var anyRest : nextArgument.restrictions) {
                        var rest = ((AbstractArgumentRestriction<Object>) anyRest);
                        var target = getTarget(result, rest.getPath());
                        if (target.getClass().isArray()
                                && target.getClass().getComponentType().isAssignableFrom(rest.getType())) {
                            for (var item : (Object[])target)
                                rest.assertRestriction(item, context);
                        }
                        else
                            rest.assertRestriction(target, context);
                    }

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
                        var result = parseArgument(nextArgument, nextArgument.defaultValue, context);
                        field.setAccessible(true);
                        field.set(argList, result);
                    }
                }
                else if (nextArgument.action == Argument.Action.VarArg) {
                    if (!nextArgument.defaultValue.isEmpty()) {
                        String[] defaultArgs = nextArgument.defaultValue.split(" ");
                        List<Object> defaultVarargs = new ArrayList<>(defaultArgs.length);
                        for (var arg : defaultArgs)
                            defaultVarargs.add(parseArgument(nextArgument, arg, context));
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
                var result = parseArgument(flag, flag.defaultValue, context);
                field.setAccessible(true);
                if (field.get(argList) == null)
                    field.set(argList, result);
            }

            for (var key : flagsVarargs.keySet()) {
                var field = clazz.getField(key);
                var vararg = flagsVarargs.get(key);
                field.setAccessible(true);
                var original = java.lang.reflect.Array.newInstance(field.getType().getComponentType(), 0);
                field.set(argList, vararg.toArray((Object[])original));
            }
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ParseError(new AbstractMessage.StringMessage(
                    "Internal error for "
                            + nextArgument + (nextArgument != null ? ":(" + nextArgument.name + ")" : "") + ": "
                            + e + "\n"
                            + String.join(", ", Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList())));
        }

        return argList;
    }

    private <T, AC extends BaseArgumentContext>
    T parseArgument(ArgumentDescriptor<T, AC> argument, String stringValue, ExecutionContext ec)
            throws ParseError
    {
        return argument.processor.parseWithContext(stringValue, ec);
    }

    @Override
    public List<String> tabCompleteSimple(String[] args, CommandArgumentList arguments, ExecutionContext context) {
        return List.of();
    }

    private Object getTarget(Object instance, String targetPath)
    {
        if (targetPath.isEmpty())
            return instance;
        var r = targetPath.split("\\.", 2);
        var targetName = r[0];
        var nextPath = (r.length > 1 ? r[1] : null);
        try {
            var field = instance.getClass().getDeclaredField(targetName);
            field.setAccessible(true);
            if (nextPath != null)
                return getTarget(field.get(instance), nextPath);
            else
                return field.get(instance);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
