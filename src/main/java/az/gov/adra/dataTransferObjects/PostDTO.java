package az.gov.adra.dataTransferObjects;

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

    private Integer id;
    private User user;
    private String title;
    private String description;
    private Integer viewCount;
    private String imgUrl;
    private Integer likeCount;
    private Integer dislikeCount;
    private String dateOfReg;
    private String dateOfDel;
    private Integer status;


}
