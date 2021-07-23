package mandysax.anna2.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author liuxiaoliu66
 */
public final class JsonUtils {
    public static String Parsing(String content, String... key) {
        if (key == null) return content;
        for (String name : key) {
            try {
                content = new JSONTokener(content).nextValue() instanceof JSONObject ? new JSONObject(content).optString(name) : new JSONArray(content).optJSONObject(0).optString(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return content;
    }

}
