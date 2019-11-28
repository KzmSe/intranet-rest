package az.gov.adra.repository;

import az.gov.adra.constant.DepartmentConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.entity.Department;
import az.gov.adra.exception.DepartmentCredentialsException;
import az.gov.adra.repository.interfaces.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private static final String FIND_ALL_DEPARTMENTS_SQL = "select * from Department where status = ? and name is not null order by name";
    private static final String IS_DEPARTMENT_EXIST_WITH_GIVEN_ID_SQL = "select count(*) as count from Department where id = ? and status = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Department> findAllDepartments() {
        List<Department> departments = jdbcTemplate.query(FIND_ALL_DEPARTMENTS_SQL, new Object[]{DepartmentConstants.DEPARTMENT_STATUS_ACTIVE}, new ResultSetExtractor<List<Department>>() {
            @Override
            public List<Department> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Department> list = new LinkedList<>();
                while (rs.next()) {
                    Department department = new Department();
                    department.setId(rs.getInt("id"));
                    department.setName(rs.getString("name"));

                    list.add(department);
                }
                return list;
            };
        });
        return departments;
    }

    @Override
    public void isDepartmentExistWithGivenId(int id) throws DepartmentCredentialsException {
        int count = jdbcTemplate.queryForObject(IS_DEPARTMENT_EXIST_WITH_GIVEN_ID_SQL, new Object[] {id, DepartmentConstants.DEPARTMENT_STATUS_ACTIVE}, Integer.class);
        if (count <= 0) {
            throw new DepartmentCredentialsException(MessageConstants.ERROR_MESSAGE_DEPARTMENT_NOT_FOUND);
        }
    }

}
