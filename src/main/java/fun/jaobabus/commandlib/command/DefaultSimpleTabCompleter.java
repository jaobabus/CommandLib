package fun.jaobabus.commandlib.command;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentDescriptor;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DefaultSimpleTabCompleter<ExecutionContext extends AbstractExecutionContext>
{
    private record TabCompleteParsedArgumentResult<EC extends AbstractExecutionContext> (
            String source,
            AbstractArgument<?, EC> result,
            boolean success,
            boolean isArgument
    ) {}

    private static class TabCompleteData<EC extends AbstractExecutionContext> {
        public int argumentOnlyTokenAt = 2147483647;
        public ArrayList<TabCompleteParsedArgumentResult<EC>> parsed = new ArrayList<>();
    }

    public List<String> tabComplete(String[] args, ExecutionContext context, CommandArgumentList<ExecutionContext> arguments)
    {
        @SuppressWarnings("unchecked")
        var cached = (TabCompleteData<ExecutionContext>)context.getSTCacheFor(this, new TabCompleteData<ExecutionContext>());
        return getTabComplete(args, context, cached, arguments);
    }

    private List<String> getTabComplete(String[] args,
                                        ExecutionContext context,
                                        TabCompleteData<ExecutionContext> storage,
                                        CommandArgumentList<ExecutionContext> arguments)
    {
        int argumentIndex = 0;
        for (int i = 0; i < args.length - 1; i++) {
            TabCompleteParsedArgumentResult<ExecutionContext> parsed;
            if (storage.parsed.size() <= i) {
                parsed = newParsedResult(args[i], context, storage, arguments, argumentIndex, i);
                storage.parsed.add(parsed);
            }
            else if (!storage.parsed.get(i).source.equals(args[i])) {
                parsed = newParsedResult(args[i], context, storage, arguments, argumentIndex, i);
                storage.parsed.set(i, parsed);
            }
            else {
                parsed = storage.parsed.get(i);
            }
            if (parsed.isArgument)
                argumentIndex++;
        }
        return getTabCompleteForLast(args[args.length - 1], context, arguments, argumentIndex);
    }

    List<String> getTabCompleteForLast(String source,
                                       ExecutionContext context,
                                       CommandArgumentList<ExecutionContext> arguments,
                                       int argumentIndex)
    {
        if (!source.isEmpty() && source.charAt(0) == '-') {
            List<String> complete = new ArrayList<>();
            if (source.length() == 2 && source.charAt(1) == '-') {
                complete.add("--");
            }
            else {
                Set<String> usedFlags = new HashSet<>();
                for (int i = 1; i < source.length(); i++) {
                    if (!arguments.flags.containsKey(String.valueOf(source.charAt(i)))) {
                        return null;
                    }
                    if (processFlag(complete, source, i, usedFlags, arguments.flags.get(String.valueOf(source.charAt(i))), context))
                        break;
                }
                for (var flagName : arguments.flags.keySet()) {
                    if (usedFlags.contains(flagName))
                        continue;
                    complete.add(source + flagName);
                }
            }
            if (complete.isEmpty())
                complete.add(source);
            return complete;
        }
        else {
            if (arguments.arguments.size() > argumentIndex) {
                return processArgument(arguments.arguments.get(argumentIndex), source, context);
            }
            return null;
        }
    }

    private <T> boolean processFlag(List<String> complete, String source, int i, Set<String> usedFlags,
                                    ArgumentDescriptor<T, ExecutionContext> flag, ExecutionContext context)
    {
        if (flag.action.equals(Argument.Action.FlagStoreValue)) {
            for (var comp : completeArgument(flag.argument, source.substring(i), flag.restrictions, context)) {
                complete.add(source.substring(0, i) + comp);
            }
            return true;
        }
        else {
            usedFlags.add(String.valueOf(source.charAt(i)));
        }
        return false;
    }

    private <T> List<String> processArgument(ArgumentDescriptor<T, ExecutionContext> argument, String source, ExecutionContext context)
    {
        return completeArgument(argument.argument, source, argument.restrictions, context);
    }

    private <T> List<String> completeArgument(AbstractArgument<T, ExecutionContext> argument,
                                              String source,
                                              List<AbstractArgumentRestriction<T>> restrictions,
                                              ExecutionContext context)
    {
        var completes = argument
                            .tapComplete(source, context);
        for (var rest : restrictions)
            rest.processTabComplete(source, completes, context);
        return new ArrayList<>(completes
                .stream()
                .map(complete -> argument.dumpSimple(complete, context))
                .toList());
    }

    private TabCompleteParsedArgumentResult<ExecutionContext> newParsedResult(String source,
                                                                              ExecutionContext context,
                                                                              TabCompleteData<ExecutionContext> storage,
                                                                              CommandArgumentList<ExecutionContext> arguments,
                                                                              int argumentIndex,
                                                                              int parsedIndex)
    {
        if (source.isEmpty())
            return new TabCompleteParsedArgumentResult<>(source, null, true, false);
        if (source.charAt(0) == '-') {
            if (source.length() == 1)
                return new TabCompleteParsedArgumentResult<>(source, null, false, false);
            if (source.charAt(1) == '-') {
                storage.argumentOnlyTokenAt = parsedIndex;
                return new TabCompleteParsedArgumentResult<>(source, null, true, false);
            }
            else if (arguments.flags.containsKey(String.valueOf(source.charAt(1)))
                    && storage.argumentOnlyTokenAt > parsedIndex) {
                try{
                    var arg = arguments.flags.get(String.valueOf(source.charAt(1)));
                    assertRestrictions(arg, source.substring(2), context);
                    return new TabCompleteParsedArgumentResult<>(source, arg.argument, true, false);
                } catch (ParseError e) {
                    return new TabCompleteParsedArgumentResult<>(source, null, false, false);
                }
            }
            else if (argumentIndex >= arguments.arguments.size()) {
                return new TabCompleteParsedArgumentResult<>(source, null, false, false);
            }
        }
        else if (argumentIndex >= arguments.arguments.size()) {
            return new TabCompleteParsedArgumentResult<>(source, null, false, true);
        }

        try {
            var arg = arguments.arguments.get(argumentIndex);
            assertRestrictions(arg, source, context);
            return new TabCompleteParsedArgumentResult<>(source, arg.argument, true, true);
        } catch (ParseError e) {
            return new TabCompleteParsedArgumentResult<>(source, null, false, true);
        }
    }

    private <T> void assertRestrictions(ArgumentDescriptor<T, ExecutionContext> argument,
                                       String source,
                                       ExecutionContext context)
            throws ParseError
    {
        for (var rest : argument.restrictions) {
            var parsed = argument.argument.parseSimple(source, context);
            rest.assertRestriction(parsed, context);
        }
    }
}
