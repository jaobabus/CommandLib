package argument.arguments;

import argument.AbstractArgument;

import java.util.HashMap;
import java.util.Map;


public class ArgumentRegistry
{
    private final Map<String, AbstractArgument<?>> argumentsMap;

    public ArgumentRegistry()
    {
        argumentsMap = new HashMap<>();
    }

    public void putArgument(AbstractArgument<?> argument)
    {
        argumentsMap.put(argument.getArgumentClass().getName(), argument);
    }

    @SuppressWarnings("unchecked")
    public <T> AbstractArgument<T> getArgument(Class<T> clazz)
    {
        var arg = argumentsMap.get(clazz.getName());
        return (AbstractArgument<T>)arg;
    }
}
