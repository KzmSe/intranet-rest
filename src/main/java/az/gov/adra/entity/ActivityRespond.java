package az.gov.adra.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityRespond {

    private Integer id;
    private Activity activity;
    private User user;
    private Integer respond;
    private String dateOfReg;
    private String dateOfDel;
    private Integer status;

}
