package az.gov.adra.entity.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class GenericResponseBuilder {

    private Exception exception;
    private Integer status;
    private String description;
    private Object data;
    private LocalDateTime timestamp;
    private Integer totalPages;
    private String appName;

    public GenericResponseBuilder() {
        this.timestamp = LocalDateTime.now();
        this.appName = "INTRANET";
    }

    public GenericResponseBuilder withException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public GenericResponseBuilder withStatus(Integer status) {
        this.status = status;
        return this;
    }

    public GenericResponseBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public GenericResponseBuilder withData(Object data) {
        this.data = data;
        return this;
    }

    public GenericResponseBuilder withTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public GenericResponse build() {
        return new GenericResponse(exception, status, description, data, timestamp, totalPages, appName);
    }

}
