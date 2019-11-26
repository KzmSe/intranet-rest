package az.gov.adra.dataTransferObjects;

import az.gov.adra.entity.Post;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationForPostDTO {

    private Integer totalPages;
    private List<Post> posts;
    private List<PostDTO> postDTOS;

}
