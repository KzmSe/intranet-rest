package az.gov.adra.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private String username;
    private String password;
    private String token;
    private String name;
    private String surname;
    private String midname;
    private String gender;
    private LocalDate dateOfBirth;
    private String mobile;
    private String home;
    private String email;
    private Region region;
    private Department department;
    private Section section;
    private Position position;
    private String imgUrl;
    private LocalDateTime dateOfReg;
    private LocalDateTime dateOfDel;
    private Integer enabled;

}
