package railway_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class TrainType {
    private int Id;
    private int wagonAmount;
    private int wagonCapacity;
    private int price;
}
