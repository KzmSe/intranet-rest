package az.gov.adra.repository.interfaces;

import az.gov.adra.dataTransferObjects.ActivityDTO;
import az.gov.adra.entity.Activity;
import az.gov.adra.entity.ActivityRespond;
import az.gov.adra.entity.ActivityReview;
import az.gov.adra.exception.ActivityCredentialsException;

import java.util.List;
import java.util.Map;

public interface ActivityRepository {

    List<ActivityDTO> findTopActivitiesByLastAddedTime();

    List<Activity> findAllActivities(int offset);

    ActivityDTO findActivityByActivityId(int id);

    List<ActivityReview> findReviewsByActivityId(int id);

    void addActivityReview(ActivityReview activityReview) throws ActivityCredentialsException;

    void addActivity(Activity activity) throws ActivityCredentialsException;

    void incrementViewCountOfActivityById(int id) throws ActivityCredentialsException;

    List<ActivityRespond> findActivityRespondsByRespond(int id, int respond);

    void updateActivityRespond(ActivityRespond activityRespond) throws ActivityCredentialsException;

    Map<Integer, Integer> findTopThreeActivitiesByLastAddedTime(String username);

    Map<Integer, Integer> findRespondOfActivity(String username, int activityId);

    List<ActivityDTO> findActivitiesByUsername(String username, int fetchNext);

    void updateActivity(Activity activity) throws ActivityCredentialsException;

    List<Activity> findActivitiesRandomly();

    int findCountOfAllActivities();

    void deleteActivity(Activity activity) throws ActivityCredentialsException;

    List<Activity> findActivitiesByKeyword(String keyword);

    void isActivityExistWithGivenId(int id) throws ActivityCredentialsException;

}
