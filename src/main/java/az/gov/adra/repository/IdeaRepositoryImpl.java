package az.gov.adra.repository;

import az.gov.adra.constant.MessageConstants;
import az.gov.adra.entity.Idea;
import az.gov.adra.exception.IdeaCredentialsException;
import az.gov.adra.repository.interfaces.IdeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IdeaRepositoryImpl implements IdeaRepository {

//    private static final String findAllIdeasByChoiceSql = "select i.id as idea_id, i.title, i.description, i.img_url, i.date_of_reg, p.id as person_id, p.name, p.surname from Idea i inner join Employee e on i.employee_id = e.id inner join Person p on e.person_id = p.id where i.choice = ? and i.status = ? order by i.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
//    private static final String findIdeaByIdeaIdSql = "select i.id as idea_id, i.title, i.description, i.img_url, i.date_of_reg, p.id as person_id, p.name, p.surname from Idea i inner join Employee e on i.employee_id = e.id inner join Person p on e.person_id = p.id where i.id = ? and i.status = ?";
//    private static final String findReviewsByIdeaIdSql = "select i.id as idea_review_id, i.description, i.date_of_reg, p.id as person_id, p.name, p.surname from Idea_Review i inner join Employee e on i.employee_id = e.id inner join Person p on e.person_id = p.id where i.idea_id = ? and i.status = ? order by i.date_of_reg desc";
//    private static final String addIdeaReviewSql = "insert into idea_review (idea_id, employee_id, description, date_of_reg, status) value (?, ?, ?, ?, ?)";
    private static final String addIdeaSql = "insert into Idea(employee_id, choice, title, description, img_url, date_of_reg, status) values(?, ?, ?, ?, ?, ?, ?)";
//    private static final String findCountOfIdeaByIdeaIdSql = "select count(*) as Count from Idea  where id = ? and status = ?;";
//    private static final String findIdeasByEmployeeIdAndChoiceSql = "select i.id as idea_id, i.title, i.img_url, i.date_of_reg, p.name, p.surname, e.id as employee_id from Idea i inner join Employee e on i.employee_id = e.id inner join Person p on e.person_id = p.id where e.id = ? and i.choice = ? and i.status = ? order by i.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
//    private static final String updateIdeaByIdeaIdSql = "update Idea set title = ?, description = ?";
//    private static final String deleteIdeaByIdeaIdSql = "update Idea set status = ? where id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Override
//    public List<Idea> findAllIdeasByChoice(String choice, int fetchNext) {
//        List<Idea> ideas = jdbcTemplate.query(findAllIdeasByChoiceSql, new Object[]{choice, IdeaConstants.IDEA_STATUS_ACTIVE, IdeaConstants.IDEA_OFFSET_NUMBER, fetchNext}, new ResultSetExtractor<List<Idea>>() {
//            @Override
//            public List<Idea> extractData(ResultSet rs) throws SQLException, DataAccessException {
//                List<Idea> list = new LinkedList<>();
//
//                while (rs.next()) {
//                    Idea idea = new Idea();
//                    idea.setId(rs.getInt("idea_id"));
//                    idea.setTitle(rs.getString("title"));
//                    idea.setDescription(rs.getString("description"));
//                    idea.setImgUrl(rs.getString("img_url") != null ? rs.getString("img_url") : null);
//
//                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
//                    idea.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));
//
//                    Person person = new Person();
//                    person.setId(rs.getInt("person_id"));
//                    person.setName(rs.getString("name"));
//                    person.setSurname(rs.getString("surname"));
//
//                    Employee employee = new Employee();
//                    employee.setPerson(person);
//
//                    idea.setEmployee(employee);
//                    list.add(idea);
//                }
//                return list;
//            }
//        });
//        return ideas;
//    }
//
//    @Override
//    public Idea findIdeaByIdeaId(int id) throws IdeaCredentialsException {
//        if(!isIdeaExistWithGivenId(id)){
//            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_IDEA_NOT_FOUND);
//        }
//
//        Idea idea = jdbcTemplate.queryForObject(findIdeaByIdeaIdSql, new Object[]{id, IdeaConstants.IDEA_STATUS_ACTIVE}, new RowMapper<Idea>() {
//            @Override
//            public Idea mapRow(ResultSet rs, int i) throws SQLException {
//                Idea idea1 = new Idea();
//                idea1.setId(rs.getInt("idea_id"));
//                idea1.setTitle(rs.getString("title"));
//                idea1.setDescription(rs.getString("description"));
//                idea1.setImgUrl(rs.getString("img_url") != null ? rs.getString("img_url") : null);
//
//                LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
//                idea1.setDateOfReg(dateOfReg.format(TimeParserUtil.DATETIME_FORMATTER));
//
//                Person person = new Person();
//                person.setId(rs.getInt("person_id"));
//                person.setName(rs.getString("name"));
//                person.setSurname(rs.getString("surname"));
//
//                Employee employee = new Employee();
//                employee.setPerson(person);
//
//                idea1.setEmployee(employee);
//                return idea1;
//            }
//        });
//        return idea;
//    }
//
//    @Override
//    public List<IdeaReview> findReviewsByIdeaId(int id) throws IdeaCredentialsException {
//        if (!isIdeaExistWithGivenId(id)) {
//            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_IDEA_NOT_FOUND);
//        }
//
//        List<IdeaReview> ideaReviews = jdbcTemplate.query(findReviewsByIdeaIdSql, new Object[]{id, IdeaConstants.IDEA_REVIEW_STATUS_ACTIVE}, new ResultSetExtractor<List<IdeaReview>>() {
//            @Override
//            public List<IdeaReview> extractData(ResultSet rs) throws SQLException, DataAccessException {
//                List<IdeaReview> list = new LinkedList<>();
//
//                while (rs.next()) {
//                    IdeaReview ideaReview = new IdeaReview();
//                    ideaReview.setId(rs.getInt("idea_review_id"));
//                    ideaReview.setDescription(rs.getString("description"));
//
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
//                    ideaReview.setDateOfReg(rs.getTimestamp("date_of_reg").toLocalDateTime().format(formatter));
//
//                    Person person = new Person();
//                    person.setId(rs.getInt("person_id"));
//                    person.setName(rs.getString("name"));
//                    person.setSurname(rs.getString("surname"));
//
//                    Employee employee = new Employee();
//                    employee.setPerson(person);
//
//                    ideaReview.setEmployee(employee);
//                    list.add(ideaReview);
//                }
//                return list;
//            }
//        });
//        return ideaReviews;
//    }
//
//    @Override
//    public void addIdeaReview(IdeaReview ideaReview) throws IdeaCredentialsException {
//        int affectedRows = jdbcTemplate.update(addIdeaReviewSql, new Object[] {ideaReview.getIdea().getId(), ideaReview.getEmployee().getId(), ideaReview.getDescription(),ideaReview.getDateOfReg(), IdeaConstants.IDEA_REVIEW_STATUS_ACTIVE});
//        if (affectedRows == 0) {
//            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
//        }
//
//    }

    @Override
    public void addIdea(Idea idea) throws IdeaCredentialsException {
        int affectedRows = jdbcTemplate.update(addIdeaSql, new Object[]{idea.getEmployee().getId(), idea.getChoice(), idea.getTitle(), idea.getDescription(), idea.getImgUrl(), idea.getDateOfReg(), idea.getStatus()});

        if (affectedRows == 0) {
            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

//    @Override
//    public List<Idea> findIdeasByEmployeeIdAndChoice(int id, String choice, int fetchNext) {
//        List<Idea> ideas = jdbcTemplate.query(findIdeasByEmployeeIdAndChoiceSql, new Object[]{id, choice, IdeaConstants.IDEA_STATUS_ACTIVE, IdeaConstants.IDEA_OFFSET_NUMBER, fetchNext}, new ResultSetExtractor<List<Idea>>() {
//            @Override
//            public List<Idea> extractData(ResultSet rs) throws SQLException, DataAccessException {
//                List<Idea> list = new LinkedList<>();
//                while (rs.next()) {
//                    Idea idea = new Idea();
//                    idea.setId(rs.getInt("idea_id"));
//                    idea.setTitle(rs.getString("title"));
//                    idea.setImgUrl(rs.getString("img_url") != null ? rs.getString("img_url") : null);
//
//                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
//                    idea.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));
//
//                    Person person = new Person();
//                    person.setName(rs.getString("name"));
//                    person.setSurname(rs.getString("surname"));
//
//                    Employee employee = new Employee();
//                    employee.setPerson(person);
//                    employee.setId(rs.getInt("employee_id"));
//
//                    idea.setEmployee(employee);
//
//                    list.add(idea);
//                }
//
//                return list;
//            }
//        });
//        return ideas;
//    }
//
//    @Override
//    public void updateIdeaByIdeaId(Idea idea) throws IdeaCredentialsException {
//        StringBuilder builder = new StringBuilder(updateIdeaByIdeaIdSql);
//        Object[] parameters = null;
//
//        if (idea.getImgUrl() == null) {
//            builder.append(" where id = ?");
//            parameters = new Object[] {idea.getTitle(), idea.getDescription(), idea.getId()};
//
//        } else {
//            builder.append(", img_url = ? where id = ?");
//            parameters = new Object[] {idea.getTitle(), idea.getDescription(), idea.getImgUrl(), idea.getId()};
//        }
//
//        int affectedRows = jdbcTemplate.update(builder.toString(), parameters);
//
//        if (affectedRows == 0) {
//            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
//        }
//    }
//
//    @Override
//    public void deleteIdeaByIdeaId(int ideaId) throws IdeaCredentialsException {
//        int affectedRows = jdbcTemplate.update(deleteIdeaByIdeaIdSql, IdeaConstants.IDEA_REVIEW_STATUS_INACTIVE, ideaId);
//        if (affectedRows == 0) {
//            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
//        }
//    }

//    //private methods
//    private boolean isIdeaExistWithGivenId(int id){
//        boolean result = false;
//        int count = jdbcTemplate.queryForObject(findCountOfIdeaByIdeaIdSql, new Object[]{id, IdeaConstants.IDEA_STATUS_ACTIVE}, Integer.class);
//
//        if(count>0) {
//            result = true;
//        }
//        return result;
//    }

}
