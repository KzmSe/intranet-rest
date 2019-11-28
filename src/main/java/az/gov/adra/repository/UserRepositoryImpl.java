package az.gov.adra.repository;

import az.gov.adra.constant.ActivityConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.constant.UserConstants;
import az.gov.adra.dataTransferObjects.UserDTOForAdvancedSearch;
import az.gov.adra.entity.Position;
import az.gov.adra.entity.User;
import az.gov.adra.exception.UserCredentialsException;
import az.gov.adra.repository.interfaces.UserRepository;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String FIND_USERS_BY_MULTIPLE_PARAMETERS_SQL = "select u.name, u.surname, u.email, u.img_url, p.name as position_name from users u inner join Position p on u.position_id = p.id where u.enabled = ? ";
    private static final String FIND_COUNT_OF_USERS_BY_MULTIPLE_PARAMETERS_SQL = "select count(*) as count from users u inner join Position p on u.position_id = p.id where u.enabled = ? ";
    private static final String IS_USER_EXIST_WITH_GIVEN_USERNAME_SQL = "select count(*) as count from users where and enabled = ?";


    @Override
    public List<User> findUsersByMultipleParameters(UserDTOForAdvancedSearch dto) {
        StringBuilder builder = new StringBuilder(FIND_USERS_BY_MULTIPLE_PARAMETERS_SQL);
        List<Object> list = new LinkedList<>();

        list.add(UserConstants.USER_STATUS_ENABLED);

        if (!ValidationUtil.isNull(dto.getName())) {
            builder.append("and u.name = ? ");
            list.add(dto.getName());
        }

        if (!ValidationUtil.isNull(dto.getSurname())) {
            builder.append("and u.surname = ? ");
            list.add(dto.getSurname());
        }

        if (!ValidationUtil.isNull(dto.getKeyword())) {
            builder.append("and CONCAT(u.name , ' ', u.surname) like ? ");
            list.add("%" + dto.getKeyword() + "%");
        }

        if (!ValidationUtil.isNull(dto.getRegionId())) {
            builder.append("and u.region_id = ? ");
            list.add(dto.getRegionId());
        }

        if (!ValidationUtil.isNull(dto.getPositionId())) {
            builder.append("and u.position_id = ? ");
            list.add(dto.getPositionId());
        }

        if (!ValidationUtil.isNull(dto.getDepartmentId())) {
            builder.append("and u.department_id = ? ");
            list.add(dto.getDepartmentId());
        }

        if (!ValidationUtil.isNull(dto.getSectionId())) {
            builder.append("and u.section_id = ? ");
            list.add(dto.getSectionId());
        }

        builder.append("order by u.name, u.surname OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        list.add(dto.getOffset());
        list.add(UserConstants.USER_FETCH_NEXT);

        Object[] parameters = list.toArray();

        List<User> users = jdbcTemplate.query(builder.toString(), parameters, new ResultSetExtractor<List<User>>() {
            @Override
            public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<User> usersList = new LinkedList<>();
                while (rs.next()) {
                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));

                    user.setEmail(rs.getString("email"));
                    user.setImgUrl(rs.getString("img_url"));

                    Position position = new Position();
                    position.setName(rs.getString("position_name"));

                    user.setPosition(position);

                    usersList.add(user);
                }
                return usersList;
            }
        });

        return users;
    }

    @Override
    public int findCountOfUsersByMultipleParameters(UserDTOForAdvancedSearch dto) {
        StringBuilder builder = new StringBuilder(FIND_COUNT_OF_USERS_BY_MULTIPLE_PARAMETERS_SQL);
        List<Object> list = new LinkedList<>();

        list.add(UserConstants.USER_STATUS_ENABLED);

        if (!ValidationUtil.isNull(dto.getName())) {
            builder.append("and u.name = ? ");
            list.add(dto.getName());
        }

        if (!ValidationUtil.isNull(dto.getSurname())) {
            builder.append("and u.surname = ? ");
            list.add(dto.getSurname());
        }

        if (!ValidationUtil.isNull(dto.getKeyword())) {
            builder.append("and CONCAT(u.name , ' ', u.surname) like ? ");
            list.add("%" + dto.getKeyword() + "%");
        }

        if (!ValidationUtil.isNull(dto.getRegionId())) {
            builder.append("and u.region_id = ? ");
            list.add(dto.getRegionId());
        }

        if (!ValidationUtil.isNull(dto.getPositionId())) {
            builder.append("and u.position_id = ? ");
            list.add(dto.getPositionId());
        }

        if (!ValidationUtil.isNull(dto.getDepartmentId())) {
            builder.append("and u.department_id = ? ");
            list.add(dto.getDepartmentId());
        }

        if (!ValidationUtil.isNull(dto.getSectionId())) {
            builder.append("and u.section_id = ? ");
            list.add(dto.getSectionId());
        }

        Object[] parameters = list.toArray();

        int totalCount = jdbcTemplate.queryForObject(builder.toString(), parameters, Integer.class);
        return totalCount;
    }

    @Override
    public void isUserExistWithGivenUsername(String username) throws UserCredentialsException {
        int count = jdbcTemplate.queryForObject(IS_USER_EXIST_WITH_GIVEN_USERNAME_SQL, new Object[] {username, ActivityConstants.ACTIVITY_STATUS_ACTIVE}, Integer.class);
        if (count <= 0) {
            throw new UserCredentialsException(MessageConstants.ERROR_MESSAGE_EMPLOYEE_WITH_GIVEN_ID_NOT_FOUND);
        }
    }

}
