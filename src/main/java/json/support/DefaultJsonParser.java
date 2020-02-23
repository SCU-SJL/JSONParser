package json.support;

import json.JsonArray;
import json.JsonObject;
import json.JsonParser;
import json.exception.JsonParseException;
import json.token.Token;
import json.token.TokenList;
import json.token.TokenType;
import json.token.Tokenizer;
import json.util.CharReader;

import java.lang.reflect.Field;
import java.util.*;

/**
 * The default implementation of {@link JsonParser}
 *
 * @author ShaoJiale
 * Date: 2020/2/20
 */
public class DefaultJsonParser implements JsonParser {
    private static final int BEGIN_OBJECT_TOKEN = 1;
    private static final int END_OBJECT_TOKEN = 2;
    private static final int BEGIN_ARRAY_TOKEN = 4;
    private static final int END_ARRAY_TOKEN = 8;
    private static final int NULL_TOKEN = 16;
    private static final int NUMBER_TOKEN = 32;
    private static final int STRING_TOKEN = 64;
    private static final int BOOLEAN_TOKEN = 128;
    private static final int SEP_COLON_TOKEN = 256;
    private static final int SEP_COMMA_TOKEN = 512;

    private CharReader charReader;
    private Tokenizer tokenizer = new Tokenizer();
    private TokenList tokens;

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
    public Object parseToObject(String jsonStr, Class<?> targetClass) {
        return null;
    }

    @Override
    public JsonObject<String, Object> parseToJsonObject(String jsonStr) throws JsonParseException {
        this.charReader = new CharReader(jsonStr);
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

    private JsonObject<String, Object> doParseToJsonObject() throws JsonParseException{
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
                        expectedToken= SEP_COMMA_TOKEN | END_OBJECT_TOKEN | SEP_COLON_TOKEN;
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
