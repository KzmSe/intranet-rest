package az.gov.adra.dataTransferObjects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTOForUpdateUser {

    private String mobile;
    private String home;
    private String token;
    private String password;
    private String confirmPassword;
    private MultipartFile multipartFile;

}
