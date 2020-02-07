package az.gov.adra.controller;

import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.UserDTOForAdvancedSearch;
import az.gov.adra.dataTransferObjects.UserDTOForSendEmail;
import az.gov.adra.dataTransferObjects.UserDTOForUpdateUser;
import az.gov.adra.entity.User;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.entity.response.GenericResponseBuilder;
import az.gov.adra.exception.UserCredentialsException;
import az.gov.adra.service.interfaces.UserService;
import az.gov.adra.util.EmailSenderUtil;
import az.gov.adra.util.ResourceUtil;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    @Value("${spring.mail.subject}")
    private String subject;
    @Value("${spring.mail.body}")
    private String body;

    @PostMapping("/users/search")
    public GenericResponse findUsersByMultipleParameters(@RequestBody UserDTOForAdvancedSearch dto) {
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

        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("list of users by multiple parameters")
                .withData(users)
                .withTotalPages(totalPages)
                .build();
    }

    @PostMapping("/users/email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendEmail(@RequestBody UserDTOForSendEmail dto) throws UserCredentialsException, AddressException {
        if (ValidationUtil.isNullOrEmpty(dto.getEmail())) {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }
        User user = userService.findUserByEmail(dto.getEmail().trim());

        sendNewPasswordEmail(dto, user);
    }

    @PutMapping("/users/password/token")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updatePasswordByToken(@RequestBody UserDTOForUpdateUser dto) throws UserCredentialsException {
        if (ValidationUtil.isNullOrEmpty(dto.getToken(), dto.getPassword(), dto.getConfirmPassword())) {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (dto.getPassword().trim().length() >= 8 && dto.getConfirmPassword().trim().length() >= 8) {
            if (dto.getPassword().equals(dto.getConfirmPassword())) {
                try {
                    userService.updatePasswordByToken(encoder.encode(dto.getPassword()), dto.getToken());
                    String newToken = UUID.randomUUID().toString();
                    userService.updateToken(newToken, dto.getToken());

                } catch (UserCredentialsException e) {
                    throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
                }
            } else {
                throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_NEWPASSWORD_AND_CONFIRMPASSWORD_MUST_BE_SAME);
            }
        } else {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_PASSWORD_MUST_CONTAINS_MINIMUM_8_CHARACTERS);
        }
    }

    @PutMapping("/users/password")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updatePasswordByUsername(@RequestBody UserDTOForUpdateUser dto,
                                         Principal principal) throws UserCredentialsException {
        if (ValidationUtil.isNullOrEmpty(dto.getPassword(), dto.getConfirmPassword())) {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (dto.getPassword().trim().length() >= 8 && dto.getConfirmPassword().trim().length() >= 8) {
            if (dto.getPassword().equals(dto.getConfirmPassword())) {
                try {
                    userService.updatePasswordByUsername(encoder.encode(dto.getPassword()), principal.getName());

                } catch (UserCredentialsException e) {
                    throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
                }
            } else {
                throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_NEWPASSWORD_AND_CONFIRMPASSWORD_MUST_BE_SAME);
            }
        } else {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_PASSWORD_MUST_CONTAINS_MINIMUM_8_CHARACTERS);
        }
    }


    //private methods
    private void sendNewPasswordEmail(UserDTOForSendEmail dto, User user) throws AddressException {
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            ExecutorService service = Executors.newSingleThreadExecutor();

            Address address = new InternetAddress(dto.getEmail());

            Runnable runnableTask = () -> {
                emailSenderUtil.sendEmailMessage(address, subject, String.format(body, user.getToken()));
            };

            service.submit(runnableTask);
            service.shutdown();
        }
    }

}
