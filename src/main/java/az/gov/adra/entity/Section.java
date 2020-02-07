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
public class Section {

    private Integer id;
    private Department department;
    private String name;
    private LocalDateTime dateOfReg;
    private LocalDateTime dateOfDel;
    private Integer status;

}
