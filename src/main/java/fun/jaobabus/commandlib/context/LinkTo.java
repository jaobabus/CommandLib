package fun.jaobabus.commandlib.context;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(LinkTo.Links.class)
public @interface LinkTo
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Links {
        LinkTo[] value();
    }

    String source() default "<value>";
    String target();
}
