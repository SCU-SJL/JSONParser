package json.token;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ShaoJiale
 * Date: 2020/2/22
 */
@ToString(of = {"tokens"})
public class TokenList {
    private List<Token> tokens = new ArrayList<>();
    private int index = 0;

    public int size() {
        return this.tokens.size();
    }

    public void add(Token token) {
        tokens.add(token);
    }

    public Token current() {
        return index < tokens.size() ? tokens.get(index) : null;
    }

    public Token previous() {
        return index > 1 ? tokens.get(index - 2) : null;
    }

    public Token next() {
        return tokens.get(index++);
    }

    public boolean hasMore() {
        return index < tokens.size();
    }
}
