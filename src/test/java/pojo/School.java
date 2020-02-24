package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author ShaoJiale
 * Date: 2020/2/20
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class School {
    private String schoolName;
    private Building[] buildings;
}
