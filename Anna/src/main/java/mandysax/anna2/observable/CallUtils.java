package mandysax.anna2.observable;

import org.json.JSONArray;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import mandysax.anna2.Anna2;
import mandysax.anna2.ModelFactory;
import mandysax.anna2.annotation.Array;
import mandysax.anna2.callback.ResponseBody;
import mandysax.anna2.utils.JsonUtils;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;

/**
 * @author liuxiaoliu66
 */
final class CallUtils {

    public static <T> void responseBodyCall(Observable<T> observable, ResponseBody responseBody) {
        callBackCall(observable, new mandysax.anna2.callback.Callback<T>() {
            @Override
            public void onResponse(boolean loaded, T t) {
                responseBody.onResponse();
            }

            @Override
            public void onFailure(int code) {
                responseBody.onFailure(code);
            }
        });
    }

    public static <T> void callBackCall(Observable<T> observable, mandysax.anna2.callback.Callback<T> callback) {
        observable.mCall.enqueue(new Callback() {
            @Override
            @EverythingIsNonNull
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Observable.mHandler.post(() -> callback.onFailure(Anna2.NO_INTERNET));
            }

            @Override
            @EverythingIsNonNull
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Observable.mHandler.post(() -> callback.onFailure(response.code()));
                    return;
                }
                assert response.body() != null;
                AtomicReference<String> data = new AtomicReference<>(response.body().string());
                Observable.mHandler.post(() -> {
                    try {
                        if (observable.mPath != null) {
                            data.set(JsonUtils.Parsing(data.get(), observable.mPath));
                        }
                        if (observable.mModel.getAnnotation(Array.class) != null) {
                            JSONArray json = new JSONArray(data.get());
                            for (int i = 0; i < json.length(); i++) {
                                callback.onResponse(i == json.length() - 1, ModelFactory.create(observable.mModel, json.getString(i)));
                            }
                        } else {
                            callback.onResponse(true, ModelFactory.create(observable.mModel, data.get()));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
            }
        });
    }
}
