package az.gov.adra.repository;

import az.gov.adra.constant.GalleryConstants;
import az.gov.adra.entity.Gallery;
import az.gov.adra.repository.interfaces.GalleryRepository;
import az.gov.adra.util.TimeParserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Repository
public class GalleryRepositoryImpl implements GalleryRepository {

    private static final String findAllGalleriesSql = "select id, title, img_url, date_of_reg from Gallery where status = ? order by date_of_reg OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findCountOfAllGalleriesSql = "select count(*) as count from Gallery where status = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Gallery> findAllGalleries(int offset) {
        List<Gallery> galleries = jdbcTemplate.query(findAllGalleriesSql, new Object[]{GalleryConstants.GALLERY_STATUS_ACTIVE, offset, GalleryConstants.GALLERY_FETCH_NEXT}, new ResultSetExtractor<List<Gallery>>() {
            @Override
            public List<Gallery> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Gallery> list = new LinkedList<>();
                while (rs.next()) {
                    Gallery gallery = new Gallery();
                    gallery.setId(rs.getInt("id"));
                    gallery.setTitle(rs.getString("title"));
                    gallery.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    gallery.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    list.add(gallery);
                }

                return list;
            }
        });
        return galleries;
    }

    @Override
    public int findCountOfAllGalleries() {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllGalleriesSql, new Object[] {GalleryConstants.GALLERY_STATUS_ACTIVE}, Integer.class);
        return totalCount;
    }

}
