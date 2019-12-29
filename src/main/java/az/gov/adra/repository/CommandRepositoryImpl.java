package az.gov.adra.repository;

import az.gov.adra.constant.CommandConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.CommandDTO;
import az.gov.adra.entity.Command;
import az.gov.adra.entity.User;
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
import java.util.LinkedList;
import java.util.List;

@Repository
public class CommandRepositoryImpl implements CommandRepository {

    private static final String findAllCommandsSql = "select c.id as command_id, c.title, c.description, c.img_url, c.date_of_reg, u.name, u.surname, u.username from Command c inner join users u on c.username = u.username where c.status = ? order by c.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findCommandByCommandIdSql = "select c.id as command_id, c.title, c.description, c.img_url, c.date_of_reg, u.name, u.surname, u.username from Command c inner join users u on c.username = u.username where c.id = ? and c.status = ?";
    private static final String findTopThreeCommandsByLastAddedTimeSql = "select top 3 c.id as command_id, c.title, c.description, c.img_url, c.date_of_reg, u.name, u.surname, u.username from Command c inner join users u on c.username = u.username where c.status = ? order by c.date_of_reg desc";
    private static final String addCommandSql = "insert into Command(username, title, description, img_url, date_of_reg, status) values(?, ?, ?, ?, ?, ?)";
    private static final String findCountOfAllCommandsSql = "select count(*) as count from Command where status = ?";
    private static final String isCommandExistWithGivenIdSql = "select count(*) as count from Command where id = ? and status = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<CommandDTO> findAllCommands(int offset) {
        List<CommandDTO> commands = jdbcTemplate.query(findAllCommandsSql, new Object[]{CommandConstants.COMMAND_STATUS_ACTIVE, offset, CommandConstants.COMMAND_FETCH_NEXT}, new ResultSetExtractor<List<CommandDTO>>() {
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

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    command.setUser(user);

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

                User user = new User();
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setUsername(rs.getString("username"));

                command.setUser(user);

                return command;
            }
        });
        return command;
    }

    @Override
    public List<CommandDTO> findTopThreeCommandsByLastAddedTime() {
        List<CommandDTO> commands = jdbcTemplate.query(findTopThreeCommandsByLastAddedTimeSql, new Object[]{CommandConstants.COMMAND_STATUS_ACTIVE}, new ResultSetExtractor<List<CommandDTO>>() {
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

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    command.setUser(user);

                    list.add(command);
                }
                return list;
            }
        });
        return commands;
    }

    @Override
    public void addCommand(Command command) throws CommandCredentialsException {
        int affectedRows = jdbcTemplate.update(addCommandSql, command.getUser().getUsername(), command.getTitle(), command.getDescription(), command.getImgUrl(), command.getDateOfReg(), command.getStatus());
        if (affectedRows == 0) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public int findCountOfAllCommands() {
        int total = jdbcTemplate.queryForObject(findCountOfAllCommandsSql, new Object[]{CommandConstants.COMMAND_STATUS_ACTIVE}, Integer.class);
        return total;
    }

    @Override
    public void isCommandExistWithGivenId(int id) throws CommandCredentialsException {
        int count = jdbcTemplate.queryForObject(isCommandExistWithGivenIdSql, new Object[] {id, CommandConstants.COMMAND_STATUS_ACTIVE}, Integer.class);
        if (count <= 0) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_COMMAND_NOT_FOUND);
        }
    }

}
