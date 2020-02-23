package json.token;

import json.exception.JsonParseException;
import json.util.CharReader;

/**
 * @author ShaoJiale
 * Date: 2020/2/22
 */
public class Tokenizer {

    private CharReader charReader;
    private TokenList tokenList;

    public TokenList getTokenList(CharReader reader) throws JsonParseException {
        this.charReader = reader;
        this.tokenList = new TokenList();

        tokenize();

        return this.tokenList;
    }

    private void tokenize() throws JsonParseException {
        Token token;
        do {
            token = this.resolveToken();
            tokenList.add(token);
        } while (token.getType() != TokenType.END_DOCUMENT);
    }

    private Token resolveToken() throws JsonParseException {
        char cur;
        do {
            if (!charReader.hasMore()) {
                return new Token(TokenType.END_DOCUMENT, null);
            }
            cur = charReader.current();
            charReader.next();
        } while (Character.isWhitespace(cur));

        switch (cur) {
            case '{':
                return new Token(TokenType.BEGIN_OBJECT, String.valueOf(cur));
            case '}':
                return new Token(TokenType.END_OBJECT, String.valueOf(cur));
            case '[':
                return new Token(TokenType.BEGIN_ARRAY, String.valueOf(cur));
            case ']':
                return new Token(TokenType.END_ARRAY, String.valueOf(cur));
            case ',':
                return new Token(TokenType.SEP_COMMA, String.valueOf(cur));
            case ':':
                return new Token(TokenType.SEP_COLON, String.valueOf(cur));
            case 'n':
                return readNull();
            case 't':
            case 'f':
                return readBoolean(cur);
            case '"':
                return readString();
            case '-':
                return readNumber(cur);
        }

        if (Character.isDigit(cur)) {
            return readNumber(cur);
        }

        throw new JsonParseException("Resolving token failed because of a illegal character: '" + cur + "'");
    }

    private Token readNumber(char cur) {
        char ch = cur;
        StringBuilder res = new StringBuilder();

        if (ch == '-') {
            res.append(ch);
            ch = charReader.next();
        }

        while (Character.isDigit(ch) || ch == '.') {
            res.append(ch);
            ch = charReader.next();
        }
        charReader.back();
        return new Token(TokenType.NUMBER, res.toString());
    }

    private Token readBoolean(char cur) throws JsonParseException {
        if (cur == 't') {
            if (!(charReader.next() == 'r' && charReader.next() == 'u' && charReader.next() == 'e')) {
                throw new JsonParseException("Invalid boolean value in json string");
            }
            return new Token(TokenType.BOOLEAN, "true");
        } else {
            if (!(charReader.next() == 'a' && charReader.next() == 'l'
                    && charReader.next() == 's' && charReader.next() == 'e')) {
                throw new JsonParseException("Invalid boolean value json string");
            }
            return new Token(TokenType.BOOLEAN, "false");
        }
    }

    private Token readNull() throws JsonParseException {
        if (!(charReader.next() == 'u' && charReader.next() == 'l' && charReader.next() == 'l')) {
            throw new JsonParseException("Invalid null value in json string");
        }
        return new Token(TokenType.NULL, "null");
    }

    private Token readString() throws JsonParseException {
        StringBuilder res = new StringBuilder();

        // current char is \" and pointer points the next position
        while(true) {
            char ch = charReader.next();
            if (ch == '\\') {   // resolve escape character
                if (!isEscape()) {
                    throw new JsonParseException("Invalid escape character");
                }
                res.append('\\');
                ch = charReader.current();
                res.append(ch);
                if (ch == 'u') {   // unicode between \u0000 ~ \uFFFF
                    for (int i = 0; i < 4; i++) {
                        ch = charReader.next();
                        if (isHex(ch)) {
                            res.append(ch);
                        } else {
                            throw new JsonParseException("Invalid character");
                        }
                    }
                }
            } else if (ch == '"') {     // resolve completed
                return new Token(TokenType.STRING, res.toString());
            } else if (ch == '\r' || ch == '\n') {     // invalid
                throw new JsonParseException("Invalid character '\\r' or '\\n'");
            } else {    // regular string
                res.append(ch);
            }
        }
    }

    private boolean isHex(char ch) {
        return (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'f') || ('A' <= ch && ch <= 'F'));
    }

    private boolean isEscape() {
        char ch = charReader.next();
        return (ch == '"' || ch == '\\' || ch == 'u' || ch == 'r' || ch == 'n' || ch == 'b' || ch == 't' || ch == 'f' || ch == '/');
    }
}
