package az.gov.adra.repository;

import az.gov.adra.constant.AnnouncementConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.entity.Announcement;
import az.gov.adra.entity.Employee;
import az.gov.adra.entity.Person;
import az.gov.adra.exception.AnnouncementCredentialsException;
import az.gov.adra.repository.interfaces.AnnouncementRepository;
import az.gov.adra.util.TimeParserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnnouncementRepositoryImpl implements AnnouncementRepository {

    private static final String findAllAnnouncementsByImportanceLevelSql = "select a.id as announcement_id, a.title, a.description, a.date_of_reg, e.id as employee_id, p.name, p.surname from Announcement a inner join Employee e on a.employee_id = e.id inner join Person p on e.person_id = p.id where a.importance_level = ? and a.status = ? order by a.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findAnnouncementByAnnouncementIdSql = "select a.id as announcement_id, a.title, a.description, a.date_of_reg, p.id as person_id, p.name, p.surname from Announcement a inner join Employee e on a.employee_id = e.id inner join Person p on e.person_id = p.id where a.id = ? and a.status = ?";
    private static final String findTopAnnouncementByImportanceLevelSql = "select top 1 a.id as announcement_id, a.title, a.description, a.date_of_reg, e.id as employee_id, p.name, p.surname from Announcement a inner join Employee e on a.employee_id = e.id inner join Person p on e.person_id = p.id where a.importance_level = ? and a.status = ? order by a.date_of_reg desc";
    private static final String isAnnouncementExistWithGivenIdSql = "select count(*) as count from Announcement where id = ? and status = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Announcement> findAllAnnouncementsByImportanceLevel(String importanceLevel, int fetchNext) {
        List<Announcement> announcements = jdbcTemplate.query(findAllAnnouncementsByImportanceLevelSql, new Object[]{importanceLevel, AnnouncementConstants.ANNOUNCEMENT_STATUS_ACTIVE, AnnouncementConstants.ANNOUNCEMENT_OFFSET_NUMBER, fetchNext}, new ResultSetExtractor<List<Announcement>>() {
            @Override
            public List<Announcement> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Announcement> list = new ArrayList<>();
                while (rs.next()) {
                    Announcement announcement = new Announcement();
                    announcement.setId(rs.getInt("announcement_id"));
                    announcement.setTitle(rs.getString("title"));
                    announcement.setDescription(rs.getString("description"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    announcement.setDateOfReg(dateOfReg.format(TimeParserUtil.DATETIME_FORMATTER));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));

                    Person person = new Person();
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    employee.setPerson(person);
                    announcement.setEmployee(employee);

                    list.add(announcement);
                }
                return list;
            }
        });
        return announcements;
    }

    @Override
    public Announcement findAnnouncementByAnnouncementId(int id) {
        Announcement announcement = jdbcTemplate.queryForObject(findAnnouncementByAnnouncementIdSql, new Object[]{id, AnnouncementConstants.ANNOUNCEMENT_STATUS_ACTIVE}, new RowMapper<Announcement>() {
            @Override
            public Announcement mapRow(ResultSet rs, int i) throws SQLException {
                Announcement announcement1 = new Announcement();
                announcement1.setId(rs.getInt("announcement_id"));
                announcement1.setTitle(rs.getString("title"));
                announcement1.setDescription(rs.getString("description"));

                LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                announcement1.setDateOfReg(dateOfReg.format(TimeParserUtil.DATETIME_FORMATTER));

                Person person = new Person();
                person.setId(rs.getInt("person_id"));
                person.setName(rs.getString("name"));
                person.setSurname(rs.getString("surname"));

                Employee employee = new Employee();
                employee.setPerson(person);
                announcement1.setEmployee(employee);

                return announcement1;
            }
        });
        return announcement;
    }

    @Override
    public Announcement findTopAnnouncementByImportanceLevel(String importanceLevel) {
        Announcement announcement = jdbcTemplate.queryForObject(findTopAnnouncementByImportanceLevelSql, new Object[] {importanceLevel, AnnouncementConstants.ANNOUNCEMENT_STATUS_ACTIVE}, new RowMapper<Announcement>() {
            @Override
            public Announcement mapRow(ResultSet rs, int i) throws SQLException {
                Announcement announcement1 = new Announcement();
                announcement1.setId(rs.getInt("announcement_id"));
                announcement1.setTitle(rs.getString("title"));
                announcement1.setDescription(rs.getString("description"));

                LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                announcement1.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                Employee employee = new Employee();
                employee.setId(rs.getInt("employee_id"));

                Person person = new Person();
                person.setName(rs.getString("name"));
                person.setSurname(rs.getString("surname"));

                employee.setPerson(person);
                announcement1.setEmployee(employee);

                return announcement1;
            }
        });
        return announcement;
    }

    @Override
    public void isAnnouncementExistWithGivenId(int id) throws AnnouncementCredentialsException {
        int count = jdbcTemplate.queryForObject(isAnnouncementExistWithGivenIdSql, new Object[] {id, AnnouncementConstants.ANNOUNCEMENT_STATUS_ACTIVE}, Integer.class);
        if (count <= 0) {
            throw new AnnouncementCredentialsException(MessageConstants.ERROR_MESSAGE_ANNOUNCEMENT_NOT_FOUND);
        }
    }

}
