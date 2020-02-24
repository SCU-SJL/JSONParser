package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author ShaoJiale
 * Date: 2020/2/20
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Building {
    private int id;
    private String buildingName;
}
