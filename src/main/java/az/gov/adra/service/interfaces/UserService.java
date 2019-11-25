package az.gov.adra.service.interfaces;

import az.gov.adra.exception.UserCredentialsException;

public interface UserService {

    void isUserExistWithGivenUsername(String username) throws UserCredentialsException;

}
