package az.gov.adra.repository;

import az.gov.adra.constant.MessageConstants;
import az.gov.adra.constant.PostConstants;
import az.gov.adra.dataTransferObjects.PostDTO;
import az.gov.adra.entity.*;
import az.gov.adra.exception.PostCredentialsException;
import az.gov.adra.repository.interfaces.PostRepository;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private static final String findTopPostsByLastAddedTimeSql = "select top 6 p.id as post_id, p.title, p.description, p.view_count, p.img_url, e.id as employee_id, e.person_id, pr.name, pr.surname from Post p inner join Employee e on p.employee_id = e.id inner join Person pr  on e.person_id = pr.id where p.status = ? order by p.date_of_reg desc";
    private static final String findAllPostsSql = "select pos.id as post_id, pos.title, pos.description, pos.view_count, pos.img_url, pos.date_of_reg,emp.id as employee_id, per.id as person_id, per.name, per.surname from Post pos inner join Employee emp on pos.employee_id = emp.id inner join Person per on emp.person_id = per.id where pos.status = ? order by date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findPostByPostIdSql = "select p.post_id, p.title, p.description, p.view_count, p.img_url, p.date_of_reg,emp.id as employee_id, p.person_id, p.name, p.surname, l.likes as like_count, d.dislikes as dislike_count from post_view p inner join l_k l on l.id=p.post_id inner join dl_k d on d.id=p.post_id  inner join Employee emp on emp.person_id=p.person_id where post_id = ? and p.status = ?";
    private static final String isPostExistWithGivenIdSql = "select count(*) as count from Post where id = ? and status = ?";
    private static final String findReviewsByPostIdSql = "select pr.id as post_review_id, pr.description, pr.date_of_reg, pe.id as person_id, pe.name, pe.surname from Post_Review pr inner join Employee em on pr.employee_id = em.id inner join Person pe on em.person_id = pe.id where pr.post_id = ? and pr.status = ? order by pr.date_of_reg desc";
    private static final String addPostReviewSql = "insert into Post_Review(post_id, employee_id, description, date_of_reg, status) values(?, ?, ?, ?, ?)";
    private static final String addPostSql = "insert into Post(employee_id, title, description, view_count, img_url, date_of_reg, status) values(?, ?, ?, ?, ?, ?, ?)";
    private static final String incrementViewCountOfPostByIdSql = "update Post set view_count = view_count + 1 where id = ?";
    private static final String findRespondOfPostSql = "select p_ld.post_id,p_ld.like_dislike from Post_ld p_ld where p_ld.employee_id = ? and p_ld.post_id = ?";
    private static final String findCountOfPostRespondByPostIdAndEmployeeIdSql = "select COUNT(*) as count from Post_ld where post_id = ? and employee_id = ? and status = ?";
    private static final String updatePostRespondSql = "update Post_ld set like_dislike = ? where post_id = ? and employee_id = ? and status = ?";
    private static final String addPostRespondSql = "insert into Post_ld(post_id, employee_id, like_dislike, date_of_reg, status) values(?, ?, ?, ?, ?)";
    private static final String findPostsByEmployeeIdSql = "select p.id as post_id, p.title, p.view_count, p.date_of_reg, p.img_url, pr.name, pr.surname, e.id as employee_id from Post p inner join Employee e on p.employee_id = e.id inner join Person pr  on e.person_id = pr.id where e.id = ? and p.status = ? order by p.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String updatePostSql = "update Post set title = ?, description = ?";
    private static final String findPostsRandomlySql = "select top 4 p.id post_id,p.title,p.img_url,p.date_of_reg,p.employee_id,per.name,per.surname from post p inner join Employee emp on p.employee_id=emp.id inner join Person per on emp.person_id=per.id where p.id in (SELECT TOP (select count(*) FROM Post where status = ?) id from Post where status = ? ORDER BY NEWID())";
    private static final String findCountOfAllPostsSql = "select count(*) as count from Post where status = ?";
    private static final String deletePostSql = "update Post set status = ? where id = ? and employee_id = ?";
    private static final String findPostsByKeywordSql = "select pos.id as post_id, pos.title, pos.description, pos.view_count, pos.img_url, pos.date_of_reg,emp.id as employee_id, per.id as person_id, per.name, per.surname from Post pos inner join Employee emp on pos.employee_id = emp.id inner join Person per on emp.person_id = per.id where pos.title like ? and pos.status = ? order by date_of_reg desc";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Post> findTopPostsByLastAddedTime() {
        List<Post> newsList = jdbcTemplate.query(findTopPostsByLastAddedTimeSql, new Object[]{PostConstants.POST_STATUS_ACTIVE}, new ResultSetExtractor<List<Post>>() {
            @Override
            public List<Post> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Post> list = new LinkedList<>();
                while (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getInt("post_id"));
                    post.setTitle(rs.getString("title"));
                    post.setDescription(rs.getString("description"));
                    post.setImgUrl(rs.getString("img_url"));
                    post.setViewCount(rs.getInt("view_count"));

                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);
                    post.setEmployee(employee);

                    list.add(post);
                }
                return list;
            }
        });
        return newsList;
    }

    @Override
    public List<Post> findAllPosts(int offset) {
        List<Post> postList = jdbcTemplate.query(findAllPostsSql, new Object[]{PostConstants.POST_STATUS_ACTIVE, offset, PostConstants.POST_FETCH_NEXT_NUMBER}, new ResultSetExtractor<List<Post>>() {
            @Override
            public List<Post> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Post> list = new LinkedList<>();
                while (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getInt("post_id"));
                    post.setTitle(rs.getString("title"));
                    post.setViewCount(rs.getInt("view_count"));
                    post.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    post.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);
                    post.setEmployee(employee);

                    list.add(post);
                }
                return list;
            }
        });
        return postList;
    }

    @Override
    public PostDTO findPostByPostId(int id) {
        PostDTO post = jdbcTemplate.queryForObject(findPostByPostIdSql, new Object[]{id, PostConstants.POST_STATUS_ACTIVE}, new RowMapper<PostDTO>() {
            @Override
            public PostDTO mapRow(ResultSet rs, int i) throws SQLException {
                PostDTO post1 = new PostDTO();
                post1.setId(rs.getInt("post_id"));
                post1.setTitle(rs.getString("title"));
                post1.setDescription(rs.getString("description"));
                post1.setViewCount(rs.getInt("view_count"));
                post1.setImgUrl(rs.getString("img_url"));
                post1.setLikeCount(rs.getInt("like_count"));
                post1.setDislikeCount(rs.getInt("dislike_count"));

                LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                post1.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                Person person = new Person();
                person.setId(rs.getInt("person_id"));
                person.setName(rs.getString("name"));
                person.setSurname(rs.getString("surname"));

                Employee employee = new Employee();
                employee.setId(rs.getInt("employee_id"));
                employee.setPerson(person);
                post1.setEmployee(employee);

                return post1;
            }
        });
        return post;
    }

    @Override
    public List<PostReview> findReviewsByPostId(int id) {
        List<PostReview> reviews = jdbcTemplate.query(findReviewsByPostIdSql, new Object[]{id, PostConstants.POST_REVIEW_STATUS_ACTIVE}, new ResultSetExtractor<List<PostReview>>() {
            @Override
            public List<PostReview> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<PostReview> list = new LinkedList<>();
                while (rs.next()) {
                    PostReview postReview = new PostReview();
                    postReview.setId(rs.getInt("post_review_id"));
                    postReview.setDescription(rs.getString("description"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    postReview.setDateOfReg(dateOfReg.format(TimeParserUtil.DATETIME_FORMATTER));

                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setPerson(person);
                    postReview.setEmployee(employee);

                    list.add(postReview);
                }

                return list;
            }
        });
        return reviews;
    }

    @Override
    public void addPostReview(PostReview postReview) throws PostCredentialsException {
        int affectedRows = jdbcTemplate.update(addPostReviewSql, postReview.getPost().getId(), postReview.getEmployee().getId(), postReview.getDescription(), postReview.getDateOfReg(), postReview.getStatus());
        if (affectedRows == 0) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public void addPost(Post post) throws PostCredentialsException {
        int affectedRows = jdbcTemplate.update(addPostSql, post.getEmployee().getId(), post.getTitle(), post.getDescription(), post.getViewCount(), post.getImgUrl(), post.getDateOfReg(), post.getStatus());
        if (affectedRows == 0) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public void incrementViewCountOfPostById(int id) throws PostCredentialsException {
        int affectedRows = jdbcTemplate.update(incrementViewCountOfPostByIdSql, id);
        if (affectedRows == 0) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public Map<Integer, Integer> findRespondOfPost(int employeeId, int postId) {
        Map<Integer, Integer> respondedPost = jdbcTemplate.query(findRespondOfPostSql, new Object[]{employeeId, postId}, new ResultSetExtractor<Map<Integer, Integer>>() {
            @Override
            public Map<Integer, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<Integer, Integer> map = new HashMap<>();
                while (rs.next()) {
                    map.put(rs.getInt("post_id"), rs.getInt("like_dislike"));
                }

                return map;
            }
        });
        return respondedPost;
    }

    @Override
    public void updatePostRespond(PostLd postLd) throws PostCredentialsException {
        if (isPostRespondExistWithGivenPostIdAndEmployeeId(postLd.getPost().getId(), postLd.getEmployee().getId())) {
            int affectedRows = jdbcTemplate.update(updatePostRespondSql, postLd.getLikeDislike(), postLd.getPost().getId(), postLd.getEmployee().getId(), postLd.getStatus());
            if (affectedRows == 0) {
                throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
            }

        } else {
            int affectedRows = jdbcTemplate.update(addPostRespondSql, postLd.getPost().getId(), postLd.getEmployee().getId(), postLd.getLikeDislike(), postLd.getDateOfReg(), postLd.getStatus());
            if (affectedRows == 0) {
                throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
            }
        }
    }

    @Override
    public List<Post> findPostsByEmployeeId(int id, int fetchNext) {
        List<Post> posts = jdbcTemplate.query(findPostsByEmployeeIdSql, new Object[]{id, PostConstants.POST_STATUS_ACTIVE, PostConstants.POST_OFFSET_NUMBER, fetchNext}, new ResultSetExtractor<List<Post>>() {
            @Override
            public List<Post> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Post> list = new LinkedList<>();
                while (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getInt("post_id"));
                    post.setTitle(rs.getString("title"));
                    post.setViewCount(rs.getInt("view_count"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    post.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    post.setImgUrl(rs.getString("img_url"));

                    Person person = new Person();
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);

                    post.setEmployee(employee);

                    list.add(post);
                }
                return list;
            }
        });
        return posts;
    }

    @Override
    public void updatePost(Post post) throws PostCredentialsException {
        StringBuilder builder = new StringBuilder(updatePostSql);
        Object[] parameters = null;

        if (post.getImgUrl().equals("none")) {
            builder.append(" where id = ?");
            parameters = new Object[] {post.getTitle(), post.getDescription(), post.getId()};

        } else {
            builder.append(", img_url = ? where id = ?");
            parameters = new Object[] {post.getTitle(), post.getDescription(), post.getImgUrl(), post.getId()};
        }

        int affectedRows = jdbcTemplate.update(builder.toString(), parameters);

        if (affectedRows == 0) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public List<Post> findPostsRandomly() {
        List<Post> posts = jdbcTemplate.query(findPostsRandomlySql, new Object[]{PostConstants.POST_STATUS_ACTIVE, PostConstants.POST_STATUS_ACTIVE}, new ResultSetExtractor<List<Post>>() {
            @Override
            public List<Post> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Post> list = new LinkedList<>();
                while (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getInt("post_id"));
                    post.setTitle(rs.getString("title"));
                    post.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    post.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    Person person = new Person();
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);

                    post.setEmployee(employee);

                    list.add(post);
                }

                return list;
            }
        });
        return posts;
    }

    @Override
    public int findCountOfAllPosts() {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllPostsSql, new Object[] {PostConstants.POST_STATUS_ACTIVE}, Integer.class);
        return totalCount;
    }

    //sql
    @Override
    public void deletePost(Post post) throws PostCredentialsException {
        int affectedRows = jdbcTemplate.update(deletePostSql, PostConstants.POST_REVIEW_STATUS_INACTIVE, post.getId(), post.getEmployee().getId());
        if (affectedRows == 0) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public List<Post> findPostsByKeyword(String keyword) {
        List<Post> postList = jdbcTemplate.query(findPostsByKeywordSql, new Object[]{"%" + keyword + "%", PostConstants.POST_STATUS_ACTIVE}, new ResultSetExtractor<List<Post>>() {
            @Override
            public List<Post> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Post> list = new LinkedList<>();
                while (rs.next()) {
                    Post post = new Post();
                    post.setId(rs.getInt("post_id"));
                    post.setTitle(rs.getString("title"));
                    post.setViewCount(rs.getInt("view_count"));
                    post.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    post.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);
                    post.setEmployee(employee);

                    list.add(post);
                }
                return list;
            }
        });
        return postList;
    }

    @Override
    public void isPostExistWithGivenId(int id) throws PostCredentialsException {
        int count = jdbcTemplate.queryForObject(isPostExistWithGivenIdSql, new Object[] {id, PostConstants.POST_STATUS_ACTIVE}, Integer.class);

        if (count <= 0) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_POST_NOT_FOUND);
        }
    }

    private boolean isPostRespondExistWithGivenPostIdAndEmployeeId(int postId, int employeeId) {
        boolean result = false;
        int count = jdbcTemplate.queryForObject(findCountOfPostRespondByPostIdAndEmployeeIdSql, new Object[] {postId, employeeId, PostConstants.POST_STATUS_ACTIVE}, Integer.class);
        if (count > 0) {
            result = true;
        }
        return result;
    }

}