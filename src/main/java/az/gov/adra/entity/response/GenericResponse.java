package az.gov.adra.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GenericResponse {

    private Exception exception;
    private Integer status;
    private String description;
    private Object data;
    private LocalDateTime timestamp;
    private Integer totalPages;
    private String appName;

}
