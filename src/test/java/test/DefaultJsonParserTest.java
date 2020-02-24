package test;

import json.JsonObject;
import json.JsonParser;
import json.exception.JsonParseException;
import json.support.DefaultJsonParser;
import org.junit.Before;
import org.junit.Test;
import pojo.Building;
import pojo.School;
import pojo.Student;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author ShaoJiale
 * Date: 2020/2/23
 */
public class DefaultJsonParserTest {
    private Student student;
    private JsonParser parser;
    @Before
    public void init() {
        School scu = new School("Sichuan University", new Building[]{
                new Building(1, "No.1 Teaching Building"),
                new Building(2, "No.2 Teaching Building")
        });
        School bju = new School("Beijing University", new Building[]{
                new Building(1, "No.1 Teaching Building"),
                new Building(2, "No.2 Teaching Building")
        });
        String[] hobbies = {"Basketball", "Swimming"};
        List<School> schools = Arrays.asList(scu, bju);
        student = new Student("Marvin", "20170123", hobbies, schools);
        parser = new DefaultJsonParser();
    }

    @Test
    public void parseToJsonObject() throws JsonParseException {
        String jsonStr = parser.parseToJsonString(this.student);
        System.out.println(jsonStr);

        JsonObject<String, Object> jsonObject = new DefaultJsonParser().parseToJsonObject(jsonStr);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            System.out.print(entry.getKey() + ":");
            System.out.println(entry.getValue());
        }
    }
}
