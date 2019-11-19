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
    private int Salary;
    private String type;
    private String workSince;
    private int id;
    private int individualId;
    private String firstName;
    private String lastName;
}
