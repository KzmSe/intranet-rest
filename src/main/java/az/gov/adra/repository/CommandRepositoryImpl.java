package az.gov.adra.repository;

import az.gov.adra.constant.CommandConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.CommandDTO;
import az.gov.adra.entity.Command;
import az.gov.adra.entity.Employee;
import az.gov.adra.entity.Person;
import az.gov.adra.exception.CommandCredentialsException;
import az.gov.adra.repository.interfaces.CommandRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Repository
public class CommandRepositoryImpl implements CommandRepository {

    private static final String findAllCommandsSql = "select c.id as command_id, c.title, c.description, c.img_url, c.date_of_reg, p.id as person_id, p.name, p.surname from Command c inner join Employee e on c.employee_id = e.id inner join Person p on e.person_id = p.id where c.status = ? order by date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findCommandByCommandIdSql = "select c.id as command_id, c.title, c.description, c.img_url, c.date_of_reg, p.id as person_id, p.name, p.surname from Command c inner join Employee e on c.employee_id = e.id inner join Person p on e.person_id = p.id where c.id = ? and c.status = ?";
    private static final String findTopCommandSql = "select top 1 c.id as command_id, c.title, c.description, c.date_of_reg, p.id as person_id, p.name, p.surname from Command c inner join Employee e on c.employee_id = e.id inner join Person p on e.person_id = p.id where c.status = ? order by c.date_of_reg desc";
    private static final String addCommandSql = "insert into Command(employee_id, title, description, img_url, date_of_reg, status) values(?, ?, ?, ?, ?, ?)";
    private static final String isCommandExistWithGivenIdSql = "select count(*) as count from Command where id = ? and status = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<CommandDTO> findAllCommands(int fetchNext) {
        List<CommandDTO> commands = jdbcTemplate.query(findAllCommandsSql, new Object[]{CommandConstants.COMMAND_STATUS_ACTIVE, CommandConstants.COMMAND_OFFSET_NUMBER, fetchNext}, new ResultSetExtractor<List<CommandDTO>>() {
            @Override
            public List<CommandDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<CommandDTO> list = new LinkedList<>();
                while (rs.next()) {
                    CommandDTO command = new CommandDTO();
                    command.setId(rs.getInt("command_id"));
                    command.setTitle(rs.getString("title"));
                    command.setDescription(rs.getString("description"));
                    command.setImgUrl(rs.getString("img_url") != null ? rs.getString("img_url") : null);

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    command.setDateOfReg(dateOfReg.format(TimeParserUtil.DATETIME_FORMATTER));

                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setPerson(person);
                    command.setEmployee(employee);

                    list.add(command);
                }
                return list;
            }
        });
        return commands;
    }

    @Override
    public CommandDTO findCommandByCommandId(int id) {
        CommandDTO command = jdbcTemplate.queryForObject(findCommandByCommandIdSql, new Object[]{id, CommandConstants.COMMAND_STATUS_ACTIVE}, new RowMapper<CommandDTO>() {
            @Override
            public CommandDTO mapRow(ResultSet rs, int i) throws SQLException {
                CommandDTO command = new CommandDTO();
                command.setId(rs.getInt("command_id"));
                command.setTitle(rs.getString("title"));
                command.setDescription(rs.getString("description"));
                command.setImgUrl(rs.getString("img_url") != null ? rs.getString("img_url") : null);

                LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                command.setDateOfReg(dateOfReg.format(TimeParserUtil.DATETIME_FORMATTER));

                Person person = new Person();
                person.setId(rs.getInt("person_id"));
                person.setName(rs.getString("name"));
                person.setSurname(rs.getString("surname"));

                Employee employee = new Employee();
                employee.setPerson(person);
                command.setEmployee(employee);

                return command;
            }
        });
        return command;
    }

    @Override
    public void addCommand(Command command) throws CommandCredentialsException {
        int affectedRows = jdbcTemplate.update(addCommandSql, command.getEmployee().getId(), command.getTitle(), command.getDescription(), command.getImgUrl(), command.getDateOfReg(), command.getStatus());
        if (affectedRows == 0) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public void isCommandExistWithGivenId(int id) throws CommandCredentialsException {
        int count = jdbcTemplate.queryForObject(isCommandExistWithGivenIdSql, new Object[] {id, CommandConstants.COMMAND_STATUS_ACTIVE}, Integer.class);
        if (count <= 0) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_COMMAND_NOT_FOUND);
        }
    }

}
