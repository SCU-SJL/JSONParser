package json.util;

import json.exception.JsonParseException;

import java.lang.reflect.Field;

/**
 * @author ShaoJiale
 * Date: 2020/2/24
 */
public abstract class Assert {
    public static void assertFieldNotNull(Field field, String key) throws JsonParseException {
        if (field == null) {
            throw new JsonParseException("Cannot find the field corresponding to key: '" + key + "'");
        }
    }

    public static void assertTypeMatches(Class<?> valueClass, Class<?> expectedClass) throws JsonParseException{
        if (!expectedClass.isAssignableFrom(valueClass)) {
            throw new JsonParseException("Actual type does not match the expected type");
        }
    }
}
