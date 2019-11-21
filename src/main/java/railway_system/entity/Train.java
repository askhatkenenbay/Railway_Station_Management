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
    private int trainTypeId;
    private boolean isActive;

}
