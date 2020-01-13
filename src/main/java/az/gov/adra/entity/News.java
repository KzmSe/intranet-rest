package az.gov.adra.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class News {

    private Integer id;
    private String title;
    private String newsLink;
    private String imgUrl;
    private LocalDateTime dateOfReg;
    private LocalDateTime dateOfDel;
    private Integer status;

}
