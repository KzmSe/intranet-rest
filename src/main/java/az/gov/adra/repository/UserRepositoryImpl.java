package az.gov.adra.repository;

import az.gov.adra.constant.ActivityConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.exception.UserCredentialsException;
import az.gov.adra.repository.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String findCountOfUserByUsernameSql = "select count(*) as count from users where username = ? and enabled = ?";


    @Override
    public void isUserExistWithGivenUsername(String username) throws UserCredentialsException {
        int count = jdbcTemplate.queryForObject(findCountOfUserByUsernameSql, new Object[] {username, ActivityConstants.ACTIVITY_STATUS_ACTIVE}, Integer.class);
        if (count <= 0) {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_EMPLOYEE_WITH_GIVEN_ID_NOT_FOUND);
        }
    }

}
