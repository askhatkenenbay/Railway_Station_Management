package railway_system.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Train {
    private int id;
    private String companyName;
    private boolean isActive;
    private int trainTypeId;

}
