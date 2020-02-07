package az.gov.adra.repository;

import az.gov.adra.constant.PositionConstants;
import az.gov.adra.entity.Position;
import az.gov.adra.repository.interfaces.PositionRepository;
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
public class PositionRepositoryImpl implements PositionRepository {

    private static final String findAllPositionsSql = "select * from Position where status = ? order by name";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Position> findAllPositions() {
        List<Position> positions = jdbcTemplate.query(findAllPositionsSql, new Object[]{PositionConstants.POSITION_STATUS_ACTIVE}, new ResultSetExtractor<List<Position>>() {
            @Override
            public List<Position> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Position> list = new LinkedList<>();
                while (rs.next()) {
                    Position position = new Position();
                    position.setId(rs.getInt("id"));
                    position.setName(rs.getString("name"));

                    list.add(position);
                }
                return list;
            };
        });
        return positions;
    }

}
