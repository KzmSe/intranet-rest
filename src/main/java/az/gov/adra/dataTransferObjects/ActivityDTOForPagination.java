package az.gov.adra.dataTransferObjects;

import az.gov.adra.entity.Activity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDTOForPagination {

    private Integer totalPages;
    private List<Activity> activities;
    private List<ActivityDTO> activityDTOS;

}
