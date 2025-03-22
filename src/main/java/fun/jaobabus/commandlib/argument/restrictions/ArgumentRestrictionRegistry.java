package fun.jaobabus.commandlib.argument.restrictions;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;

import java.util.HashMap;
import java.util.Map;

public class ArgumentRestrictionRegistry
{
    private final Map<String, AbstractRestrictionFactory<?, ?>> restrictionsMap;

    public ArgumentRestrictionRegistry()
    {
        restrictionsMap = new HashMap<>();
    }

    public void include(ArgumentRestrictionRegistry other)
    {
        for (var key : other.restrictionsMap.keySet()) {
            if (restrictionsMap.containsKey(key))
                throw new RuntimeException(key + " already defined");
            restrictionsMap.put(key, other.restrictionsMap.get(key));
        }
    }

    public void putRestriction(AbstractRestrictionFactory<?, ?> restriction)
    {
        restrictionsMap.put(restriction.getName(), restriction);
    }

    public AbstractRestrictionFactory<?, ?> getRestriction(String name)
    {
        return restrictionsMap.get(name);
    }
}
