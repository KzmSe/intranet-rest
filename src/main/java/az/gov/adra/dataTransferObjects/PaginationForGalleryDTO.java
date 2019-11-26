package az.gov.adra.dataTransferObjects;

import az.gov.adra.entity.Gallery;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationForGalleryDTO {

    private Integer totalPages;
    private List<Gallery> galleries;

}
