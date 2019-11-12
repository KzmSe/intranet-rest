package az.gov.adra.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityReview {

    private Integer id;
    private Activity activity;
    private Employee employee;
    private String description;
    private String dateOfReg;
    private String dateOfDel;
    private Integer status;

}
