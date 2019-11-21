package az.gov.adra.repository;

import az.gov.adra.constant.ActivityConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.exception.EmployeeCredentialsException;
import az.gov.adra.repository.interfaces.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String findCountOfEmployeesByIdSql = "select count(*) as count from users where username = ? and enabled = ?";


    @Override
    public void isEmployeeExistWithGivenUsername(String username) throws EmployeeCredentialsException {
        int count = jdbcTemplate.queryForObject(findCountOfEmployeesByIdSql, new Object[] {username, ActivityConstants.ACTIVITY_STATUS_ACTIVE}, Integer.class);
        if (count <= 0) {
            throw new EmployeeCredentialsException(MessageConstants.ERROR_MESSAGE_EMPLOYEE_WITH_GIVEN_ID_NOT_FOUND);
        }
    }

}
