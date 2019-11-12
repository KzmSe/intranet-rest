package az.gov.adra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Activity {

    private Integer id;
    private Employee employee;
    private String title;
    private String description;
    private Integer viewCount;
    private String imgUrl;
    private String dateOfReg;
    private String dateOfDel;
    private Integer status;

}
