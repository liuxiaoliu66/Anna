package mandysax.anna2.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/*2021.5.19*/
/*2021.7.21*/

/**
 * @author liuxiaoliu66
 */
public final class GenericUtils {

    @Nullable
    public static Class<?> getGenericType(@NotNull Method method) {
        Type type = method.getGenericReturnType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            for (Type value : types) {
                return (Class<?>) value;
            }
        }
        return null;
    }

    @Nullable
    public static Class<?> getGenericType(@NotNull Field field) {
        Type genericsFieldType = field.getGenericType();
        if (genericsFieldType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericsFieldType;
            Type[] fieldArgTypes = parameterizedType.getActualTypeArguments();
            for (Type fieldArgType : fieldArgTypes) {
                return (Class<?>) fieldArgType;
            }
        }
        return null;
    }

}
