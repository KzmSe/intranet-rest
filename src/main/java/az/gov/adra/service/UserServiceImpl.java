package az.gov.adra.service;

import az.gov.adra.dataTransferObjects.UserDTOForAdvancedSearch;
import az.gov.adra.entity.User;
import az.gov.adra.exception.UserCredentialsException;
import az.gov.adra.repository.interfaces.UserRepository;
import az.gov.adra.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> findUsersByMultipleParameters(UserDTOForAdvancedSearch dto) {
        return userRepository.findUsersByMultipleParameters(dto);
    }

    @Override
    public int findCountOfUsersByMultipleParameters(UserDTOForAdvancedSearch dto) {
        return userRepository.findCountOfUsersByMultipleParameters(dto);
    }

    @Override
    public User findUserByEmail(String email) throws UserCredentialsException {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void updatePassword(String password, String token) throws UserCredentialsException {
        userRepository.updatePassword(password, token);
    }

    @Override
    public void updateToken(String newToken, String oldToken) throws UserCredentialsException {
        userRepository.updateToken(newToken, oldToken);
    }

    @Override
    public void isUserExistWithGivenUsername(String username) throws UserCredentialsException {
        userRepository.isUserExistWithGivenUsername(username);
    }

}
