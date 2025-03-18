package command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import argument.AbstractArgument;
import argument.Flag;
import util.AbstractExecutionContext;


public class DefaultSimpleTabCompleter
{
    private record TabCompleteParsedArgumentResult(
            String source,
            AbstractArgument<?> result,
            boolean success,
            boolean isArgument
    ) {}

    private static class TabCompleteData {
        public int argumentOnlyTokenAt = 2147483647;
        public ArrayList<TabCompleteParsedArgumentResult> parsed = new ArrayList<>();
    }

    public List<String> tabComplete(String[] args, AbstractExecutionContext context, CommandArgumentList arguments)
    {
        var cached = (TabCompleteData)context.getSTCacheFor(this, new TabCompleteData());
        return getTabComplete(args, context, cached, arguments);
    }

    private List<String> getTabComplete(String[] args,
                                        AbstractExecutionContext context,
                                        TabCompleteData storage,
                                        CommandArgumentList arguments)
    {
        int argumentIndex = 0;
        for (int i = 0; i < args.length - 1; i++) {
            TabCompleteParsedArgumentResult parsed;
            if (storage.parsed.size() < i) {
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
                                       AbstractExecutionContext context,
                                       CommandArgumentList arguments,
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
                    var flag = arguments.flags.get(String.valueOf(source.charAt(i)));
                    if (flag.annotation().action().equals(Flag.Action.StoreValue)) {
                        for (var comp : flag.argument().tapComplete(source.substring(i), context)) {
                            complete.add(source.substring(0, i) + comp);
                        }
                        break;
                    }
                    else {
                        usedFlags.add(String.valueOf(source.charAt(i)));
                    }
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
            if (arguments.arguments.size() > argumentIndex)
                return arguments.arguments.get(argumentIndex).argument().tapComplete(source, context);
            return null;
        }
    }

    private TabCompleteParsedArgumentResult newParsedResult(String source,
                                                            AbstractExecutionContext context,
                                                            TabCompleteData storage,
                                                            CommandArgumentList arguments,
                                                            int argumentIndex,
                                                            int parsedIndex)
    {
        if (source.isEmpty())
            return new TabCompleteParsedArgumentResult(source, null, true, false);
        if (source.charAt(0) == '-') {
            if (source.length() == 1)
                return new TabCompleteParsedArgumentResult(source, null, false, false);
            if (source.charAt(1) == '-') {
                storage.argumentOnlyTokenAt = parsedIndex;
                return new TabCompleteParsedArgumentResult(source, null, true, false);
            }
            else if (arguments.flags.containsKey(String.valueOf(source.charAt(1)))
                    && storage.argumentOnlyTokenAt > parsedIndex) {
                return new TabCompleteParsedArgumentResult(source, arguments.flags.get(String.valueOf(source.charAt(1))).argument(), true, false);
            }
        }
        return new TabCompleteParsedArgumentResult(source, arguments.arguments.get(argumentIndex).argument(), true, true);
    }
}
