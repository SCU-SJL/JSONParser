package json.support;

import json.JsonNode;
import json.JsonObject;
import json.JsonParser;
import json.exception.JsonParseException;

import java.lang.reflect.Field;
import java.util.*;

/**
 * The default implementation of {@link JsonParser}
 *
 * @author ShaoJiale
 * Date: 2020/2/20
 */
public class DefaultJsonParser implements JsonParser {

    public final static Set<Class<?>> REGULAR_TYPE;
    public final static Set<Class<?>> ARRAY_TYPE;

    static {
        REGULAR_TYPE = new HashSet<>();
        ARRAY_TYPE = new HashSet<>();
        REGULAR_TYPE.add(byte.class);
        REGULAR_TYPE.add(boolean.class);
        REGULAR_TYPE.add(short.class);
        REGULAR_TYPE.add(char.class);
        REGULAR_TYPE.add(int.class);
        REGULAR_TYPE.add(float.class);
        REGULAR_TYPE.add(long.class);
        REGULAR_TYPE.add(double.class);
        REGULAR_TYPE.add(Byte.class);
        REGULAR_TYPE.add(Boolean.class);
        REGULAR_TYPE.add(Short.class);
        REGULAR_TYPE.add(Character.class);
        REGULAR_TYPE.add(Integer.class);
        REGULAR_TYPE.add(Float.class);
        REGULAR_TYPE.add(Long.class);
        REGULAR_TYPE.add(Double.class);
        REGULAR_TYPE.add(String.class);
        ARRAY_TYPE.add(byte[].class);
        ARRAY_TYPE.add(boolean[].class);
        ARRAY_TYPE.add(short[].class);
        ARRAY_TYPE.add(char[].class);
        ARRAY_TYPE.add(int[].class);
        ARRAY_TYPE.add(float[].class);
        ARRAY_TYPE.add(long[].class);
        ARRAY_TYPE.add(double[].class);
        ARRAY_TYPE.add(String[].class);
    }

    @Override
    public String parseToJsonString(Object bean) throws JsonParseException {
        if (bean == null) {
            return null;
        }
        JsonObject<?> jsonObject = parseToJsonObject(bean);
        Field[] fields = jsonObject.getFields();
        StringBuilder result = new StringBuilder("{");

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            String key = field.getName();
            Class<?> fieldClass = field.getType();
            Object value;

            try {
                value = field.get(bean);
            } catch (IllegalAccessException ex) {
                throw new JsonParseException("Cannot access this field: [" + field + "]", ex);
            }

            result.append("\"")
                    .append(key)
                    .append("\":");

            if (value == null) {
                result.append("null");
                if (i < fields.length - 1) {
                    result.append(",");
                }
                continue;
            }

            parseValueToJsonString(result, fieldClass, value);

            if (i < fields.length - 1) {
                result.append(",");
            }
        }

        result.append("}");
        return result.toString();
    }

    @Override
    public Object parseToObject(String jsonStr, Class<?> targetClass) {
        return null;
    }

    @Override
    public JsonObject<?> parseToJsonObject(Object bean) {
        return new JsonObject<>(bean);
    }

    /**
     * Parse value corresponding to the specific key into JSONString.
     *
     * @param result     the final JSONString
     * @param fieldClass {@link Class} of the value
     * @param value      the value itself
     * @throws JsonParseException this method will callback {@link DefaultJsonParser#parseToJsonString(Object)} when it meets a bean
     */
    private void parseValueToJsonString(StringBuilder result, Class<?> fieldClass, Object value) throws JsonParseException {
        if (REGULAR_TYPE.contains(fieldClass)) { // if the current field is regular
            if (fieldClass.equals(String.class)) {
                result.append("\"")
                        .append(value)
                        .append("\"");
            } else {
                result.append(value);
            }
        } else if (fieldClass.isArray()) {  // if the current field is an array
            result.append("[");
            Object[] array = (Object[]) value;
            if (ARRAY_TYPE.contains(fieldClass)) {
                if (String[].class.equals(fieldClass)) {
                    for (int j = 0; j < array.length; j++) {
                        result.append("\"")
                                .append(array[j])
                                .append("\"");
                        if (j < array.length - 1) {
                            result.append(",");
                        }
                    }
                } else {
                    for (int j = 0; j < array.length; j++) {
                        result.append(array[j]);
                        if (j < array.length - 1) {
                            result.append(",");
                        }
                    }
                }
            } else {    // if it's an array of beans
                for (int j = 0; j < array.length; j++) {
                    result.append(parseToJsonString(array[j]));
                    if (j < array.length - 1) {
                        result.append(",");
                    }
                }
            }
            result.append("]");
        } else if (List.class.isAssignableFrom(fieldClass)) {
            result.append("[");
            List<?> list = (List<?>) value;
            Iterator<?> iterator = list.iterator();
            parseListOrSetFieldToJsonString(result, iterator, list.size());
            result.append("]");
        } else if (Map.class.isAssignableFrom(fieldClass)) {
            result.append("{");
            Map<?, ?> map = (Map<?, ?>) value;
            int i = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object mapKey = entry.getKey();
                Object mapValue = entry.getValue();
                parseValueToJsonString(result, mapKey.getClass(), mapKey);
                result.append(":");
                parseValueToJsonString(result, mapValue.getClass(), mapValue);
                if (i++ < map.size() - 1) {
                    result.append(",");
                }
            }
            result.append("}");
        } else if (Set.class.isAssignableFrom(fieldClass)) {
            result.append("[");
            Set<?> set = (Set<?>) value;
            Iterator<?> iterator = set.iterator();
            parseListOrSetFieldToJsonString(result, iterator, set.size());
            result.append("]");
        } else {    // if the current field is a bean
            result.append(parseToJsonString(value));
        }
    }

    /**
     * Parse {@link Field} of {@link List} or {@link Set} into JSONString
     *
     * @param result   the final JSONString
     * @param iterator {@link Iterator} of a List or a Set
     * @param size     size of a List or Set
     * @throws JsonParseException for each of the elements in the List or Set, we have to callback
     *                            {@link DefaultJsonParser#parseValueToJsonString(StringBuilder, Class, Object)}
     *                            because we are not sure about the type of the elements. In this way, we can
     *                            resolve {@link Field} like "List<List<Integer>>"
     */
    private void parseListOrSetFieldToJsonString(StringBuilder result, Iterator<?> iterator, int size) throws JsonParseException {
        int i = 0;
        while (iterator.hasNext()) {
            Object elem = iterator.next();
            parseValueToJsonString(result, elem.getClass(), elem);
            if (i++ < size - 1) {
                result.append(",");
            }
        }
    }
}
