package az.gov.adra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRespond {

    private Integer id;
    private Activity activity;
    private Employee employee;
    private Integer respond;
    private String dateOfReg;
    private String dateOfDel;
    private Integer status;

}
