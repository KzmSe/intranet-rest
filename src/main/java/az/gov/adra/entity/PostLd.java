package az.gov.adra.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostLd {

    private Integer id;
    private User user;
    private Post post;
    private Integer likeDislike;
    private String dateOfReg;
    private String dateOfDel;
    private Integer status;

}
