package json;

import java.util.ArrayList;

/**
 * @author ShaoJiale
 * Date: 2020/2/23
 */
public class JsonArray<E> extends ArrayList<E> {
    public JsonObject<?, ?> getJsonObject(int index) {
        Object obj = this.get(index);
        if (obj instanceof JsonObject) {
            return (JsonObject<?, ?>) obj;
        }
        throw new RuntimeException("Value is not a JsonObject");
    }

    public JsonArray<?> getJsonArray(int index) {
        Object obj = this.get(index);
        if (obj instanceof JsonArray) {
            return (JsonArray<?>) obj;
        }
        throw new RuntimeException("Value is not a JsonArray");
    }
}
