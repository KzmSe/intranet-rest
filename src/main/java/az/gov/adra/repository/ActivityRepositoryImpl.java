package az.gov.adra.repository;

import az.gov.adra.constant.ActivityConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.constant.UserConstants;
import az.gov.adra.dataTransferObjects.ActivityDTO;
import az.gov.adra.dataTransferObjects.RespondDTO;
import az.gov.adra.entity.*;
import az.gov.adra.exception.ActivityCredentialsException;
import az.gov.adra.repository.interfaces.ActivityRepository;
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
public class ActivityRepositoryImpl implements ActivityRepository {

    private static final String findTopActivitiesByLastAddedTimeSql = "select top 3 a.id as activity_id, a.title, a.view_count, a.img_url,a.date_of_reg, u.username, u.name, u.surname, isnull(act1.respond,0) positive,isnull(act0.respond,0) negative from Activity a inner join users u on a.username = u.username  full outer join act_res_1_view act1 on a.id = act1.activity_id full outer join act_res_0_view act0 on a.id = act0.activity_id where a.status = ? order by a.date_of_reg desc";
    private static final String findAllActivitiesSql = "select a.id as activity_id, a.title, a.view_count, a.date_of_reg, a.img_url, u.username, u.name, u.surname from Activity a inner join users u on u.username = a.username where a.status = ? order by a.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findActivityByActivityIdSql = "select a.id as activity_id, a.title, a.description, a.view_count, a.img_url, a.date_of_reg, u.username , u.name, u.surname, act1.respond positive,act0.respond negative from Activity a inner join users u on u.username = a.username  full outer join act_res_1_view act1 on a.id = act1.activity_id full outer join act_res_0_view act0 on a.id = act0.activity_id where a.id = ? and a.status = ?";
    private static final String findReviewsByActivityIdSql = "select ar.id as activity_review_id, ar.description, ar.date_of_reg, u.name, u.surname, u.username from Activity_Review ar inner join users u on ar.username = u.username where ar.activity_id = ? and ar.status = ? order by ar.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String addActivityReviewSql = "insert into Activity_Review(activity_id, username, description, date_of_reg, status) values(?, ?, ?, ?, ?)";
    private static final String deleteActivityReviewSql = "update Activity_Review set status = ? where id = ? and username = ? and status = ?";
    private static final String addActivitySql = "insert into Activity(username, title, description, view_count, img_url, date_of_reg, status) values(?, ?, ?, ?, ?, ?, ?)";
    private static final String incrementViewCountOfActivityByIdSql = "update Activity set view_count = view_count + 1 where id = ?";
    private static final String findActivityRespondsByRespondSql = "select ar.id as acrivity_respond_id, u.name, u.surname, u.username from Activity_Respond ar inner join users u on ar.username = u.username where ar.activity_id = ? and ar.respond = ? and ar.status = ? order by ar.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String addActivityRespondSql = "insert into Activity_Respond(activity_id, username, respond, date_of_reg, status) values(?, ?, ?, ?, ?)";
    private static final String findTopThreeActivitiesByLastAddedTimeSql = "select a_r.id,a_r.activity_id, a_r.username, a_r.respond, a_r.status, t_3_a.date_of_reg from Activity_Respond a_r inner join top_3_activity t_3_a on a_r.activity_id=t_3_a.id where a_r.username = ? and a_r.status = ? order by t_3_a.date_of_reg desc";
    private static final String updateActivityRespondSql = "update Activity_Respond set respond = ? where activity_id = ? and username = ? and status = ?";
    private static final String findRespondOfActivitySql = "select ar.activity_id, ar.respond from Activity_Respond ar where ar.username = ? and ar.activity_id = ?";
    private static final String findActivitiesByUsernameSql = "select a.id as activity_id, a.title, a.description, a.view_count, a.date_of_reg, a.img_url, a.status, isnull(act1.respond,0) positive, isnull(act0.respond,0) negative, u.username, u.name, u.surname from Activity a inner join users u on a.username = u.username full outer join act_res_1_view act1 on a.id = act1.activity_id full outer join act_res_0_view act0 on a.id = act0.activity_id where u.username = ? and a.status = ? order by a.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findTopThreeActivitiesByUsernameSql = "select top 3 a.id as activity_id, a.title, a.description, a.view_count, a.date_of_reg, a.img_url, a.status, isnull(act1.respond,0) positive, isnull(act0.respond,0) negative, u.username, u.name, u.surname from Activity a inner join users u on a.username = u.username full outer join act_res_1_view act1 on a.id = act1.activity_id full outer join act_res_0_view act0 on a.id = act0.activity_id where u.username = ? and a.status = ? order by a.date_of_reg desc";
    private static final String updateActivitySql = "update Activity set title = ?, description = ?";
    private static final String findActivitiesRandomlySql = "select top 3 a.id activity_id,a.title,a.img_url,a.date_of_reg,a.username,u.name,u.surname from Activity a  inner join users u on u.username=a.username  where a.id in (SELECT TOP (select count(*) from Activity where status = ?) id FROM Activity where status = ? ORDER BY NEWID())";
    private static final String findCountOfAllActivitiesSql = "select count(*) as count from Activity where status = ?";
    private static final String findCountOfAllActivitiesByKeywordSql = "select count(*) as count from Activity where status = ? and title like ?";
    private static final String findCountOfAllActivitiesByUsernameSql = "select count(*) as count from Activity where status = ? and username = ?";
    private static final String deleteActivitySql = "update Activity set status = ?, date_of_del = ? where id = ? and username = ?";
    private static final String findActivitiesByKeywordSql = "select a.id as activity_id, a.title, a.view_count, a.date_of_reg, a.img_url,u.username, u.name,u.surname from Activity a inner join users u on u.username = a.username  where a.title like ? and a.status = ? order by a.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String isActivityExistWithGivenIdSql = "select count(*) as count from Activity where id = ? and status = ?";
    private static final String isActivityReviewExistWithGivenIdSql = "select count(*) as count from Activity_Review where id = ? and status = ?";
    private static final String findCountOfActivityRespondsByActivityIdAndEmployeeIdSql = "select COUNT(*) as count from Activity_Respond where activity_id = ? and username = ? and status = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ActivityDTO> findTopActivitiesByLastAddedTime() {
        List<ActivityDTO> activityList = jdbcTemplate.query(findTopActivitiesByLastAddedTimeSql, new Object[]{ActivityConstants.ACTIVITY_STATUS_CONFIRMED}, new ResultSetExtractor<List<ActivityDTO>>() {
            @Override
            public List<ActivityDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ActivityDTO> list = new LinkedList<>();
                while (rs.next()) {
                    ActivityDTO activity = new ActivityDTO();
                    activity.setId(rs.getInt("activity_id"));
                    activity.setTitle(rs.getString("title"));
                    activity.setViewCount(rs.getInt("view_count"));
                    activity.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    activity.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    activity.setPositiveCount(rs.getInt("positive"));
                    activity.setNegativeCount(rs.getInt("negative"));

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    activity.setUser(user);

                    list.add(activity);
                }
                return list;
            }
        });
        return activityList;
    }

    @Override
    public List<Activity> findAllActivities(int offset) {
        List<Activity> activityList = jdbcTemplate.query(findAllActivitiesSql, new Object[]{ActivityConstants.ACTIVITY_STATUS_CONFIRMED, offset, ActivityConstants.ACTIVITY_FETCH_NEXT}, new ResultSetExtractor<List<Activity>>() {
            @Override
            public List<Activity> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Activity> list = new LinkedList<>();
                while (rs.next()) {
                    Activity activity = new Activity();
                    activity.setId(rs.getInt("activity_id"));
                    activity.setTitle(rs.getString("title"));
                    activity.setViewCount(rs.getInt("view_count"));
                    activity.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    activity.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    activity.setUser(user);

                    list.add(activity);
                }
                return list;
            }
        });
        return activityList;
    }

    @Override
    public ActivityDTO findActivityByActivityId(int id) {
        ActivityDTO activity = jdbcTemplate.queryForObject(findActivityByActivityIdSql, new Object[]{id, ActivityConstants.ACTIVITY_STATUS_CONFIRMED}, new RowMapper<ActivityDTO>() {
            @Override
            public ActivityDTO mapRow(ResultSet rs, int i) throws SQLException {
                ActivityDTO activity1 = new ActivityDTO();
                activity1.setId(rs.getInt("activity_id"));
                activity1.setTitle(rs.getString("title"));
                activity1.setDescription(rs.getString("description"));
                activity1.setViewCount(rs.getInt("view_count"));
                activity1.setImgUrl(rs.getString("img_url"));

                LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                activity1.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                activity1.setPositiveCount(rs.getInt("positive"));
                activity1.setNegativeCount(rs.getInt("negative"));

                User user = new User();
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setUsername(rs.getString("username"));

                activity1.setUser(user);

                return activity1;
            }
        });

        return activity;
    }

    @Override
    public List<ActivityReview> findReviewsByActivityId(int id, int fetchNext) {
        List<ActivityReview> reviews = jdbcTemplate.query(findReviewsByActivityIdSql, new Object[]{id, ActivityConstants.ACTIVITY_REVIEW_STATUS_ACTIVE, ActivityConstants.ACTIVITY_REVIEW_OFFSET, fetchNext}, new ResultSetExtractor<List<ActivityReview>>() {
            @Override
            public List<ActivityReview> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ActivityReview> list = new LinkedList<>();
                while (rs.next()) {
                    ActivityReview activityReview = new ActivityReview();
                    activityReview.setId(rs.getInt("activity_review_id"));
                    activityReview.setDescription(rs.getString("description"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    activityReview.setDateOfReg(dateOfReg.format(TimeParserUtil.DATETIME_FORMATTER));

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    activityReview.setUser(user);

                    list.add(activityReview);
                }

                return list;
            }
        });

        return reviews;
    }

    @Override
    public void addActivityReview(ActivityReview activityReview) throws ActivityCredentialsException {
        int affectedRows = jdbcTemplate.update(addActivityReviewSql, activityReview.getActivity().getId(), activityReview.getUser().getUsername(), activityReview.getDescription(), activityReview.getDateOfReg(), activityReview.getStatus());
        if (affectedRows == 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public void deleteActivityReview(ActivityReview review) throws ActivityCredentialsException {
        int affectedRows = jdbcTemplate.update(deleteActivityReviewSql, ActivityConstants.ACTIVITY_REVIEW_STATUS_INACTIVE, review.getId(), review.getUser().getUsername(), ActivityConstants.ACTIVITY_REVIEW_STATUS_ACTIVE);
        if (affectedRows == 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public void addActivity(Activity activity) throws ActivityCredentialsException {
        int affectedRows = jdbcTemplate.update(addActivitySql, activity.getUser().getUsername(), activity.getTitle(), activity.getDescription(), activity.getViewCount(), activity.getImgUrl(), activity.getDateOfReg(), activity.getStatus());
        if (affectedRows == 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public void incrementViewCountOfActivityById(int id) throws ActivityCredentialsException {
        int affectedRows = jdbcTemplate.update(incrementViewCountOfActivityByIdSql, id);
        if (affectedRows == 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public List<ActivityRespond> findActivityRespondsByRespond(int id, int respond, int offset) {
        List<ActivityRespond> activityResponds = jdbcTemplate.query(findActivityRespondsByRespondSql, new Object[]{id, respond, ActivityConstants.ACTIVITY_RESPOND_STATUS_ACTIVE, offset, ActivityConstants.ACTIVITY_RESPOND_FETCH_NEXT}, new ResultSetExtractor<List<ActivityRespond>>() {
            @Override
            public List<ActivityRespond> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ActivityRespond> list = new LinkedList<>();

                while (rs.next()) {
                    ActivityRespond activityRespond = new ActivityRespond();

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    activityRespond.setUser(user);

                    list.add(activityRespond);
                }

                return list;
            }
        });

        return activityResponds;
    }

    @Override
    public void updateActivityRespond(ActivityRespond activityRespond) throws ActivityCredentialsException {
        if (isActivityRespondExistWithGivenActivityIdAndEmployeeId(activityRespond.getActivity().getId(), activityRespond.getUser().getUsername())) {
            int affectedRows = jdbcTemplate.update(updateActivityRespondSql, activityRespond.getRespond(), activityRespond.getActivity().getId(), activityRespond.getUser().getUsername(), activityRespond.getStatus());
            if (affectedRows == 0) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
            }

        } else {
            int affectedRows = jdbcTemplate.update(addActivityRespondSql, activityRespond.getActivity().getId(), activityRespond.getUser().getUsername(), activityRespond.getRespond(), activityRespond.getDateOfReg(), activityRespond.getStatus());
            if (affectedRows == 0) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
            }
        }
    }

    @Override
    public Map<Integer, Integer> findTopThreeActivitiesByLastAddedTime(String username) {
        Map<Integer, Integer> respondedActivities = jdbcTemplate.query(findTopThreeActivitiesByLastAddedTimeSql, new Object[]{username, ActivityConstants.ACTIVITY_RESPOND_STATUS_ACTIVE}, new ResultSetExtractor<Map<Integer, Integer>>() {
            @Override
            public Map<Integer, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<Integer, Integer> map = new HashMap<>();
                while (rs.next()) {
                    map.put(rs.getInt("activity_id"), rs.getInt("respond"));
                }

                return map;
            }
        });
        return respondedActivities;
    }

    @Override
    public RespondDTO findRespondOfActivity(String username, int activityId) {
        RespondDTO respondedActivity = jdbcTemplate.query(findRespondOfActivitySql, new Object[]{username, activityId}, new ResultSetExtractor<RespondDTO>() {
            @Override
            public RespondDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
                RespondDTO dto = new RespondDTO();
                while (rs.next()) {
                    dto.setValue(rs.getInt("respond"));
                }

                return dto;
            }
        });
        return respondedActivity;
    }

    @Override
    public List<ActivityDTO> findActivitiesByUsername(String username, int offset) {
        List<ActivityDTO> activities = jdbcTemplate.query(findActivitiesByUsernameSql, new Object[]{username, ActivityConstants.ACTIVITY_STATUS_CONFIRMED, offset, ActivityConstants.ACTIVITY_FETCH_NEXT_BY_USERNAME}, new ResultSetExtractor<List<ActivityDTO>>() {
            @Override
            public List<ActivityDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ActivityDTO> list = new LinkedList<>();
                while (rs.next()) {
                    ActivityDTO activity = new ActivityDTO();
                    activity.setId(rs.getInt("activity_id"));
                    activity.setTitle(rs.getString("title"));
                    activity.setDescription(rs.getString("description"));
                    activity.setViewCount(rs.getInt("view_count"));
                    activity.setStatus(rs.getInt("status"));
                    activity.setPositiveCount(rs.getInt("positive"));
                    activity.setNegativeCount(rs.getInt("negative"));
                    activity.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    activity.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    activity.setUser(user);

                    list.add(activity);
                }
                return list;
            }
        });
        return activities;
    }

    @Override
    public List<ActivityDTO> findTopThreeActivitiesByUsername(String username) {
        List<ActivityDTO> activities = jdbcTemplate.query(findTopThreeActivitiesByUsernameSql, new Object[]{username, ActivityConstants.ACTIVITY_STATUS_CONFIRMED}, new ResultSetExtractor<List<ActivityDTO>>() {
            @Override
            public List<ActivityDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ActivityDTO> list = new LinkedList<>();
                while (rs.next()) {
                    ActivityDTO activity = new ActivityDTO();
                    activity.setId(rs.getInt("activity_id"));
                    activity.setTitle(rs.getString("title"));
                    activity.setDescription(rs.getString("description"));
                    activity.setViewCount(rs.getInt("view_count"));
                    activity.setStatus(rs.getInt("status"));
                    activity.setPositiveCount(rs.getInt("positive"));
                    activity.setNegativeCount(rs.getInt("negative"));
                    activity.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    activity.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    activity.setUser(user);

                    list.add(activity);
                }
                return list;
            }
        });
        return activities;
    }

    @Override
    public void isActivityExistWithGivenId(int id) throws ActivityCredentialsException {
        int count = jdbcTemplate.queryForObject(isActivityExistWithGivenIdSql, new Object[] {id, ActivityConstants.ACTIVITY_STATUS_CONFIRMED}, Integer.class);
        if (count <= 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ACTIVITY_NOT_FOUND);
        }
    }

    @Override
    public void isActivityReviewExistWithGivenId(int id) throws ActivityCredentialsException {
        int count = jdbcTemplate.queryForObject(isActivityReviewExistWithGivenIdSql, new Object[] {id, ActivityConstants.ACTIVITY_REVIEW_STATUS_ACTIVE}, Integer.class);
        if (count <= 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ACTIVITY_REVIEW_NOT_FOUND);
        }
    }

    @Override
    public void updateActivity(Activity activity) throws ActivityCredentialsException {
        StringBuilder builder = new StringBuilder(updateActivitySql);
        Object[] parameters = null;

        if (activity.getImgUrl().equals("none")) {
            builder.append(" where id = ?");
            parameters = new Object[] {activity.getTitle(), activity.getDescription(), activity.getId()};

        } else {
            builder.append(", img_url = ? where id = ?");
            parameters = new Object[] {activity.getTitle(), activity.getDescription(), activity.getImgUrl(), activity.getId()};
        }

        int affectedRows = jdbcTemplate.update(builder.toString(), parameters);

        if (affectedRows == 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public List<Activity> findActivitiesRandomly() {
        List<Activity> activities = jdbcTemplate.query(findActivitiesRandomlySql, new Object[]{ActivityConstants.ACTIVITY_STATUS_CONFIRMED, ActivityConstants.ACTIVITY_STATUS_CONFIRMED}, new ResultSetExtractor<List<Activity>>() {
            @Override
            public List<Activity> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Activity> list = new LinkedList<>();
                while (rs.next()) {
                    Activity activity = new Activity();
                    activity.setId(rs.getInt("activity_id"));
                    activity.setTitle(rs.getString("title"));
                    activity.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    activity.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    activity.setUser(user);

                    list.add(activity);
                }

                return list;
            }
        });
        return activities;
    }

    @Override
    public int findCountOfAllActivities() {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllActivitiesSql, new Object[] {ActivityConstants.ACTIVITY_STATUS_CONFIRMED}, Integer.class);
        return totalCount;
    }

    @Override
    public int findCountOfAllActivitiesByKeyword(String keyword) {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllActivitiesByKeywordSql, new Object[] {ActivityConstants.ACTIVITY_STATUS_CONFIRMED, "%" + keyword + "%"}, Integer.class);
        return totalCount;
    }

    @Override
    public int findCountOfAllActivitiesByUsername(String username) {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllActivitiesByUsernameSql, new Object[] {ActivityConstants.ACTIVITY_STATUS_CONFIRMED, username}, Integer.class);
        return totalCount;
    }

    @Override
    public void deleteActivity(Activity activity) throws ActivityCredentialsException {
        int affectedRows = jdbcTemplate.update(deleteActivitySql, ActivityConstants.ACTIVITY_STATUS_DELETED, activity.getDateOfDel(), activity.getId(), activity.getUser().getUsername());
        if (affectedRows == 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public List<Activity> findActivitiesByKeyword(String keyword, int offset) {
        List<Activity> activityList = jdbcTemplate.query(findActivitiesByKeywordSql, new Object[]{"%" + keyword + "%" ,ActivityConstants.ACTIVITY_STATUS_CONFIRMED, offset, ActivityConstants.ACTIVITY_FETCH_NEXT}, new ResultSetExtractor<List<Activity>>() {
            @Override
            public List<Activity> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Activity> list = new LinkedList<>();
                while (rs.next()) {
                    Activity activity = new Activity();
                    activity.setId(rs.getInt("activity_id"));
                    activity.setTitle(rs.getString("title"));
                    activity.setViewCount(rs.getInt("view_count"));
                    activity.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    activity.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    User user = new User();
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));

                    activity.setUser(user);

                    list.add(activity);
                }
                return list;
            }
        });
        return activityList;
    }

    private boolean isActivityRespondExistWithGivenActivityIdAndEmployeeId(int activityId, String username) {
        boolean result = false;
        int count = jdbcTemplate.queryForObject(findCountOfActivityRespondsByActivityIdAndEmployeeIdSql, new Object[] {activityId, username, ActivityConstants.ACTIVITY_RESPOND_STATUS_ACTIVE}, Integer.class);
        if (count > 0) {
            result = true;
        }
        return result;
    }

}
