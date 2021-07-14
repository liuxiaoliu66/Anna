package mandysax.anna2.callback;

/**
 * @author liuxiaoliu66
 */
public interface ResponseBody {

    /**
     * 请求完成
     */
    void onResponse();

    /**
     * @param code 错误码
     */
    void onFailure(int code);
}
