package az.gov.adra.repository;

import az.gov.adra.constant.DocumentConstants;
import az.gov.adra.dataTransferObjects.DocumentDTO;
import az.gov.adra.repository.interfaces.DocumentRepository;
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
public class DocumentRepositoryImpl implements DocumentRepository {

    private static final String findAllDocumentsSql = "select id, title , file_url, date_of_reg from Document where status = ? order by date_of_reg OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findTopDocumentsByLastAddedTimeSql = "select top 3 id, title , file_url, date_of_reg from Document where status = ? order by date_of_reg";
    private static final String findCountOfAllDocumentsSql = "select count(*) as count from Document where status = ?";
    private static final String findCountOfAllDocumentsByKeywordSql = "select count(*) as count from Document where status = ? and title like ?";
    private static final String findDocumentsByKeywordSql = "select id, title , file_url, date_of_reg from Document where title like ? and status = ? order by date_of_reg OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<DocumentDTO> findAllDocuments(int offset) {
        List<DocumentDTO> documents = jdbcTemplate.query(findAllDocumentsSql, new Object[]{DocumentConstants.DOCUMENT_STATUS_ACTIVE, offset, DocumentConstants.DOCUMENT_FETCH_NEXT}, new ResultSetExtractor<List<DocumentDTO>>() {
            @Override
            public List<DocumentDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<DocumentDTO> list = new LinkedList<>();
                while (rs.next()) {
                    DocumentDTO document = new DocumentDTO();
                    document.setId(rs.getInt("id"));
                    document.setTitle(rs.getString("title"));
                    document.setFileUrl(rs.getString("file_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    document.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    list.add(document);
                }

                return list;
            }
        });
        return documents;
    }

    @Override
    public List<DocumentDTO> findTopDocumentsByLastAddedTime() {
        List<DocumentDTO> documents = jdbcTemplate.query(findTopDocumentsByLastAddedTimeSql, new Object[]{DocumentConstants.DOCUMENT_STATUS_ACTIVE}, new ResultSetExtractor<List<DocumentDTO>>() {
            @Override
            public List<DocumentDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<DocumentDTO> list = new LinkedList<>();
                while (rs.next()) {
                    DocumentDTO document = new DocumentDTO();
                    document.setId(rs.getInt("id"));
                    document.setTitle(rs.getString("title"));
                    document.setFileUrl(rs.getString("file_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    document.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    list.add(document);
                }

                return list;
            }
        });
        return documents;
    }

    @Override
    public int findCountOfAllDocuments() {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllDocumentsSql, new Object[] {DocumentConstants.DOCUMENT_STATUS_ACTIVE}, Integer.class);
        return totalCount;
    }

    @Override
    public int findCountOfAllDocumentsByKeyword(String keyword) {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllDocumentsByKeywordSql, new Object[] {DocumentConstants.DOCUMENT_STATUS_ACTIVE, keyword}, Integer.class);
        return totalCount;
    }

    @Override
    public List<DocumentDTO> findDocumentsByKeyword(String keyword, int offset) {
        List<DocumentDTO> documents = jdbcTemplate.query(findDocumentsByKeywordSql, new Object[]{"%" + keyword + "%", DocumentConstants.DOCUMENT_STATUS_ACTIVE, offset, DocumentConstants.DOCUMENT_FETCH_NEXT}, new ResultSetExtractor<List<DocumentDTO>>() {
            @Override
            public List<DocumentDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<DocumentDTO> list = new LinkedList<>();
                while (rs.next()) {
                    DocumentDTO document = new DocumentDTO();
                    document.setId(rs.getInt("id"));
                    document.setTitle(rs.getString("title"));
                    document.setFileUrl(rs.getString("file_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    document.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    list.add(document);
                }

                return list;
            }
        });
        return documents;
    }

}
