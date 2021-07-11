package mandysax.anna2.observable;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import mandysax.anna2.ModelFactory;
import mandysax.anna2.TYPE;
import mandysax.anna2.annotation.Array;
import mandysax.anna2.callback.Callback;
import mandysax.anna2.utils.JsonUtils;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author liuxiaoliu66
 */
public final class Observable<T> {

    private final Class<T> mModel;
    /**
     * url
     */
    private final String mUrl;
    /**
     * 数据加载完成后的解析路径
     */
    private final String[] mPath;
    /**
     * 请求体和请求头
     */
    private final HashMap<String, String> mBody, mHeaders;
    private final TYPE mType;
    private Callback<T> mCallback;

    private Observable(TYPE type, String url, String path, HashMap<String, String> body, HashMap<String, String> headers, Class<T> model) {
        mType = type;
        mUrl = url;
        if (path != null) {
            mPath = path.split("/");
        } else {
            mPath = null;
        }
        mBody = body;
        mModel = model;
        mHeaders = headers;
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

    public void set(Callback<T> callback) {
        mCallback = callback;
        OkHttpClient httpClient = new OkHttpClient();

        Request.Builder builder = mType == TYPE.GET ? new Request.Builder().url(getUrl(mUrl, mBody)) : new Request.Builder().url(mUrl);
        if (mHeaders != null) {
            for (Map.Entry<String, String> set : mHeaders.entrySet()) {
                builder.header(set.getKey(), set.getValue());
            }
        }
        switch (mType) {
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(bodyMapBuild());
                break;
            case DELETE:
                if (bodyMapIsEmpty()) {
                    builder.delete();
                    return;
                }
                builder.delete(bodyMapBuild());
                break;
            case PUT:
                builder.put(bodyMapBuild());
                break;
            default:
                throw new IllegalStateException("No such request type in anna:" + mType);
        }
        Request request = builder.build();
        Call call = httpClient.newCall(request);
        //noinspection NullableProblems
        call.enqueue(new okhttp3.Callback() {

            private final Handler mHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mHandler.post(() -> mCallback.onFailure(call.hashCode()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                AtomicReference<String> data = new AtomicReference<>(response.body().string());
                mHandler.post(() -> {
                    try {
                        if (mPath != null) {
                            data.set(JsonUtils.Parsing(data.get(), mPath));
                        }
                        if (mModel.getAnnotation(Array.class) != null) {
                            JSONArray json = new JSONArray(data.get());
                            for (int i = 0; i < json.length(); i++) {
                                mCallback.onResponse(i == json.length() - 1, ModelFactory.create(mModel, json.getString(i)));
                            }
                        } else {
                            mCallback.onResponse(true, ModelFactory.create(mModel, data.get()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mCallback.onFailure(response.code());
                    }
                });
            }

        });
    }

}
