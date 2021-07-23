package mandysax.anna2.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author liuxiaoliu66
 */
public final class ThrowUtils {

    @NotNull
    @Contract(" -> new")
    public static IllegalStateException queryMapAlreadyExists() {
        return new IllegalStateException("@QueryMap & @Query cannot coexist,And only one @QueryMap can exist");
    }

    @NotNull
    @Contract(" -> new")
    public static IllegalStateException bodyMapAlreadyExists() {
        return new IllegalStateException("@BodyMap & @Body cannot coexist,And only one @BodyMap can exist");
    }

    @NotNull
    @Contract(" -> new")
    public static IllegalStateException returnGenericClazzIsNull() {
        return new IllegalStateException("Could not find the generic type that needs to be returned");
    }

    @NotNull
    @Contract(" -> new")
    public static IllegalStateException pathAlreadyExists() {
        return new IllegalStateException("Only one @Path is allowed");
    }

    @NotNull
    @Contract("_ -> new")
    public static RuntimeException jsonParsingError(String s) {
        return new RuntimeException("Json parsing error:" + s);
    }

    @NotNull
    @Contract("_ -> new")
    public static RuntimeException newInstanceError(Class<?> clazz) {
        return new RuntimeException("Unable to create a new instance of " + clazz);
    }

    @NotNull
    @Contract(" -> new")
    public static NullPointerException noAnnotationRequestType() {
        return new NullPointerException("No annotation request type");
    }

}
