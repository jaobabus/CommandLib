package fun.jaobabus.commandlib.argument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Argument
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Phrase
    {
        String phrase();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Help
    {
        String help();
    }

    enum Action
    {
        Argument,
        Optional,
        VarArg,
        FlagStoreTrue,
        FlagStoreValue,
    }

    Action action() default Action.Argument;

}
