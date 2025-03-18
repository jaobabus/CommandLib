package util;

import java.util.Map;


public class AbstractExecutionContext
{
    public Object executor;
    public Map<String, Object> shortTermCache;

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

}
