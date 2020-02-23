package json.util;

/**
 * @author ShaoJiale
 * Date: 2020/2/22
 */
public class CharReader {
    private int size;
    private int index;
    private char[] data;

    public CharReader(String jsonStr) {
        this.data = jsonStr.toCharArray();
        this.size = jsonStr.length();
        index = 0;
    }

    public char current() {
        if (index >= size) {
            return (char) -1;
        }
        return data[Math.max(0, index)];
    }

    public char next() {
        if (index >= size) {
            return (char) -1;
        }
        return data[index++];
    }

    public void back() {
        index = Math.max(0, index - 1);
    }

    public boolean hasMore() {
        return index < size;
    }
}
