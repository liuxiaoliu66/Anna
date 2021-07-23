package studio.mandysa.sample.logic.network;

import org.jetbrains.annotations.NotNull;

import mandysax.anna2.Anna2;

/**
 * @author liuxiaoliu66
 */
public final class ServiceCreator {
    private static final Anna2 ANNA = Anna2.build().baseUrl("http://xxx.xxx.xxx/");

    @NotNull
    public static <T> T create(Class<T> clazz) {
        return ANNA.newProxy(clazz);
    }
}
