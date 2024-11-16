package ui;

import com.google.gson.Gson;

public class GsonUtils {
    private static final Gson gson = new Gson();

    public static <T> T deserialize(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> String serialize(T object) {
        return gson.toJson(object);
    }
}
