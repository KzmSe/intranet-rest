package az.gov.adra.repository.interfaces;

import az.gov.adra.dataTransferObjects.ActivityDTO;
import az.gov.adra.dataTransferObjects.RespondDTO;
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

    List<ActivityReview> findReviewsByActivityId(int id, int fetchNext);

    void addActivityReview(ActivityReview activityReview) throws ActivityCredentialsException;

    void deleteActivityReview(ActivityReview review) throws ActivityCredentialsException;

    void addActivity(Activity activity) throws ActivityCredentialsException;

    void incrementViewCountOfActivityById(int id) throws ActivityCredentialsException;

    List<ActivityRespond> findActivityRespondsByRespond(int id, int respond, int fetchNext);

    void updateActivityRespond(ActivityRespond activityRespond) throws ActivityCredentialsException;

    Map<Integer, Integer> findTopThreeActivitiesByLastAddedTime(String username);

    RespondDTO findRespondOfActivity(String username, int activityId);

    List<ActivityDTO> findActivitiesByUsername(String username, int offset);

    List<ActivityDTO> findTopThreeActivitiesByUsername(String username);

    void updateActivity(Activity activity) throws ActivityCredentialsException;

    List<Activity> findActivitiesRandomly();

    int findCountOfAllActivities();

    int findCountOfAllActivitiesByKeyword(String keyword);

    int findCountOfAllActivitiesByUsername(String username);

    void deleteActivity(Activity activity) throws ActivityCredentialsException;

    List<Activity> findActivitiesByKeyword(String keyword, int offset);

    void isActivityExistWithGivenId(int id) throws ActivityCredentialsException;

    void isActivityReviewExistWithGivenId(int id) throws ActivityCredentialsException;

}
