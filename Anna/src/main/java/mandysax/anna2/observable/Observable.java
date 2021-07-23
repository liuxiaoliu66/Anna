package mandysax.anna2.observable;

import mandysax.anna2.callback.Callback;
import mandysax.anna2.callback.ResponseBody;

/**
 * @author liuxiaoliu66
 */
public interface Observable<T> {

    /**
     * @param responseBody 事件回调
     */
    void set(ResponseBody responseBody);

    /**
     * @param callback 事件回调
     */
    void set(Callback<T> callback);

    /**
     * 取消当前请求
     */
    void cancel();

}
