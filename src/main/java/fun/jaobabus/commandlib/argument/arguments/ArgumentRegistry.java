package fun.jaobabus.commandlib.argument.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.context.BaseArgumentContext;

import java.util.HashMap;
import java.util.Map;


public class ArgumentRegistry
{
    private final Map<String, AbstractArgument<?, ?>> argumentsMap;

    public ArgumentRegistry()
    {
        argumentsMap = new HashMap<>();
    }

    public void include(ArgumentRegistry other)
    {
        for (var key : other.argumentsMap.keySet()) {
            if (argumentsMap.containsKey(key))
                throw new RuntimeException(key + " already defined");
            argumentsMap.put(key, other.argumentsMap.get(key));
        }
    }

    public void putArgument(AbstractArgument<?, ?> argument)
    {
        argumentsMap.put(argument.getArgumentClass().getName(), argument);
    }

    @SuppressWarnings("unchecked")
    public <T, AC extends BaseArgumentContext> AbstractArgument<T, AC> getArgument(Class<T> clazz)
    {
        var arg = argumentsMap.get(clazz.getName());
        return (AbstractArgument<T, AC>)arg;
    }
}
