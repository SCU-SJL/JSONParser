package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author ShaoJiale
 * Date: 2020/2/24
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArrayBean {
    String[] strings = new String[]{"123", "hello"};
    Integer[] nums = new Integer[]{1, 2, 3, 4, 5};
    Boolean[] booleans = new Boolean[]{true, false, true, false};
    Double[] doubles = new Double[]{1.1, 2.2, 3.3};
}
