package az.gov.adra.repository.interfaces;

import az.gov.adra.dataTransferObjects.ActivityDTO;
import az.gov.adra.entity.Activity;
import az.gov.adra.entity.ActivityRespond;
import az.gov.adra.entity.ActivityReview;
import az.gov.adra.exception.ActivityCredentialsException;

import java.util.List;

public interface ActivityRepository {

//    List<ActivityDTO> findActiviesByLastAddedTime();

    List<Activity> findAllActivities(int offset);

    ActivityDTO findActivityByActivityId(int id);

    List<ActivityReview> findReviewsByActivityId(int id);

    void addActivityReview(ActivityReview activityReview) throws ActivityCredentialsException;

    void addActivity(Activity activity) throws ActivityCredentialsException;

//    void incrementViewCountByActivityId(int id) throws ActivityCredentialsException;
//
//    void incrementPositiveCountByActivityId(int id) throws ActivityCredentialsException;
//
//    void incrementNegativeCountByActivityId(int id) throws ActivityCredentialsException;

    List<ActivityRespond> findActivityRespondsByRespond(int id, int respond);

    void updateActivityRespond(ActivityRespond activityRespond) throws ActivityCredentialsException;

//    Map<Integer, Integer> findRespondedActivitiesByLastAddedTime(int id);
//
//    Map<Integer, Integer> findRespondedActivity(int employeeId, int activityId);

    List<ActivityDTO> findActivitiesByEmployeeId(int id, int fetchNext);

    void updateActivityById(Activity activity) throws ActivityCredentialsException;

    List<Activity> findActivitiesRandomly();

//    int findCountOfAllActivities();

    void deleteActivityByActivityIdAndEmployeeId(Activity activity) throws ActivityCredentialsException;

    List<Activity> findActivitiesByKeyword(String keyword);

    void isActivityExistWithGivenId(int id) throws ActivityCredentialsException;

}
