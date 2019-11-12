package az.gov.adra.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee {

    private Integer id;
    private Person person;
    private Department department;
    private Region region;
    private Section section;
    private Position position;
    private String hId;
    private String password;
    private Integer salary;
    private String token;
    private LocalDateTime dateOfReg;
    private LocalDateTime dateOfDel;
    private Integer status;

}
