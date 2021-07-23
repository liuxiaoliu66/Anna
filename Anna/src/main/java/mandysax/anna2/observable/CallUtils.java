package mandysax.anna2.observable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import mandysax.anna2.Anna2;
import mandysax.anna2.annotation.Array;
import mandysax.anna2.callback.ResponseBody;
import mandysax.anna2.utils.JsonUtils;
import mandysax.anna2.utils.ThrowUtils;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author liuxiaoliu66
 */
final class CallUtils {

    public static <T> void responseBodyCall(ObservableImpl<T> observable, ResponseBody responseBody) {
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

    public static <T> void callBackCall(@NotNull ObservableImpl<T> observable, mandysax.anna2.callback.Callback<T> callback) {
        observable.mCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                e.printStackTrace();
                ObservableImpl.mHandler.post(() -> callback.onFailure(Anna2.UNKNOWN));
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    ObservableImpl.mHandler.post(() -> callback.onFailure(response.code()));
                    return;
                }
                AtomicReference<String> data = new AtomicReference<>(Objects.requireNonNull(response.body()).string());
                ObservableImpl.mHandler.post(() -> {
                    try {
                        if (observable.mPath != null) {
                            data.set(JsonUtils.Parsing(data.get(), observable.mPath));
                        }
                        if (observable.mModel.getAnnotation(Array.class) != null) {
                            JSONArray json = new JSONArray(data.get());
                            for (int i = 0; i < json.length(); i++) {
                                callback.onResponse(i == json.length() - 1, observable.mFactory.create(observable.mModel, json.getString(i)));
                            }
                        } else {
                            callback.onResponse(true, observable.mFactory.create(observable.mModel, data.get()));
                        }
                    } catch (Exception e) {
                        throw ThrowUtils.jsonParsingError(e.getMessage());
                    }
                });
            }
        });
    }
}
