package argument.restrictions;

import java.util.HashMap;
import java.util.Map;

public class ArgumentRestrictionRegistry
{
    private final Map<String, AbstractRestrictionFactory<?>> restrictionsMap;

    public ArgumentRestrictionRegistry()
    {
        restrictionsMap = new HashMap<>();
    }

    public void putRestriction(AbstractRestrictionFactory<?> restriction)
    {
        restrictionsMap.put(restriction.getName(), restriction);
    }

    public AbstractRestrictionFactory<?> getRestriction(String name)
    {
        return restrictionsMap.get(name);
    }
}
