package mandysax.anna2.callback;

/**
 * @author liuxiaoliu66
 */
public interface Callback<T> {
    /**
     * @param loaded 是否加载完成，实体类有@Array注解时需要注意此参数
     * @param t      解析好的实体类
     */
    void onResponse(boolean loaded, T t);

    /**
     * @param code 错误码
     */
    void onFailure(int code);
}
