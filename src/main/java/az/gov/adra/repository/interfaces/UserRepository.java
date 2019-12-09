package az.gov.adra.repository.interfaces;

import az.gov.adra.dataTransferObjects.UserDTOForAdvancedSearch;
import az.gov.adra.entity.User;
import az.gov.adra.exception.UserCredentialsException;

import java.util.List;

public interface UserRepository {

    List<User> findUsersByMultipleParameters(UserDTOForAdvancedSearch dto);

    int findCountOfUsersByMultipleParameters(UserDTOForAdvancedSearch dto);

    User findUserByEmail(String email) throws UserCredentialsException;

    void isUserExistWithGivenUsername(String username) throws UserCredentialsException;

}
