package command;

import argument.AbstractArgument;
import argument.AbstractArgumentRestriction;
import argument.Argument;
import argument.Flag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class CommandArgumentList
{
    public record FlagPair (AbstractArgument<?> argument,
                            Flag annotation,
                            AbstractArgumentRestriction<?>[] restrictions) {}

    public record ArgPair (String name,
                           AbstractArgument<?> argument,
                           Argument annotation,
                           AbstractArgumentRestriction<?>[] restrictions) {}

    public final Map<String, FlagPair> flags = new HashMap<String, FlagPair>();
    public final List<ArgPair> arguments = new ArrayList<>();

    public abstract Object newInstance();
    public abstract Class<?> getType();

}
