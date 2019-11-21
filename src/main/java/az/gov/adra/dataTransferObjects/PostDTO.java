package az.gov.adra.dataTransferObjects;

import az.gov.adra.entity.Employee;
import az.gov.adra.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO {

    private int id;
    private User user;
    private String title;
    private String description;
    private int viewCount;
    private String imgUrl;
    private int likeCount;
    private int dislikeCount;
    private String dateOfReg;
    private String dateOfDel;
    private int status;


}
