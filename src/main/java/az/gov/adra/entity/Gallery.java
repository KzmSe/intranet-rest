package az.gov.adra.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gallery {

    private Integer id;
    private User user;
    private String title;
    private String imgUrl;
    private String dateOfReg;
    private String dateOfDel;
    private Integer status;

}
