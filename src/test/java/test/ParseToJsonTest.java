package test;

import json.JsonParser;
import json.exception.JsonParseException;
import json.support.DefaultJsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.*;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author ShaoJiale
 * Date: 2020/2/21
 */
public class ParseToJsonTest {
    private JsonParser jsonParser;
    private User user;
    private Student student;
    private School scu;
    private School bju;
    private Building building_1;
    private Building building_2;
    private String expectedJsonString;

    @Before
    public void init() {
        jsonParser = new DefaultJsonParser();
        user = new User.Builder().name("Jack Ma").age(45).build();
        building_1 = new Building("No.1 teaching building");
        building_2 = new Building("No.2 teaching building");
        scu = new School("Sichuan University", new Building[]{building_1, building_2});
        bju = new School("Beijing University", new Building[]{building_1, building_2});
        student = new Student("Pony Ma", "20170123", new String[]{"Basketball", "Swimming"}, Arrays.asList(scu, bju));
    }

    @Before
    public void getExpectedJson() {
        StringBuilder res = new StringBuilder();
        Optional<String> path = Optional.of(this.getClass().getClassLoader().getResource("expectedJson.txt").getPath());
        File file = new File(path.get());
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.expectedJsonString = res.toString();
    }

    @Test
    public void test() throws JsonParseException {
        Assert.assertEquals(expectedJsonString, jsonParser.parseToJsonString(student));
    }
}
