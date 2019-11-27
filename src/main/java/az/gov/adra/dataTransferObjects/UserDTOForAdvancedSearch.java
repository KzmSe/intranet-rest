package az.gov.adra.dataTransferObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTOForAdvancedSearch {

    private String name;
    private String surname;
    private Integer regionId;
    private Integer departmentId;
    private Integer sectionId;
    private Integer positionId;
    private Integer offset;
    private Integer fetchSize;

    public UserDTOForAdvancedSearch() {
        this.name = "none";
        this.surname = "none";
        this.regionId = 0;
        this.departmentId = 0;
        this.sectionId = 0;
        this.positionId = 0;
        this.offset = 0;
        this.fetchSize = 0;
    }
}
