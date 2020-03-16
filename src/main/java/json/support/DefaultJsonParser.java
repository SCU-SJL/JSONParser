package json.support;

import json.JsonArray;
import json.JsonObject;
import json.JsonParser;
import json.exception.JsonParseException;
import json.token.Token;
import json.token.TokenType;
import json.util.Assert;
import json.util.CharReader;

import java.lang.reflect.*;
import java.util.*;

/**
 * The default implementation of {@link JsonParser}
 *
 * @author ShaoJiale
 * Date: 2020/2/20
 */
public class DefaultJsonParser extends AbstractJsonParser {

    @Override
    public String parseToJsonString(Object bean) throws JsonParseException {
        if (bean == null) {
            return null;
        }
        Field[] fields = bean.getClass().getDeclaredFields();
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
    public Object parseToObject(String jsonStr, Class<?> targetClass) throws JsonParseException {
        Object targetObject;
        try {
            targetObject = targetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        JsonObject<String, Object> jsonObject = this.parseToJsonObject(jsonStr);
        return doParseToObject(jsonObject, targetObject, targetClass);
    }

    private Object doParseToObject(JsonObject<String, Object> jsonObject, Object targetObject, Class<?> targetClass) throws JsonParseException {
        Field[] fields = targetClass.getDeclaredFields();

        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Field targetField = null;
            Class<?> valueClass = value.getClass();
            Class<?> fieldClass = null;
            for (Field field : fields) {
                if (field.getName().equals(key)) {
                    targetField = field;
                    fieldClass = targetField.getType();
                    break;
                }
            }

            if (REGULAR_TYPE.contains(valueClass)) {
                Assert.assertFieldNotNull(targetField, key);
                targetField.setAccessible(true);
                try {
                    targetField.set(targetObject, value);
                } catch (IllegalAccessException e) {
                    throw new JsonParseException("Set value to field: '" + targetField + "' failed");
                }
            } else if (valueClass.equals(JsonArray.class)) {
                JsonArray<?> jsonArray = (JsonArray<?>) value;
                if (PRIMITIVE_ARRAY_TYPE.contains(fieldClass)) {
                    Assert.assertFieldNotNull(targetField, key);
                    Assert.assertTypeMatches(valueClass, JsonArray.class);
                    targetField.setAccessible(true);

                    try {
                        if (fieldClass.equals(int[].class)) {
                            int[] arr = new int[jsonArray.size()];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = (Integer) jsonArray.get(i);
                            }
                            targetField.set(targetObject, arr);
                        } else if (fieldClass.equals(short[].class)) {
                            short[] arr = new short[jsonArray.size()];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = (Short) jsonArray.get(i);
                            }
                            targetField.set(targetObject, arr);
                        } else if (fieldClass.equals(byte[].class)) {
                            byte[] arr = new byte[jsonArray.size()];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = Byte.parseByte(jsonArray.get(i).toString());
                            }
                            targetField.set(targetObject, arr);
                        } else if (fieldClass.equals(char[].class)) {
                            char[] arr = new char[jsonArray.size()];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = (Character) jsonArray.get(i);
                            }
                            targetField.set(targetObject, arr);
                        } else if (fieldClass.equals(boolean[].class)) {
                            boolean[] arr = new boolean[jsonArray.size()];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = (Boolean) jsonArray.get(i);
                            }
                            targetField.set(targetObject, arr);
                        } else if (fieldClass.equals(double[].class)) {
                            double[] arr = new double[jsonArray.size()];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = (Double) jsonArray.get(i);
                            }
                            targetField.set(targetObject, arr);
                        } else if (fieldClass.equals(float[].class)) {
                            float[] arr = new float[jsonArray.size()];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = (Float) jsonArray.get(i);
                            }
                            targetField.set(targetObject, arr);
                        } else if (fieldClass.equals(long[].class)) {
                            long[] arr = new long[jsonArray.size()];
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = Long.parseLong(jsonArray.get(i).toString());
                            }
                            targetField.set(targetObject, arr);
                        }
                    } catch (IllegalAccessException e) {
                        throw new JsonParseException("Set value to field: '" + targetField + "' failed");
                    }
                } else if (PACKAGING_ARRAY_TYPE.contains(fieldClass)) {
                    Assert.assertFieldNotNull(targetField, key);
                    Assert.assertTypeMatches(valueClass, JsonArray.class);
                    targetField.setAccessible(true);
                    try {
                        if (fieldClass.equals(Integer[].class)) {
                            targetField.set(targetObject, jsonArray.toArray(new Integer[0]));
                        } else if (fieldClass.equals(Double[].class)) {
                            targetField.set(targetObject, jsonArray.toArray(new Double[0]));
                        } else if (fieldClass.equals(Float[].class)) {
                            targetField.set(targetObject, jsonArray.toArray(new Float[0]));
                        } else if (fieldClass.equals(Short[].class)) {
                            targetField.set(targetObject, jsonArray.toArray(new Short[0]));
                        } else if (fieldClass.equals(Boolean[].class)) {
                            targetField.set(targetObject, jsonArray.toArray(new Boolean[0]));
                        } else if (fieldClass.equals(Character[].class)) {
                            targetField.set(targetObject, jsonArray.toArray(new Character[0]));
                        } else if (fieldClass.equals(Long[].class)) {
                            Long[] longs = new Long[jsonArray.size()];
                            for (int i = 0; i < longs.length; i++) {
                                longs[i] = Long.valueOf(jsonArray.get(i).toString());
                            }
                            targetField.set(targetObject, longs);
                        } else if (fieldClass.equals(Byte[].class)) {
                            targetField.set(targetObject, jsonArray.toArray(new Byte[0]));
                        }
                    } catch (IllegalAccessException e) {
                        throw new JsonParseException("Set value to field: '" + targetField + "' failed");
                    }
                } else if (COLLECTION_TYPE.contains(fieldClass)) {
                    assert fieldClass != null;
                    targetField.setAccessible(true);
                    Class<?> parameterType = getParameterType(targetField);

                    try {
                        if (List.class.isAssignableFrom(fieldClass)) {
                            List<Object> list = new ArrayList<>();
                            if (REGULAR_TYPE.contains(parameterType)) {
                                list.addAll(jsonArray);
                            } else {
                                for (Object o : jsonArray) {
                                    JsonObject<String, Object> elemJsonObject = (JsonObject<String, Object>) o;
                                    Object elemObject = parameterType.getDeclaredConstructor().newInstance();
                                    list.add(doParseToObject(elemJsonObject, elemObject, parameterType));
                                }
                            }
                            targetField.set(targetObject, list);
                        } else if (Set.class.isAssignableFrom(fieldClass)) {
                            Set<Object> set = new HashSet<>();
                            if (REGULAR_TYPE.contains(parameterType)) {
                                set.addAll(jsonArray);
                            } else {
                                for (Object o : jsonArray) {
                                    JsonObject<String, Object> elemJsonObject = (JsonObject<String, Object>) o;
                                    Object elemObject = parameterType.getDeclaredConstructor().newInstance();
                                    set.add(doParseToObject(elemJsonObject, elemObject, parameterType));
                                }
                            }
                            targetField.set(targetObject, set);
                        }
                    } catch (Exception e) {
                        throw new JsonParseException("Set value to field: '" + targetField + "' failed");
                    }
                } else { // array of bean
                    assert targetField != null;
                    targetField.setAccessible(true);
                    try {
                        assert fieldClass != null;
                        Object arr = Array.newInstance(fieldClass.getComponentType(), jsonArray.size());
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject<String, Object> elemJsonObject = (JsonObject<String, Object>) jsonArray.get(i);
                            Object elemObject = fieldClass.getComponentType().getDeclaredConstructor().newInstance();
                            Array.set(arr, i, doParseToObject(elemJsonObject, elemObject, fieldClass.getComponentType()));
                        }
                        targetField.set(targetObject, arr);
                    } catch (Exception e) {
                        throw new JsonParseException("Set value to field: '" + targetField + "' failed");
                    }
                }
            } else if (valueClass.equals(JsonObject.class)) {
                assert targetField != null;
                targetField.setAccessible(true);
                Object fieldObject = null;
                try {
                    assert fieldClass != null;
                    fieldObject = fieldClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                try {
                    targetField.set(targetObject, doParseToObject((JsonObject<String, Object>) value, fieldObject, fieldClass));
                } catch (IllegalAccessException e) {
                    throw new JsonParseException("Set value to field: '" + targetField + "' failed");
                }
            }
        }

        return targetObject;
    }

    @Override
    public JsonObject<String, Object> parseToJsonObject(String jsonStr) throws JsonParseException {
        CharReader charReader = new CharReader(jsonStr);
        this.tokens = this.tokenizer.getTokenList(charReader);

        Token token = tokens.next();
        if (token == null) {
            return new JsonObject<>();
        } else if (token.getType() == TokenType.BEGIN_OBJECT) {
            return doParseToJsonObject();
        } else {
            throw new JsonParseException("Invalid token founded: '" + token + "'");
        }
    }

    /**
     * Parse tokens into JsonObject
     *
     * @return JsonObject
     * @throws JsonParseException when token is invalid or illegal
     */
    private JsonObject<String, Object> doParseToJsonObject() throws JsonParseException {
        JsonObject<String, Object> jsonObject = new JsonObject<>();

        int expectedToken = STRING_TOKEN | END_OBJECT_TOKEN | NUMBER_TOKEN;
        String key = null;
        Object value;

        while (tokens.hasMore()) {
            Token token = tokens.next();
            TokenType tokenType = token.getType();
            String tokenValue = token.getValue();
            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectedToken(tokenType, expectedToken);
                    jsonObject.put(key, doParseToJsonObject());
                    expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case BEGIN_ARRAY:
                    checkExpectedToken(tokenType, expectedToken);
                    jsonObject.put(key, parseToJsonArray());
                    expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case END_OBJECT:
                case END_DOCUMENT:
                    checkExpectedToken(tokenType, expectedToken);
                    return jsonObject;
                case NULL:
                    checkExpectedToken(tokenType, expectedToken);
                    jsonObject.put(key, null);
                    expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case NUMBER:
                    checkExpectedToken(tokenType, expectedToken);
                    Token preT = tokens.previous();

                    if (preT.getType() == TokenType.SEP_COLON) {
                        if (tokenValue.contains(".")) {
                            jsonObject.put(key, Double.valueOf(tokenValue));
                        } else {
                            long num = Long.parseLong(tokenValue);
                            if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                                jsonObject.put(key, num);
                            } else {
                                jsonObject.put(key, (int) num);
                            }
                        }
                        expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN | SEP_COLON_TOKEN;
                    } else {
                        key = token.getValue();
                        expectedToken = SEP_COLON_TOKEN;
                    }

                    break;
                case BOOLEAN:
                    checkExpectedToken(tokenType, expectedToken);
                    jsonObject.put(key, Boolean.valueOf(token.getValue()));
                    expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    break;
                case STRING:
                    checkExpectedToken(tokenType, expectedToken);
                    Token preToken = tokens.previous();
                    if (preToken.getType() == TokenType.SEP_COLON) {    // current string represents a value
                        value = token.getValue();
                        jsonObject.put(key, value);
                        expectedToken = SEP_COMMA_TOKEN | END_OBJECT_TOKEN;
                    } else {                                            // current string represents a key
                        key = token.getValue();
                        expectedToken = SEP_COLON_TOKEN;
                    }
                    break;
                case SEP_COLON:
                    checkExpectedToken(tokenType, expectedToken);
                    expectedToken = NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN
                            | STRING_TOKEN | BEGIN_OBJECT_TOKEN | BEGIN_ARRAY_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExpectedToken(tokenType, expectedToken);
                    expectedToken = STRING_TOKEN | BOOLEAN_TOKEN | NUMBER_TOKEN;    // the last 2 is for Map resolving
                    break;
                default:
                    throw new JsonParseException("Unexpected token");
            }
        }
        throw new JsonParseException("Parse to JsonObject failed, invalid token");
    }

    /**
     * Parse the tokens into JsonArray
     *
     * @return JsonArray
     * @throws JsonParseException when token is invalid or illegal
     */
    private JsonArray<Object> parseToJsonArray() throws JsonParseException {
        int expectedToken = BEGIN_ARRAY_TOKEN | END_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN
                | NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN | STRING_TOKEN;
        JsonArray<Object> jsonArray = new JsonArray<>();

        while (tokens.hasMore()) {
            Token token = tokens.next();
            TokenType tokenType = token.getType();
            String tokenValue = token.getValue();

            switch (tokenType) {
                case BEGIN_OBJECT:
                    checkExpectedToken(tokenType, expectedToken);
                    jsonArray.add(doParseToJsonObject());
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BEGIN_ARRAY:
                    checkExpectedToken(tokenType, expectedToken);
                    jsonArray.add(parseToJsonArray());
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case END_ARRAY:
                case END_DOCUMENT:
                    checkExpectedToken(tokenType, expectedToken);
                    return jsonArray;
                case NULL:
                    checkExpectedToken(tokenType, expectedToken);
                    jsonArray.add(null);
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case NUMBER:
                    checkExpectedToken(tokenType, expectedToken);
                    if (tokenValue.contains(".")) {
                        jsonArray.add(Double.valueOf(tokenValue));
                    } else {
                        long num = Long.parseLong(tokenValue);
                        if (num > Integer.MAX_VALUE || num < Integer.MIN_VALUE) {
                            jsonArray.add(num);
                        } else {
                            jsonArray.add((int) num);
                        }
                    }
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case BOOLEAN:
                    checkExpectedToken(tokenType, expectedToken);
                    jsonArray.add(Boolean.valueOf(tokenValue));
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case STRING:
                    checkExpectedToken(tokenType, expectedToken);
                    jsonArray.add(tokenValue);
                    expectedToken = SEP_COMMA_TOKEN | END_ARRAY_TOKEN;
                    break;
                case SEP_COMMA:
                    checkExpectedToken(tokenType, expectedToken);
                    expectedToken = STRING_TOKEN | NULL_TOKEN | NUMBER_TOKEN | BOOLEAN_TOKEN
                            | BEGIN_ARRAY_TOKEN | BEGIN_OBJECT_TOKEN;
                    break;
                default:
                    throw new JsonParseException("Invalid token: " + token);
            }
        }

        throw new JsonParseException("Parse to JsonArray failed, invalid token");
    }

    /**
     * check if the current token is the expected token.
     *
     * @param tokenType     current token
     * @param expectedToken expected token
     * @throws JsonParseException when the current token is not the expected token
     */
    private void checkExpectedToken(TokenType tokenType, int expectedToken) throws JsonParseException {
        if ((tokenType.getTokenCode() & expectedToken) == 0) {
            throw new JsonParseException("Parse error, invalid Token.");
        }
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
            Object[] array = null;

            if (!PRIMITIVE_ARRAY_TYPE.contains(fieldClass)) {
                array = (Object[]) value;
            }

            if (PRIMITIVE_ARRAY_TYPE.contains(fieldClass) || PACKAGING_ARRAY_TYPE.contains(fieldClass)) {
                if (String[].class.equals(fieldClass)) {
                    String[] arr = (String[]) value;
                    for (int j = 0; j < arr.length; j++) {
                        result.append("\"")
                                .append(arr[j])
                                .append("\"");
                        if (j < arr.length - 1) {
                            result.append(",");
                        }
                    }
                } else if (int[].class.equals(fieldClass)) {
                    assert value instanceof int[];
                    int[] arr = (int[]) value;
                    for (int j = 0; j < arr.length; j++) {
                        result.append(arr[j]);
                        if (j < arr.length - 1) {
                            result.append(",");
                        }
                    }
                } else if (double[].class.equals(fieldClass)) {
                    assert value instanceof double[];
                    double[] arr = (double[]) value;
                    for (int j = 0; j < arr.length; j++) {
                        result.append(arr[j]);
                        if (j < arr.length - 1) {
                            result.append(",");
                        }
                    }
                } else if (float[].class.equals(fieldClass)) {
                    assert value instanceof float[];
                    float[] arr = (float[]) value;
                    for (int j = 0; j < arr.length; j++) {
                        result.append(arr[j]);
                        if (j < arr.length - 1) {
                            result.append(",");
                        }
                    }
                } else if (boolean[].class.equals(fieldClass)) {
                    assert value instanceof boolean[];
                    boolean[] arr = (boolean[]) value;
                    for (int j = 0; j < arr.length; j++) {
                        result.append(arr[j]);
                        if (j < arr.length - 1) {
                            result.append(",");
                        }
                    }
                } else if (char[].class.equals(fieldClass)) {
                    assert value instanceof char[];
                    char[] arr = (char[]) value;
                    for (int j = 0; j < arr.length; j++) {
                        result.append(arr[j]);
                        if (j < arr.length - 1) {
                            result.append(",");
                        }
                    }
                } else if (byte[].class.equals(fieldClass)) {
                    assert value instanceof byte[];
                    byte[] arr = (byte[]) value;
                    for (int j = 0; j < arr.length; j++) {
                        result.append(arr[j]);
                        if (j < arr.length - 1) {
                            result.append(",");
                        }
                    }
                } else if (short[].class.equals(fieldClass)) {
                    assert value instanceof short[];
                    short[] arr = (short[]) value;
                    for (int j = 0; j < arr.length; j++) {
                        result.append(arr[j]);
                        if (j < arr.length - 1) {
                            result.append(",");
                        }
                    }
                } else if (long[].class.equals(fieldClass)) {
                    assert value instanceof long[];
                    long[] arr = (long[]) value;
                    for (int j = 0; j < arr.length; j++) {
                        result.append(arr[j]);
                        if (j < arr.length - 1) {
                            result.append(",");
                        }
                    }
                } else {    // packaging class
                    assert array != null;
                    for (int j = 0; j < array.length; j++) {
                        result.append(array[j]);
                        if (j < array.length - 1) {
                            result.append(",");
                        }
                    }
                }
            } else {    // if it's an array of beans
                assert array != null;
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

    /**
     * Get class of parameters of a collection.
     *
     * @param field field of a collection
     * @return class of parameters of the collection
     */
    private Class<?> getParameterType(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return (Class<?>) pt.getActualTypeArguments()[0];
        }
        return null;
    }
}
