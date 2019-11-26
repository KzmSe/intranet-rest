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

    private static final String findTopPostsByLastAddedTimeSql = "select top 3 p.id as post_id, p.title, p.description, p.view_count, p.img_url, u.name, u.surname, u.username from Post p inner join users u on p.username = u.username where p.status = ? order by p.date_of_reg desc";
    private static final String findAllPostsSql = "select pos.id as post_id, pos.title, pos.description, pos.view_count, pos.img_url, pos.date_of_reg, u.name, u.surname, u.username from Post pos inner join users u on pos.username = u.username where pos.status = ? order by pos.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findPostByPostIdSql = "select p.post_id, p.title, p.description, p.view_count, p.img_url, p.date_of_reg, u.username, u.name, u.surname, l.likes as like_count, d.dislikes as dislike_count from post_view p inner join l_k l on l.id=p.post_id inner join dl_k d on d.id=p.post_id inner join users u on u.username=p.username where post_id = ? and p.status = ?";
    private static final String isPostExistWithGivenIdSql = "select count(*) as count from Post where id = ? and status = ?";
    private static final String findReviewsByPostIdSql = "select pr.id as post_review_id, pr.description, pr.date_of_reg, u.name, u.surname, u.username from Post_Review pr inner join users u on pr.username = u.username where pr.post_id = ? and pr.status = ? order by pr.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String addPostReviewSql = "insert into Post_Review(post_id, username, description, date_of_reg, status) values(?, ?, ?, ?, ?)";
    private static final String addPostSql = "insert into Post(username, title, description, view_count, img_url, date_of_reg, status) values(?, ?, ?, ?, ?, ?, ?)";
    private static final String incrementViewCountOfPostByIdSql = "update Post set view_count = view_count + 1 where id = ?";
    private static final String findRespondOfPostSql = "select p_ld.post_id, p_ld.like_dislike from Post_ld p_ld where p_ld.username = ? and p_ld.post_id = ?";
    private static final String findCountOfPostRespondByPostIdAndEmployeeIdSql = "select COUNT(*) as count from Post_ld where post_id = ? and username = ? and status = ?";
    private static final String updatePostRespondSql = "update Post_ld set like_dislike = ? where post_id = ? and username = ? and status = ?";
    private static final String addPostRespondSql = "insert into Post_ld(post_id, username, like_dislike, date_of_reg, status) values(?, ?, ?, ?, ?)";
    private static final String findPostsByUsernameSql = "select p.id as post_id, p.title, p.view_count, p.date_of_reg, p.img_url, u.name, u.surname, u.username from Post p inner join users u on p.username = u.username where u.username = ? and p.status = ? order by p.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String updatePostSql = "update Post set title = ?, description = ?";
    private static final String findPostsRandomlySql = "select top 4 p.id post_id, p.title, p.img_url, p.date_of_reg, u.name, u.surname, u.username from post p inner join users u on p.username=u.username where p.id in (SELECT TOP (select count(*) FROM Post where status = ?) id from Post where status = ? ORDER BY NEWID())";
    private static final String findCountOfAllPostsSql = "select count(*) as count from Post where status = ?";
    private static final String findCountOfAllPostsByKeywordSql = "select count(*) as count from Post where status = ? and title like ?";
    private static final String findCountOfAllPostsByUsernameSql = "select count(*) as count from Post where status = ? and username = ?";
    private static final String deletePostSql = "update Post set status = ? where id = ? and username = ?";
    private static final String findPostsByKeywordSql = "select pos.id as post_id, pos.title, pos.description, pos.view_count, pos.img_url, pos.date_of_reg, u.name, u.surname, u.username from Post pos inner join users u on pos.username = u.username where pos.title like ? and pos.status = ? order by pos.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

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

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    post.setUser(user);

                    list.add(post);
                }
                return list;
            }
        });
        return newsList;
    }

    @Override
    public List<Post> findAllPosts(int offset) {
        List<Post> postList = jdbcTemplate.query(findAllPostsSql, new Object[]{PostConstants.POST_STATUS_ACTIVE, offset, PostConstants.POST_FETCH_NEXT}, new ResultSetExtractor<List<Post>>() {
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

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    post.setUser(user);

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

                User user = new User();
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setUsername(rs.getString("username"));

                post1.setUser(user);

                return post1;
            }
        });
        return post;
    }

    @Override
    public List<PostReview> findReviewsByPostId(int id, int offset) {
        List<PostReview> reviews = jdbcTemplate.query(findReviewsByPostIdSql, new Object[]{id, PostConstants.POST_REVIEW_STATUS_ACTIVE, offset, PostConstants.POST_FETCH_NEXT}, new ResultSetExtractor<List<PostReview>>() {
            @Override
            public List<PostReview> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<PostReview> list = new LinkedList<>();
                while (rs.next()) {
                    PostReview postReview = new PostReview();
                    postReview.setId(rs.getInt("post_review_id"));
                    postReview.setDescription(rs.getString("description"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    postReview.setDateOfReg(dateOfReg.format(TimeParserUtil.DATETIME_FORMATTER));

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    postReview.setUser(user);

                    list.add(postReview);
                }

                return list;
            }
        });
        return reviews;
    }

    @Override
    public void addPostReview(PostReview postReview) throws PostCredentialsException {
        int affectedRows = jdbcTemplate.update(addPostReviewSql, postReview.getPost().getId(), postReview.getUser().getUsername(), postReview.getDescription(), postReview.getDateOfReg(), postReview.getStatus());
        if (affectedRows == 0) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public void addPost(Post post) throws PostCredentialsException {
        int affectedRows = jdbcTemplate.update(addPostSql, post.getUser().getUsername(), post.getTitle(), post.getDescription(), post.getViewCount(), post.getImgUrl(), post.getDateOfReg(), post.getStatus());
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
    public Map<Integer, Integer> findRespondOfPost(String username, int postId) {
        Map<Integer, Integer> respondedPost = jdbcTemplate.query(findRespondOfPostSql, new Object[]{username, postId}, new ResultSetExtractor<Map<Integer, Integer>>() {
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
        if (isPostRespondExistWithGivenPostIdAndEmployeeId(postLd.getPost().getId(), postLd.getUser().getUsername())) {
            int affectedRows = jdbcTemplate.update(updatePostRespondSql, postLd.getLikeDislike(), postLd.getPost().getId(), postLd.getUser().getUsername(), postLd.getStatus());
            if (affectedRows == 0) {
                throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
            }

        } else {
            int affectedRows = jdbcTemplate.update(addPostRespondSql, postLd.getPost().getId(), postLd.getUser().getUsername(), postLd.getLikeDislike(), postLd.getDateOfReg(), postLd.getStatus());
            if (affectedRows == 0) {
                throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
            }
        }
    }

    @Override
    public List<Post> findPostsByUsername(String username, int offset) {
        List<Post> posts = jdbcTemplate.query(findPostsByUsernameSql, new Object[]{username, PostConstants.POST_STATUS_ACTIVE, offset, PostConstants.POST_FETCH_NEXT_BY_USERNAME}, new ResultSetExtractor<List<Post>>() {
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

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    post.setUser(user);

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

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    post.setUser(user);

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

    @Override
    public int findCountOfAllPostsByKeyword(String keyword) {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllPostsByKeywordSql, new Object[] {PostConstants.POST_STATUS_ACTIVE, "%" + keyword + "%"}, Integer.class);
        return totalCount;
    }

    @Override
    public int findCountOfAllPostsByUsername(String username) {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllPostsByUsernameSql, new Object[] {PostConstants.POST_STATUS_ACTIVE, username}, Integer.class);
        return totalCount;
    }

    @Override
    public void deletePost(Post post) throws PostCredentialsException {
        int affectedRows = jdbcTemplate.update(deletePostSql, PostConstants.POST_REVIEW_STATUS_INACTIVE, post.getId(), post.getUser().getUsername());
        if (affectedRows == 0) {
            throw new PostCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public List<Post> findPostsByKeyword(String keyword, int offset) {
        List<Post> postList = jdbcTemplate.query(findPostsByKeywordSql, new Object[]{"%" + keyword + "%", PostConstants.POST_STATUS_ACTIVE, offset, PostConstants.POST_FETCH_NEXT}, new ResultSetExtractor<List<Post>>() {
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

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    post.setUser(user);

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

    private boolean isPostRespondExistWithGivenPostIdAndEmployeeId(int postId, String username) {
        boolean result = false;
        int count = jdbcTemplate.queryForObject(findCountOfPostRespondByPostIdAndEmployeeIdSql, new Object[] {postId, username, PostConstants.POST_STATUS_ACTIVE}, Integer.class);
        if (count > 0) {
            result = true;
        }
        return result;
    }

}
