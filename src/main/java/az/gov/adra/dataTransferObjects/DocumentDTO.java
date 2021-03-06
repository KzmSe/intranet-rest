package az.gov.adra.dataTransferObjects;

import az.gov.adra.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentDTO {

    private Integer id;
    private User user;
    private String title;
    private String fileUrl;
    private File file;
    private String dateOfReg;
    private String dateOfDel;
    private Integer status;

}
