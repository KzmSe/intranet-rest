package az.gov.adra.controller;

import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.UserDTOForAdvancedSearch;
import az.gov.adra.entity.User;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.UserCredentialsException;
import az.gov.adra.service.interfaces.UserService;
import az.gov.adra.util.EmailSenderUtil;
import az.gov.adra.util.ResourceUtil;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailSenderUtil emailSenderUtil;
    @Value("${auth.server.paths.count-of-all-users}")
    private String countOfAllUsersUrl;
    @Value("${spring.email.changePassword.subject}")
    private String subject;
    @Value("${spring.email.changePassword.body}")
    private String body;

    @PostMapping("/users/search")
    public GenericResponse findUsersByMultipleParameters(@RequestBody UserDTOForAdvancedSearch dto,
                                                         HttpServletResponse response) {
        int total = userService.findCountOfUsersByMultipleParameters(dto);
        int totalPages = 0;
        int offset = 0;

        if (total != 0) {
            totalPages = (int) Math.ceil((double) total / 10);

            if (dto.getPage() != null && dto.getPage() >= totalPages) {
                offset = (totalPages - 1) * 10;

            } else if (dto.getPage() != null && dto.getPage() > 1) {
                offset = (dto.getPage() - 1) * 10;
            };
        }

        dto.setOffset(offset);

        List<User> users = userService.findUsersByMultipleParameters(dto);
        for (User user : users) {
            if (user.getImgUrl() == null) {
                continue;
            }
            user.setImgUrl(ResourceUtil.convertToString(user.getImgUrl()));
        }

        response.setIntHeader("Total-Pages", totalPages);
        return GenericResponse.withSuccess(HttpStatus.OK, "list of users by multiple parameters", users);
    }

    @PostMapping("/users/email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendEmail(@RequestParam(value = "email", required = false) String email) throws UserCredentialsException {
        if (ValidationUtil.isNullOrEmpty(email)) {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }
        User user = userService.findUserByEmail(email.trim());
        //TODO: add thread to send email!
        emailSenderUtil.sendEmailMessage(email, subject, String.format(body, user.getToken()));
    }

}
