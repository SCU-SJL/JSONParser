package json.token;

/**
 * @author ShaoJiale
 * Date: 2020/2/22
 */
public enum TokenType {
    BEGIN_OBJECT(1),    // {
    END_OBJECT(2),      // }
    BEGIN_ARRAY(4),     // [
    END_ARRAY(8),       // ]
    NULL(16),           // null
    NUMBER(32),         // numbers
    STRING(64),         // string
    BOOLEAN(128),       // true / false
    SEP_COLON(256),     // :
    SEP_COMMA(512),     // ,
    END_DOCUMENT(1024); // end

    private int code;

    TokenType(int code) {
        this.code = code;
    }

    public int getTokenCode() {
        return code;
    }
}
