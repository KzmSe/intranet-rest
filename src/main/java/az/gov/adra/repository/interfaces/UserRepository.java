package az.gov.adra.repository.interfaces;

import az.gov.adra.exception.UserCredentialsException;

public interface UserRepository {

    void isUserExistWithGivenUsername(String username) throws UserCredentialsException;

}
