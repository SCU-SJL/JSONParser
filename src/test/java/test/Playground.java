package test;

import json.JsonArray;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ShaoJiale
 * Date: 2020/2/24
 */
public class Playground {
    @Test
    public void test() throws Exception {
        JsonParser parser = new DefaultJsonParser();
        Building building = new Building(1, "No.1 Teaching Building");
        Building building1 = new Building(2, "No.2 Teaching Building");
        Set<Building> set = new HashSet<>();
        set.add(building);
        set.add(building1);
        Bean bean = new Bean(set);

        String json = parser.parseToJsonString(bean);
        System.out.println(json);
        Bean newBean = (Bean) parser.parseToObject(json, Bean.class);
        System.out.println(newBean);
    }

    @Test
    public void test2() {
        Bean bean = new Bean();
        Field field = bean.getClass().getDeclaredFields()[0];
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Class<?> actualType = (Class<?>) pt.getActualTypeArguments()[0];
            System.out.println("actual: " + actualType);
        }
        System.out.println(type);
    }

    @ToString
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Bean {
        Set<Building> buildings;
//        Building[] buildings;
    }
}
