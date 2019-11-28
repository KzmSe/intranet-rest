package az.gov.adra.controller;

import az.gov.adra.dataTransferObjects.UserDTOForAdvancedSearch;
import az.gov.adra.entity.User;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.service.interfaces.UserService;
import az.gov.adra.util.ResourceUtil;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Value("${auth.server.paths.count-of-all-users}")
    private String countOfAllUsersUrl;

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

}
