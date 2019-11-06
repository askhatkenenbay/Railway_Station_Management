package railway_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Employee {
    private int paymentForHour;
    private String type;
    private String workStartTime;
    private String workEndTime;
    private String workDays;
    private int id;
    private int individualId;
}
