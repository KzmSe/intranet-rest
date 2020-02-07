package az.gov.adra.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exception {

    private String code;
    private String message;
    private String errorStack;



}
