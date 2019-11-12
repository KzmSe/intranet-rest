package az.gov.adra.dataTransferObjects;

import az.gov.adra.entity.Employee;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDTO {

    private int id;
    private Employee employee;
    private String title;
    private String description;
    private int viewCount;
    private String imgUrl;
    private int positiveCount;
    private int negativeCount;
    private String dateOfReg;
    private String dateOfDel;
    private int status;

}
