package mandysax.anna2.factory;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import mandysax.anna2.annotation.Path;
import mandysax.anna2.annotation.Value;
import mandysax.anna2.utils.FieldUtils;
import mandysax.anna2.utils.GenericUtils;
import mandysax.anna2.utils.JsonUtils;
import mandysax.anna2.utils.ThrowUtils;

/**
 * @author liuxiaoliu66
 */
public final class DefaultConverterFactory implements ConverterFactory.Factory {

    private DefaultConverterFactory() {
    }

    private final HashMap<String, String> pathMap = new HashMap<>();

    @NotNull
    @Contract(" -> new")
    public static DefaultConverterFactory create() {
        return new DefaultConverterFactory();
    }

    @NotNull
    @Override
    public <T> T create(@NotNull Class<T> modelClass, String content) {
        T object;
        try {
            object = modelClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw ThrowUtils.newInstanceError(modelClass);
        }
        if (content == null) {
            return object;
        }
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Path.class)) {
                Path path = field.getAnnotation(Path.class);
                pathMap.put(field.getName(), JsonUtils.Parsing(content, path != null ? path.value().split("/") : null));
            }
        }
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getAnnotations().length != 0) {
                String json = pathMap.get(field.getName()) != null ? pathMap.get(field.getName()) : content;
                String name = Objects.requireNonNull(field.getAnnotation(Value.class)).value();
                if (field.getType() == Boolean.class) {
                    FieldUtils.setField(field, object, parsingBoolean(name, json));
                } else if (field.getType() == String.class) {
                    FieldUtils.setField(field, object, parsingString(name, json));
                } else if (field.getType() == int.class) {
                    FieldUtils.setField(field, object, parsingInt(name, json));
                } else if (field.getType() == long.class) {
                    FieldUtils.setField(field, object, parsingLong(name, json));
                } else if (field.getType() == List.class || field.getType() == ArrayList.class) {
                    @SuppressWarnings("All") Class<Objects> classType = (Class<Objects>) GenericUtils.getGenericType(field);
                    if (classType == null) continue;
                    JSONArray array = parsingJsonArray(name, json);
                    if (array == null) continue;
                    ArrayList<Objects> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            list.add(create(classType, array.getString(i)));
                        } catch (JSONException ignored) {
                        }
                    }
                    FieldUtils.setField(field, object, list);
                } else {
                    FieldUtils.setField(field, object, parsingObject(field, json));
                }
            }
        }
        return object;
    }

    private @NotNull
    JSONObject getNextValue(String in) {
        try {
            return new JSONTokener(in).nextValue() instanceof JSONObject ? new JSONObject(in) : new JSONArray(in).optJSONObject(0);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    private boolean parsingBoolean(String name, String json) {
        return getNextValue(json).optBoolean(name);
    }

    private int parsingInt(String name, String json) {
        return getNextValue(json).optInt(name);
    }

    private long parsingLong(String name, String json) {
        return getNextValue(json).optLong(name);
    }

    @NotNull
    private String parsingString(String name, String json) {
        return getNextValue(json).optString(name);
    }

    private JSONArray parsingJsonArray(String name, String json) {
        return getNextValue(json).optJSONArray(name);
    }

    @NotNull
    private Object parsingObject(@NotNull Field field, String json) {
        return create(field.getType(), json);
    }

}
