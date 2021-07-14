package mandysax.anna2.observable;

import org.json.JSONArray;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import mandysax.anna2.Anna2;
import mandysax.anna2.ModelFactory;
import mandysax.anna2.annotation.Array;
import mandysax.anna2.callback.ResponseBody;
import mandysax.anna2.utils.JsonUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;

/**
 * @author liuxiaoliu66
 */
public final class CallUtils {

    public static <T> void responseBodyCall(Observable<T> observable, ResponseBody responseBody) {
        observable.mCall.enqueue(new okhttp3.Callback() {
            @Override
            @EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                observable.mHandler.post(() -> responseBody.onFailure(Anna2.NO_INTERNET));
            }

            @Override
            @EverythingIsNonNull
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    observable.mHandler.post(() -> responseBody.onFailure(response.code()));
                    return;
                }
                observable.mHandler.post(responseBody::onResponse);
            }
        });
    }

    public static <T> void callBackCall(Observable<T> observable, mandysax.anna2.callback.Callback<T> callback) {
        observable.mCall.enqueue(new Callback() {
            @Override
            @EverythingIsNonNull
            public void onFailure(okhttp3.Call call, IOException e) {
                observable.mHandler.post(() -> callback.onFailure(Anna2.NO_INTERNET));
            }

            @Override
            @EverythingIsNonNull
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    observable.mHandler.post(() -> callback.onFailure(response.code()));
                    return;
                }
                assert response.body() != null;
                AtomicReference<String> data = new AtomicReference<>(response.body().string());
                observable.mHandler.post(() -> {
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
