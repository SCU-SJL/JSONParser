package json.support;

import json.JsonObject;
import json.JsonParser;
import json.exception.JsonParseException;
import json.token.TokenList;
import json.token.Tokenizer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ShaoJiale
 * Date: 2020/3/16
 */
public abstract class AbstractJsonParser implements JsonParser {
    protected static final int BEGIN_OBJECT_TOKEN = 1;
    protected static final int END_OBJECT_TOKEN = 2;
    protected static final int BEGIN_ARRAY_TOKEN = 4;
    protected static final int END_ARRAY_TOKEN = 8;
    protected static final int NULL_TOKEN = 16;
    protected static final int NUMBER_TOKEN = 32;
    protected static final int STRING_TOKEN = 64;
    protected static final int BOOLEAN_TOKEN = 128;
    protected static final int SEP_COLON_TOKEN = 256;
    protected static final int SEP_COMMA_TOKEN = 512;

    protected Tokenizer tokenizer = new Tokenizer();
    protected TokenList tokens;

    public final static Set<Class<?>> REGULAR_TYPE;
    public final static Set<Class<?>> PRIMITIVE_ARRAY_TYPE;
    public final static Set<Class<?>> PACKAGING_ARRAY_TYPE;
    public final static Set<Class<?>> COLLECTION_TYPE;

    static {
        REGULAR_TYPE = new HashSet<>();
        PRIMITIVE_ARRAY_TYPE = new HashSet<>();
        PACKAGING_ARRAY_TYPE = new HashSet<>();
        COLLECTION_TYPE = new HashSet<>();

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

        PRIMITIVE_ARRAY_TYPE.add(byte[].class);
        PRIMITIVE_ARRAY_TYPE.add(boolean[].class);
        PRIMITIVE_ARRAY_TYPE.add(short[].class);
        PRIMITIVE_ARRAY_TYPE.add(char[].class);
        PRIMITIVE_ARRAY_TYPE.add(int[].class);
        PRIMITIVE_ARRAY_TYPE.add(float[].class);
        PRIMITIVE_ARRAY_TYPE.add(long[].class);
        PRIMITIVE_ARRAY_TYPE.add(double[].class);

        PACKAGING_ARRAY_TYPE.add(Byte[].class);
        PACKAGING_ARRAY_TYPE.add(Boolean[].class);
        PACKAGING_ARRAY_TYPE.add(Short[].class);
        PACKAGING_ARRAY_TYPE.add(Character[].class);
        PACKAGING_ARRAY_TYPE.add(Integer[].class);
        PACKAGING_ARRAY_TYPE.add(Float[].class);
        PACKAGING_ARRAY_TYPE.add(Long[].class);
        PACKAGING_ARRAY_TYPE.add(Double[].class);
        PACKAGING_ARRAY_TYPE.add(String[].class);

        COLLECTION_TYPE.add(List.class);
        COLLECTION_TYPE.add(Set.class);
        COLLECTION_TYPE.add(Map.class);
    }

    @Override
    public abstract Object parseToObject(String jsonStr, Class<?> targetClass) throws JsonParseException;

    @Override
    public abstract String parseToJsonString(Object bean) throws JsonParseException;

    @Override
    public abstract JsonObject<?, ?> parseToJsonObject(String jsonStr) throws JsonParseException;
}
