package test;

import json.JsonArray;
import json.JsonObject;
import json.JsonParser;
import json.exception.JsonParseException;
import json.support.DefaultJsonParser;
import lombok.*;
import org.junit.Assert;
import org.junit.Test;
import pojo.Building;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author ShaoJiale
 * Date: 2020/2/24
 */
public class Playground {
    private static JsonParser parser = new DefaultJsonParser();
    @Test
    public void test() throws Exception {
        Map<String, Building> g = new HashMap<>();
        int a = 100;
        double b = 3.14;
        String d = "Hello";
        List<Integer> e = new ArrayList<>();
        e.add(101);
        Set<String> f = new HashSet<>();
        f.add("World");
        g.put( "11", new Building(1, "No.1 building"));
        Bean bean = new Bean(a, b, true, d, e, f, g);

        String json = parser.parseToJsonString(bean);
        System.out.println(json);

        JsonObject<String, Object> jsonObject = (JsonObject<String, Object>) parser.parseToJsonObject(json);
        System.out.println(jsonObject);

        bean = (Bean) parser.parseToObject(json, Bean.class);
        System.out.println(bean);
    }

    @ToString
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Bean {
        int a;
        double b;
        Boolean c;
        String d;
        List<Integer> e;
        Set<String> f;
        Map<String, Building> g;
    }
}
