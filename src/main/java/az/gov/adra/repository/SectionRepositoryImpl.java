package az.gov.adra.repository;

import az.gov.adra.constant.SectionConstants;
import az.gov.adra.entity.Section;
import az.gov.adra.repository.interfaces.SectionRepository;
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
public class SectionRepositoryImpl implements SectionRepository {

    private static final String FIND_ALL_SECTIONS_SQL = "select * from Section where status = ? and name is not null order by name";
    private static final String FIND_SECTIONS_BY_DEPARTMENT_ID_SQL = "select id, name from Section where status = ? and department_id = ? and name is not null order by name";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Section> findAllSections() {
        List<Section> sections = jdbcTemplate.query(FIND_ALL_SECTIONS_SQL, new Object[]{SectionConstants.SECTION_STATUS_ACTIVE}, new ResultSetExtractor<List<Section>>() {
            @Override
            public List<Section> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Section> list = new LinkedList<>();
                while (rs.next()) {
                    Section section = new Section();
                    section.setId(rs.getInt("id"));
                    section.setName(rs.getString("name"));

                    list.add(section);
                }
                return list;
            };
        });
        return sections;
    }

    @Override
    public List<Section> findSectionsByDepartmentId(int id) {
        List<Section> sections = jdbcTemplate.query(FIND_SECTIONS_BY_DEPARTMENT_ID_SQL, new Object[]{SectionConstants.SECTION_STATUS_ACTIVE, id}, new ResultSetExtractor<List<Section>>() {
            @Override
            public List<Section> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Section> list = new LinkedList<>();
                while (rs.next()) {
                    Section section = new Section();
                    section.setId(rs.getInt("id"));
                    section.setName(rs.getString("name"));

                    list.add(section);
                }
                return list;
            };
        });
        return sections;
    }

}
