package test;

import json.JsonParser;
import json.exception.JsonParseException;
import json.support.DefaultJsonParser;
import org.junit.Before;
import org.junit.Test;
import pojo.ArrayBean;
import pojo.Building;
import pojo.School;

import java.lang.reflect.Field;

/**
 * @author ShaoJiale
 * Date: 2020/2/24
 */
public class ParseJsonStrToObjectTest {
    private JsonParser parser = new DefaultJsonParser();
    private Building building1;
    private Building building2;
    private School school;
    @Before
    public void init() {
        building1 = new Building(1, "No.1 teaching building");
        building2 = new Building(2, "No.2 teaching building");
        school = new School("SCU", new Building[]{building1, building2});
    }

    @Test
    public void testV1() throws JsonParseException {
        String json = parser.parseToJsonString(building1);
        Building parsedBuilding = (Building) parser.parseToObject(json, Building.class);
        System.out.println(parsedBuilding);
    }

    @Test
    public void testV2() throws JsonParseException {
        String json = parser.parseToJsonString(new ArrayBean());
        System.out.println(json);
//        school = (School) parser.parseToObject(json, School.class);
//        System.out.println(school);
    }
}
