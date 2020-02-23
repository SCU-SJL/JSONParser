package json;

import json.exception.JsonParseException;

/**
 * Utility interface for conversion of Bean, JSONString and {@link JsonObject}
 *
 * @author ShaoJiale
 * Date: 2020/2/20
 */
public interface JsonParser {
    /**
     * Convert JSONString into target bean.
     *
     * @param jsonStr     JSONString
     * @param targetClass Class of target bean
     * @return target bean
     */
    Object parseToObject(String jsonStr, Class<?> targetClass);

    /**
     * Convert bean into JSONString.
     *
     * @param bean target bean
     * @return JSONString corresponding to the bean
     * @throws JsonParseException this method may cause illegal access when reading the {@link java.lang.reflect.Field}
     *                            from the given bean.
     */
    String parseToJsonString(Object bean) throws JsonParseException;

    /**
     * Get the {@link JsonObject} corresponding to the given json.
     *
     * @param jsonStr JSONString
     * @return {@link JsonObject} corresponding to the given json
     */
    JsonObject<?, ?> parseToJsonObject(String jsonStr) throws JsonParseException;
}
