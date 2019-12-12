package az.gov.adra.controller;

import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.UserDTOForAdvancedSearch;
import az.gov.adra.dataTransferObjects.UserDTOForSendEmail;
import az.gov.adra.dataTransferObjects.UserDTOForUpdateUser;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailSenderUtil emailSenderUtil;
    @Autowired
    private BCryptPasswordEncoder encoder;
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
    public void sendEmail(@RequestBody UserDTOForSendEmail dto) throws UserCredentialsException {
        if (ValidationUtil.isNullOrEmpty(dto.getEmail())) {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }
        User user = userService.findUserByEmail(dto.getEmail().trim());
        //TODO: add thread to send email!
        emailSenderUtil.sendEmailMessage(dto.getEmail(), subject, String.format(body, user.getToken()));
    }

    @PutMapping("/users/password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updatePassword(@RequestBody UserDTOForUpdateUser dto,
                               Principal principal) throws UserCredentialsException {
        if (ValidationUtil.isNullOrEmpty(dto.getToken()) && ValidationUtil.isNullOrEmpty(dto.getPassword()) && ValidationUtil.isNullOrEmpty(dto.getConfirmPassword())) {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        User user = new User();
        user.setUsername(principal.getName());

        if (dto.getPassword().trim().length() >= 8 && dto.getConfirmPassword().trim().length() >= 8) {
            if (dto.getPassword().equals(dto.getConfirmPassword())) {
                try {
                    userService.updatePassword(encoder.encode(dto.getPassword()), dto.getToken());
                    String newToken = UUID.randomUUID().toString();
                    userService.updateToken(newToken, dto.getToken());

                } catch (UserCredentialsException e) {
                    throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
                }
            } else {
                throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
            }
        } else {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_PASSWORD_MUST_CONTAINS_MINIMUM_8_CHARACTERS);
        }
    }

}
