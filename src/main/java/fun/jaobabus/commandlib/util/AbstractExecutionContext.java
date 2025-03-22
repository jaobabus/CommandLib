package fun.jaobabus.commandlib.util;

import java.util.HashMap;
import java.util.Map;


public class AbstractExecutionContext
{
    public Object executor;
    public Map<String, Object> shortTermCache = new HashMap<>();
    public Map<String, Object> contextualValues = new HashMap<>();

    public Object getSTCacheFor(Object obj)
    {
        return getSTCacheFor(obj, null);
    }

    public Object getSTCacheFor(Object obj, Object def)
    {
        String key = String.valueOf(obj.hashCode());
        if (def != null && !shortTermCache.containsKey(key))
            shortTermCache.put(key, def);
        return shortTermCache.getOrDefault(key, def);
    }

    public Object getContextualValue(String name) {
        return contextualValues.get(name);
    }

    public void setContextualValue(String name, Object value) {
        contextualValues.put(name, value);
    }

}
