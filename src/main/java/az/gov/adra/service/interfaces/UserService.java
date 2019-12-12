package az.gov.adra.service.interfaces;

import az.gov.adra.dataTransferObjects.UserDTOForAdvancedSearch;
import az.gov.adra.entity.User;
import az.gov.adra.exception.UserCredentialsException;

import java.util.List;

public interface UserService {

    List<User> findUsersByMultipleParameters(UserDTOForAdvancedSearch dto);

    int findCountOfUsersByMultipleParameters(UserDTOForAdvancedSearch dto);

    User findUserByEmail(String email) throws UserCredentialsException;

    void updatePassword(String password, String token) throws UserCredentialsException;

    void updateToken(String newToken, String oldToken) throws UserCredentialsException;

    void isUserExistWithGivenUsername(String username) throws UserCredentialsException;

}
