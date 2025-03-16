package argument.arguments;


public class DefaultArguments
{
    static private ArgumentRegistry defaultArgumentsRegistry = null;
    static public ArgumentRegistry getDefaultArgumentsRegistry() {
        if (defaultArgumentsRegistry == null) {
            defaultArgumentsRegistry = new ArgumentRegistry();
            defaultArgumentsRegistry.putArgument(new IntegerArgument());
            defaultArgumentsRegistry.putArgument(new FloatArgument());
            defaultArgumentsRegistry.putArgument(new StringArgument());
            defaultArgumentsRegistry.putArgument(new BooleanArgument());
        }
        return defaultArgumentsRegistry;
    }
}
