package railway_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Individual {
    private int id;
    private String firstName;
    private String secondName;
    private String email;
    private String login;
    private String password;
    private String activation;
    private String remember;
    private String reset;
    private int activated;
}
