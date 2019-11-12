package az.gov.adra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private Integer id;
    private Role role;
    private String name;
    private String surname;
    private String midname;
    private String imgUrl;
    private String gender;
    private LocalDate dateOfBirth;
    private String mobile;
    private String home;
    private String email;
    private Integer status;

}
