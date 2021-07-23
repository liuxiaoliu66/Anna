package mandysax.anna2.factory;

import org.jetbrains.annotations.NotNull;

/**
 * @author liuxiaoliu66
 */
public final class ConverterFactory {

    public interface Factory {
        /**
         * @param modelClass 实体类class
         * @param content    待解析的json数据
         * @param <T>        实体泛型
         * @return 解析好的实体对象
         */
        <T> T create(@NotNull Class<T> modelClass, String content);
    }

}
