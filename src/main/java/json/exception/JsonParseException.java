package json.exception;

/**
 * @author ShaoJiale
 * Date: 2020/2/20
 */
public class JsonParseException extends Exception{
    public JsonParseException() {
        super();
    }

    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
