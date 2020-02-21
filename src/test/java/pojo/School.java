package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ShaoJiale
 * Date: 2020/2/20
 */
@Data
@AllArgsConstructor
public class School {
    private String schoolName;
    private Building[] buildings;
}
