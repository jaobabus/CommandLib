package command;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(CommandRestriction.Restrictions.class)
public @interface CommandRestriction
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Restrictions {
        CommandRestriction[] value();
    }

    String restriction() default "null";
}

