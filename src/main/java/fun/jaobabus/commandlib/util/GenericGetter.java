package fun.jaobabus.commandlib.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class GenericGetter
{
    @SuppressWarnings("unchecked")
    public static <T> Class<T> get(Class<?> currentClass) {
        Type parameterizedType = currentClass.getGenericSuperclass();
        return (Class<T>) ((ParameterizedType)parameterizedType).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getRecursive(Class<?> currentClass, Class<?> targetClass) {
        while (currentClass != Object.class) {
            var genericSuperclass = currentClass.getGenericSuperclass();

            if (genericSuperclass instanceof ParameterizedType parameterizedType &&
                    ((Class<?>) parameterizedType.getRawType()).equals(targetClass)) {
                return (Class<T>) parameterizedType.getActualTypeArguments()[0];
            }

            currentClass = currentClass.getSuperclass();
        }

        throw new RuntimeException("Cannot determine generic type for " + targetClass.getName());
    }
}
