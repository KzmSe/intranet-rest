package az.gov.adra.dataTransferObjects;

import az.gov.adra.entity.Announcement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationForAnnouncementDTO {

    private Integer totalPages;
    private List<Announcement> announcements;

}
