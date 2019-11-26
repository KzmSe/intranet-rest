package az.gov.adra.dataTransferObjects;

import az.gov.adra.entity.Command;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationForCommandDTO {

    private Integer totalPages;
    private List<Command> commands;
    private List<CommandDTO> commandDTOS;

}
