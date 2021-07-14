package mandysax.anna2.observable;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

import mandysax.anna2.TYPE;
import mandysax.anna2.callback.Callback;
import mandysax.anna2.callback.ResponseBody;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author liuxiaoliu66
 */
public final class Observable<T> {

    final Handler mHandler = new Handler(Looper.getMainLooper());

    final Class<T> mModel;
    /**
     * 数据加载完成后的解析路径
     */
    final String[] mPath;
    /**
     * 请求体和请求头
     */
    private final HashMap<String, String> mBody;

    final Call mCall;

    private Observable(TYPE type, String url, String path, HashMap<String, String> body, HashMap<String, String> headers, Class<T> model) {
        if (path != null) {
            mPath = path.split("/");
        } else {
            mPath = null;
        }
        mBody = body;
        mModel = model;
        Request.Builder mBuilder = type == TYPE.GET ? new Request.Builder().url(getUrl(url, mBody)) : new Request.Builder().url(url);
        if (headers != null) {
            for (Map.Entry<String, String> set : headers.entrySet()) {
                mBuilder.header(set.getKey(), set.getValue());
            }
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

    public static <T> Observable<T> create(TYPE type, String url, String path, HashMap<String, String> body, HashMap<String, String> headers, Class<T> model) {
        return new Observable<>(type, url, path, body, headers, model);
    }

    private String getUrl(String path, Map<String, String> paramsMap) {
        if (paramsMap != null) {
            StringBuilder pathBuilder = new StringBuilder(path + "?");
            for (String key : paramsMap.keySet()) {
                pathBuilder.append(key).append("=").append(paramsMap.get(key)).append("&");
            }
            path = pathBuilder.toString();
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private FormBody bodyMapBuild() {
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry<String, String> set : mBody.entrySet()) {
            formBody.add(set.getKey(), set.getValue());
        }
        return formBody.build();
    }

    private boolean bodyMapIsEmpty() {
        return mBody.values().isEmpty();
    }

    public void set(ResponseBody responseBody) {
        CallUtils.responseBodyCall(this, responseBody);
    }

    public void set(Callback<T> callback) {
        CallUtils.callBackCall(this, callback);
    }

    public void cancel() {
        if (!mCall.isCanceled()) {
            mCall.cancel();
        }
    }

}
