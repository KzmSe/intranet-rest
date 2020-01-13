package az.gov.adra.repository;

import az.gov.adra.constant.RegionConstants;
import az.gov.adra.entity.Region;
import az.gov.adra.repository.interfaces.RegionRepository;
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
public class RegionRepositoryImpl implements RegionRepository {

    private static final String findAllRegionsSql = "select * from Region where status = ? order by name";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Region> findAllRegions() {
        List<Region> regions = jdbcTemplate.query(findAllRegionsSql, new Object[]{RegionConstants.REGION_STATUS_ACTIVE}, new ResultSetExtractor<List<Region>>() {
            @Override
            public List<Region> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Region> list = new LinkedList<>();
                while (rs.next()) {
                    Region region = new Region();
                    region.setId(rs.getInt("id"));
                    region.setName(rs.getString("name"));
                    list.add(region);
                }
                return list;
            }
        });
        return regions;
    }

}
