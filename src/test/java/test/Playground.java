package test;

import json.JsonArray;
import json.JsonParser;
import json.support.DefaultJsonParser;
import lombok.*;
import org.junit.Assert;
import org.junit.Test;
import pojo.Building;

import java.lang.reflect.Field;

/**
 * @author ShaoJiale
 * Date: 2020/2/24
 */
public class Playground {
    @Test
    public void test() throws Exception {
        JsonParser parser = new DefaultJsonParser();
        Integer[] integers = {1, 2, 3};
        Boolean[] booleans = {true, false, false, true};
        Double[] doubles = {1.11, 2.22, 3.33, 4.44};
        Long[] longs = {111111L, 2222222L, 333333L, 444444L};
        Building[] buildings = {new Building(1, "No.1 building"), new Building(1, "No.2 building")};
        PrimitiveArray primitiveArray = new PrimitiveArray(integers, booleans, doubles, longs, buildings);
        String json = parser.parseToJsonString(primitiveArray);
        System.out.println(json);

        PrimitiveArray newOne = (PrimitiveArray) parser.parseToObject(json, PrimitiveArray.class);
        System.out.println(newOne);
    }

    @Test
    public void test1() throws Exception {
        Integer[] integers = {1, 2, 3};
        Boolean[] booleans = {true, false, false, true};
        Double[] doubles = {1.11, 2.22, 3.33, 4.44};
        Long[] longs = {111111L, 2222222L, 333333L, 444444L};
        Building[] buildings = {new Building(1, "No.1 building"), new Building(1, "No.2 building")};
        PrimitiveArray primitiveArray = new PrimitiveArray(integers, booleans, doubles, longs, buildings);
        Class<?> clazz = primitiveArray.getClass().getDeclaredFields()[4].getType().getComponentType();
        Assert.assertEquals(clazz, Building.class);
    }

    @ToString
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrimitiveArray {
        private Integer[] integers;
        private Boolean[] booleans;
        private Double[] doubles;
        private Long[] longs;
        Building[] buildings;
    }
}
