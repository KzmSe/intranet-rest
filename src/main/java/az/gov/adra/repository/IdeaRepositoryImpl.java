package az.gov.adra.repository;

import az.gov.adra.constant.IdeaConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.entity.Idea;
import az.gov.adra.exception.IdeaCredentialsException;
import az.gov.adra.repository.interfaces.IdeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IdeaRepositoryImpl implements IdeaRepository {

    private static final String addIdeaSql = "insert into Idea(username, choice, title, description, img_url, date_of_reg, status) values(?, ?, ?, ?, ?, ?, ?)";
    private static final String findCountOfAllIdeasSql = "select count(*) as count from Idea where status = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addIdea(Idea idea) throws IdeaCredentialsException {
        int affectedRows = jdbcTemplate.update(addIdeaSql, new Object[]{idea.getUser().getUsername(), idea.getChoice(), idea.getTitle(), idea.getDescription(), idea.getImgUrl(), idea.getDateOfReg(), idea.getStatus()});

        if (affectedRows == 0) {
            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public int findCountOfAllIdeas() {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllIdeasSql, new Object[] {IdeaConstants.IDEA_STATUS_ACTIVE}, Integer.class);
        return totalCount;
    }

}
