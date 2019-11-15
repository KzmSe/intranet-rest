package az.gov.adra.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

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
    @Value("${spring.app-name}")
    private String appName;


    public static GenericResponse withSuccess(HttpStatus status, String description, Object object) {
        GenericResponse response = new GenericResponse();
        response.setStatus(status.value());
        response.setDescription(description);
        response.setData(object);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public static GenericResponse withException(HttpStatus status, String description, Exception exception) {
        GenericResponse response = new GenericResponse();
        response.setException(exception);
        response.setStatus(status.value());
        response.setDescription(description);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

}
