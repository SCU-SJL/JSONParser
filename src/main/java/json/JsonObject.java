package json;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author ShaoJiale
 * Date: 2020/2/20
 */
@Getter
public class JsonObject<T> {
    private Class<?> targetClass;

    private Constructor<?>[] constructors;

    private Method[] methods;

    private Field[] fields;

    public JsonObject(T object) {
        this.targetClass = object.getClass();
        this.constructors = targetClass.getConstructors();
        this.methods = targetClass.getMethods();
        this.fields = targetClass.getDeclaredFields();
    }
}
