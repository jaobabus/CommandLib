package fun.jaobabus.commandlib.argument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Flag
{
    enum Action
    {
        StoreTrue,
        StoreValue
    }

    Action action() default Action.StoreTrue;
}
