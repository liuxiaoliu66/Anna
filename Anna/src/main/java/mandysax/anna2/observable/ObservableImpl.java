package mandysax.anna2.observable;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import mandysax.anna2.Anna2;
import mandysax.anna2.TYPE;
import mandysax.anna2.callback.Callback;
import mandysax.anna2.callback.ResponseBody;
import mandysax.anna2.factory.ConverterFactory;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author liuxiaoliu66
 */
public final class ObservableImpl<T> implements Observable<T> {

    static final Handler mHandler = new Handler(Looper.getMainLooper());

    final Class<T> mModel;
    /**
     * 数据加载完成后的解析路径
     */
    final String[] mPath;
    /**
     * 请求体
     */
    private final HashMap<String, String> mBodyMap;

    final Call mCall;

    final ConverterFactory.Factory mFactory;

    @NotNull
    @Contract(pure = true)
    public static <T> Observable<T> create(@NotNull Anna2 anna2, Class<T> model, TYPE type, String url, String path, HashMap<String, String> queryMap, HashMap<String, String> bodyMap, HashMap<String, String> headerMap) {
        return new ObservableImpl<>(anna2.getConverterFactory(), model, type, url, path, queryMap, bodyMap, headerMap);
    }

    private ObservableImpl(ConverterFactory.Factory factory, Class<T> model, TYPE type, String url, String path, HashMap<String, String> queryMap, HashMap<String, String> bodyMap, HashMap<String, String> headerMap) {
        mFactory = factory;
        mPath = path != null ? path.split("/") : null;
        mBodyMap = bodyMap;
        mModel = model;
        Request.Builder mBuilder = new Request.Builder().url(type == TYPE.GET ? getUrl(url, queryMap) : url);
        if (headerMap != null) {
            mBuilder.headers(Headers.of(headerMap));
        }
        switch (type) {
            case GET:
                mBuilder.get();
                break;
            case POST:
                mBuilder.post(bodyMapBuild());
                break;
            case DELETE:
                if (bodyMapIsEmpty()) {
                    mBuilder.delete();
                    break;
                }
                mBuilder.delete(bodyMapBuild());
                break;
            case PUT:
                mBuilder.put(bodyMapBuild());
                break;
            default:
                throw new IllegalStateException("No such request type in anna:" + type);
        }
        mCall = new OkHttpClient().newCall(mBuilder.build());
    }

    @NotNull
    private FormBody bodyMapBuild() {
        FormBody.Builder formBody = new FormBody.Builder();
        if (mBodyMap == null) {
            return formBody.build();
        }
        for (Map.Entry<String, String> set : mBodyMap.entrySet()) {
            formBody.add(set.getKey(), set.getValue());
        }
        return formBody.build();
    }

    private boolean bodyMapIsEmpty() {
        return mBodyMap.values().isEmpty();
    }

    private String getUrl(String path, Map<String, String> paramsMap) {
        if (paramsMap != null) {
            StringBuilder pathBuilder = new StringBuilder(path + "?");
            for (Map.Entry<String, String> map : paramsMap.entrySet()) {
                pathBuilder.append(map.getKey()).append("=").append(map.getValue()).append("&");
            }
            path = pathBuilder.toString();
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    @Override
    public void set(ResponseBody responseBody) {
        CallUtils.responseBodyCall(this, responseBody);
    }

    @Override
    public void set(Callback<T> callback) {
        CallUtils.callBackCall(this, callback);
    }

    @Override
    public void cancel() {
        if (!mCall.isCanceled()) {
            mCall.cancel();
        }
    }

}
