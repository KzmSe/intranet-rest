package az.gov.adra.dataTransferObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTOForAdvancedSearch {

    private String name;
    private String surname;
    private String keyword;
    private Integer regionId;
    private Integer positionId;
    private Integer departmentId;
    private Integer sectionId;
    private Integer page;
    private Integer offset;

}
