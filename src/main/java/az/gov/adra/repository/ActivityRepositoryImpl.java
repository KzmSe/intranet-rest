package az.gov.adra.repository;

import az.gov.adra.constant.ActivityConstants;
import az.gov.adra.constant.EmployeeConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.ActivityDTO;
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

    private static final String findTopActivitiesByLastAddedTimeSql = "select top 3 a.id as activity_id, a.title, a.view_count, a.img_url,a.date_of_reg, e.id as employee_id,p.id as person_id, p.name, p.surname, isnull(act1.respond,0) positive,isnull(act0.respond,0) negative from Activity a inner join Employee e on a.employee_id = e.id inner join Person p on e.person_id = p.id full outer join act_res_1_view act1 on a.id = act1.activity_id full outer join act_res_0_view act0 on a.id = act0.activity_id where a.status = ? order by a.date_of_reg desc";
    private static final String findAllActivitiesSql = "select a.id as activity_id, a.title, a.view_count, a.date_of_reg, a.img_url, e.id as employee_id, p.id as person_id, p.name, p.surname from Activity a inner join Employee e on a.employee_id = e.id inner join Person p on e.person_id = p.id where a.status = ? order by a.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String findActivityByActivityIdSql = "select a.id as activity_id, a.title, a.description, a.view_count, a.img_url, a.date_of_reg,e.id as employee_id, p.id as person_id, p.name, p.surname, act1.respond positive,act0.respond negative from Activity a inner join Employee e on a.employee_id = e.id inner join Person p on e.person_id = p.id full outer join act_res_1_view act1 on a.id = act1.activity_id full outer join act_res_0_view act0 on a.id = act0.activity_id where a.id = ? and a.status = ?";
    private static final String findReviewsByActivityIdSql = "select ar.id as activity_review_id, ar.description, ar.date_of_reg, pe.id as person_id, pe.name, pe.surname from Activity_Review ar inner join Employee em on ar.employee_id = em.id inner join Person pe on em.person_id = pe.id where ar.activity_id = ? and ar.status = ? order by ar.date_of_reg desc";
    private static final String addActivityReviewSql = "insert into Activity_Review(activity_id, employee_id, description, date_of_reg, status) values(?, ?, ?, ?, ?)";
    private static final String addActivitySql = "insert into Activity(employee_id, title, description, view_count, img_url, date_of_reg, status) values(?, ?, ?, ?, ?, ?, ?)";
    private static final String incrementViewCountOfActivityByIdSql = "update Activity set view_count = view_count + 1 where id = ?";
    private static final String findActivityRespondsByRespondSql = "select ar.id as acrivity_respond_id, per.id as person_id, per.name, per.surname from Activity_Respond ar inner join Employee emp on ar.employee_id = emp.id inner join Person per on emp.person_id = per.id where ar.activity_id = ? and respond = ? and ar.status = ?";
    private static final String addActivityRespondSql = "insert into Activity_Respond(activity_id, employee_id, respond, date_of_reg, status) values(?, ?, ?, ?, ?)";
    private static final String findTopThreeActivitiesByLastAddedTimeSql = "select a_r.id,a_r.activity_id,a_r.employee_id,a_r.respond,a_r.status,t_3_a.date_of_reg from Activity_Respond a_r inner join top_3_activity t_3_a on  a_r.activity_id=t_3_a.id where a_r.employee_id = ? and a_r.status = ? order by t_3_a.date_of_reg desc";
    private static final String updateActivityRespondSql = "update Activity_Respond set respond = ? where activity_id = ? and employee_id = ? and status = ?";
    private static final String findRespondOfActivitySql = "select a_r.activity_id,a_r.respond from Activity_Respond a_r where a_r.employee_id = ? and a_r.activity_id = ?";
    private static final String findActivitiesByEmployeeIdSql = "select a.id as activity_id, a.title, a.view_count, a.date_of_reg, a.img_url, isnull(act1.respond,0) positive,isnull(act0.respond,0) negative, pr.name, pr.surname, e.id as employee_id from Activity a inner join Employee e on a.employee_id = e.id inner join Person pr  on pr.id = a.employee_id full outer join act_res_1_view act1 on a.id = act1.activity_id full outer join act_res_0_view act0 on a.id = act0.activity_id where e.id = ? and a.status = ? order by a.date_of_reg desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    private static final String updateActivitySql = "update Activity set title = ?, description = ?";
    private static final String findActivitiesRandomlySql = "select top 4 a.id activity_id,a.title,a.img_url,a.date_of_reg,a.employee_id,per.name,per.surname from Activity a  inner join Employee emp on a.employee_id=emp.id  inner join Person per on emp.person_id=per.id  where a.id in (SELECT TOP (select count(*) from Activity where status = ?) id FROM Activity where status = ? ORDER BY NEWID())";
    private static final String findCountOfAllActivitiesSql = "select count(*) as count from Activity where status = ?";
    private static final String deleteActivitySql = "update Activity set status = ? where id = ? and employee_id = ?";
    private static final String findActivitiesByKeywordSql = "select a.id as activity_id, a.title, a.view_count, a.date_of_reg, a.img_url, e.id as employee_id, p.id as person_id, p.name, p.surname from Activity a inner join Employee e on a.employee_id = e.id inner join Person p on e.person_id = p.id where a.title like ? and a.status = ? order by a.date_of_reg desc";
    private static final String isActivityExistWithGivenIdSql = "select count(*) as count from Activity where id = ? and status = ?";
    private static final String findCountOfActivityRespondsByActivityIdAndEmployeeIdSql = "select COUNT(*) as count from Activity_Respond where activity_id = ? and employee_id = ? and status = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ActivityDTO> findTopActivitiesByLastAddedTime() {
        List<ActivityDTO> activityList = jdbcTemplate.query(findTopActivitiesByLastAddedTimeSql, new Object[]{ActivityConstants.ACTIVITY_STATUS_ACTIVE}, new ResultSetExtractor<List<ActivityDTO>>() {
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

                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);
                    activity.setEmployee(employee);

                    list.add(activity);
                }
                return list;
            }
        });
        return activityList;
    }

    @Override
    public List<Activity> findAllActivities(int offset) {
        List<Activity> activityList = jdbcTemplate.query(findAllActivitiesSql, new Object[]{ActivityConstants.ACTIVITY_STATUS_ACTIVE, offset, ActivityConstants.ACTIVITY_FETCH_NEXT_NUMBER}, new ResultSetExtractor<List<Activity>>() {
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

                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);
                    activity.setEmployee(employee);

                    list.add(activity);
                }
                return list;
            }
        });
        return activityList;
    }

    @Override
    public ActivityDTO findActivityByActivityId(int id) {
        ActivityDTO activity = jdbcTemplate.queryForObject(findActivityByActivityIdSql, new Object[]{id, ActivityConstants.ACTIVITY_STATUS_ACTIVE}, new RowMapper<ActivityDTO>() {
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

                Person person = new Person();
                person.setId(rs.getInt("person_id"));
                person.setName(rs.getString("name"));
                person.setSurname(rs.getString("surname"));

                Employee employee = new Employee();
                employee.setId(rs.getInt("employee_id"));
                employee.setPerson(person);
                activity1.setEmployee(employee);

                return activity1;
            }
        });

        return activity;
    }

    @Override
    public List<ActivityReview> findReviewsByActivityId(int id) {
        List<ActivityReview> reviews = jdbcTemplate.query(findReviewsByActivityIdSql, new Object[]{id, ActivityConstants.ACTIVITY_REVIEW_STATUS_ACTIVE}, new ResultSetExtractor<List<ActivityReview>>() {
            @Override
            public List<ActivityReview> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ActivityReview> list = new LinkedList<>();
                while (rs.next()) {
                    ActivityReview activityReview = new ActivityReview();
                    activityReview.setId(rs.getInt("activity_review_id"));
                    activityReview.setDescription(rs.getString("description"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    activityReview.setDateOfReg(dateOfReg.format(TimeParserUtil.DATETIME_FORMATTER));

                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setPerson(person);
                    activityReview.setEmployee(employee);

                    list.add(activityReview);
                }

                return list;
            }
        });

        return reviews;
    }

    @Override
    public void addActivityReview(ActivityReview activityReview) throws ActivityCredentialsException {
        int affectedRows = jdbcTemplate.update(addActivityReviewSql, activityReview.getActivity().getId(), activityReview.getEmployee().getId(), activityReview.getDescription(), activityReview.getDateOfReg(), activityReview.getStatus());
        if (affectedRows == 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public void addActivity(Activity activity) throws ActivityCredentialsException {
        int affectedRows = jdbcTemplate.update(addActivitySql, activity.getEmployee().getId(), activity.getTitle(), activity.getDescription(), activity.getViewCount(), activity.getImgUrl(), activity.getDateOfReg(), activity.getStatus());
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
    public List<ActivityRespond> findActivityRespondsByRespond(int id, int respond) {
        List<ActivityRespond> activityResponds = jdbcTemplate.query(findActivityRespondsByRespondSql, new Object[]{id, respond, ActivityConstants.ACTIVITY_RESPOND_STATUS_ACTIVE}, new ResultSetExtractor<List<ActivityRespond>>() {
            @Override
            public List<ActivityRespond> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ActivityRespond> list = new LinkedList<>();

                while (rs.next()) {
                    ActivityRespond activityRespond = new ActivityRespond();
                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setPerson(person);
                    activityRespond.setEmployee(employee);

                    list.add(activityRespond);
                }

                return list;
            }
        });

        return activityResponds;
    }

    @Override
    public void updateActivityRespond(ActivityRespond activityRespond) throws ActivityCredentialsException {
        if (isActivityRespondExistWithGivenActivityIdAndEmployeeId(activityRespond.getActivity().getId(), activityRespond.getEmployee().getId())) {
            int affectedRows = jdbcTemplate.update(updateActivityRespondSql, activityRespond.getRespond(), activityRespond.getActivity().getId(), activityRespond.getEmployee().getId(), activityRespond.getStatus());
            if (affectedRows == 0) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
            }

        } else {
            int affectedRows = jdbcTemplate.update(addActivityRespondSql, activityRespond.getActivity().getId(), activityRespond.getEmployee().getId(), activityRespond.getRespond(), activityRespond.getDateOfReg(), activityRespond.getStatus());
            if (affectedRows == 0) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
            }
        }
    }

    @Override
    public Map<Integer, Integer> findTopThreeActivitiesByLastAddedTime(int id) {
        Map<Integer, Integer> respondedActivities = jdbcTemplate.query(findTopThreeActivitiesByLastAddedTimeSql, new Object[]{id, EmployeeConstants.EMPLOYEE_STATUS_ACTIVE}, new ResultSetExtractor<Map<Integer, Integer>>() {
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
    public Map<Integer, Integer> findRespondOfActivity(int employeeId, int activityId) {
        Map<Integer, Integer> respondedActivity = jdbcTemplate.query(findRespondOfActivitySql, new Object[]{employeeId, activityId}, new ResultSetExtractor<Map<Integer, Integer>>() {
            @Override
            public Map<Integer, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<Integer, Integer> map = new HashMap<>();
                while (rs.next()) {
                    map.put(rs.getInt("activity_id"), rs.getInt("respond"));
                }

                return map;
            }
        });
        return respondedActivity;
    }

    @Override
    public List<ActivityDTO> findActivitiesByEmployeeId(int id, int fetchNext) {
        List<ActivityDTO> activities = jdbcTemplate.query(findActivitiesByEmployeeIdSql, new Object[]{id, ActivityConstants.ACTIVITY_STATUS_ACTIVE, ActivityConstants.ACTIVITY_OFFSET_NUMBER, fetchNext}, new ResultSetExtractor<List<ActivityDTO>>() {
            @Override
            public List<ActivityDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<ActivityDTO> list = new LinkedList<>();
                while (rs.next()) {
                    ActivityDTO activity = new ActivityDTO();
                    activity.setId(rs.getInt("activity_id"));
                    activity.setTitle(rs.getString("title"));
                    activity.setViewCount(rs.getInt("view_count"));
                    activity.setPositiveCount(rs.getInt("positive"));
                    activity.setNegativeCount(rs.getInt("negative"));
                    activity.setImgUrl(rs.getString("img_url"));

                    LocalDateTime dateOfReg = TimeParserUtil.parseStringToLocalDateTime(rs.getString("date_of_reg"));
                    activity.setDateOfReg(dateOfReg.format(TimeParserUtil.DATE_FORMATTER));

                    Person person = new Person();
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);

                    activity.setEmployee(employee);

                    list.add(activity);
                }
                return list;
            }
        });
        return activities;
    }

    @Override
    public void isActivityExistWithGivenId(int id) throws ActivityCredentialsException {
        int count = jdbcTemplate.queryForObject(isActivityExistWithGivenIdSql, new Object[] {id, ActivityConstants.ACTIVITY_STATUS_ACTIVE}, Integer.class);
        if (count <= 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ACTIVITY_NOT_FOUND);
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
        List<Activity> activities = jdbcTemplate.query(findActivitiesRandomlySql, new Object[]{ActivityConstants.ACTIVITY_STATUS_ACTIVE, ActivityConstants.ACTIVITY_STATUS_ACTIVE}, new ResultSetExtractor<List<Activity>>() {
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

                    Person person = new Person();
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);

                    activity.setEmployee(employee);

                    list.add(activity);
                }

                return list;
            }
        });
        return activities;
    }

    @Override
    public int findCountOfAllActivities() {
        int totalCount = jdbcTemplate.queryForObject(findCountOfAllActivitiesSql, new Object[] {ActivityConstants.ACTIVITY_STATUS_ACTIVE}, Integer.class);
        return totalCount;
    }

    @Override
    public void deleteActivity(Activity activity) throws ActivityCredentialsException {
        int affectedRows = jdbcTemplate.update(deleteActivitySql, ActivityConstants.ACTIVITY_STATUS_INACTIVE, activity.getId(), activity.getEmployee().getId());
        if (affectedRows == 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INTERNAL_ERROR);
        }
    }

    @Override
    public List<Activity> findActivitiesByKeyword(String keyword) {
        List<Activity> activityList = jdbcTemplate.query(findActivitiesByKeywordSql, new Object[]{"%" + keyword + "%" ,ActivityConstants.ACTIVITY_STATUS_ACTIVE}, new ResultSetExtractor<List<Activity>>() {
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

                    Person person = new Person();
                    person.setId(rs.getInt("person_id"));
                    person.setName(rs.getString("name"));
                    person.setSurname(rs.getString("surname"));

                    Employee employee = new Employee();
                    employee.setId(rs.getInt("employee_id"));
                    employee.setPerson(person);
                    activity.setEmployee(employee);

                    list.add(activity);
                }
                return list;
            }
        });
        return activityList;
    }


    private boolean isActivityRespondExistWithGivenActivityIdAndEmployeeId(int activityId, int employeeId) {
        boolean result = false;
        int count = jdbcTemplate.queryForObject(findCountOfActivityRespondsByActivityIdAndEmployeeIdSql, new Object[] {activityId, employeeId, ActivityConstants.ACTIVITY_STATUS_ACTIVE}, Integer.class);
        if (count > 0) {
            result = true;
        }
        return result;
    }
}
