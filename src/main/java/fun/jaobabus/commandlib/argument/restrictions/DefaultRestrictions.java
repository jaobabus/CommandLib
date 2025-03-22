package fun.jaobabus.commandlib.argument.restrictions;


public class DefaultRestrictions
{

    static private ArgumentRestrictionRegistry defaultRegistry = null;
    static public ArgumentRestrictionRegistry getDefaultRegistry() {
        if (defaultRegistry == null) {
            defaultRegistry = new ArgumentRestrictionRegistry();
            defaultRegistry.putRestriction(new IntRange());
            defaultRegistry.putRestriction(new StringRange());
        }
        return defaultRegistry;
    }
}
