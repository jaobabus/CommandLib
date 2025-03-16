package argument;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(ArgumentRestriction.Restrictions.class)
public @interface ArgumentRestriction
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Restrictions {
        ArgumentRestriction[] value();
    }

    String restriction() default "null";
}

