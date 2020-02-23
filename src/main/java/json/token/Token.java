package json.token;

import lombok.*;

/**
 * @author ShaoJiale
 * Date: 2020/2/22
 */
@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
@AllArgsConstructor
@ToString
public class Token {
    private TokenType type;
    private String value;
}
