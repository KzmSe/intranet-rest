package az.gov.adra.repository;

import az.gov.adra.constant.NewsConstants;
import az.gov.adra.entity.News;
import az.gov.adra.repository.interfaces.NewsRepository;
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
public class NewsRepositoryImpl implements NewsRepository {

    //fields
    private static final String findNewsByLastAddedTimeSql = "select top 3 id, news_link, title, img_url from News where status = ? order by date_of_reg desc";

    @Autowired
    JdbcTemplate jdbcTemplate;

    //methods

    @Override
    public List<News> findNewsByLastAddedTime() {
        List<News> newsList = jdbcTemplate.query(findNewsByLastAddedTimeSql, new Object[]{NewsConstants.NEWS_STATUS_ACTIVE}, new ResultSetExtractor<List<News>>() {
            @Override
            public List<News> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<News> list = new LinkedList<>();
                while (rs.next()) {
                    News news = new News();
                    news.setId(rs.getInt("id"));
                    news.setTitle(rs.getString("title"));
                    news.setNewsLink(rs.getString("news_link"));
                    news.setImgUrl(rs.getString("img_url"));

                    list.add(news);
                }
                return list;
            }
        });
        return newsList;
    }

}
