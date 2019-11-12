package az.gov.adra.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse {

    private Exception exception;
    private Integer status;
    private String description;
    private Object data;
    private LocalDateTime timestamp;
    private String appName;

}
