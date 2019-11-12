package az.gov.adra.service.interfaces;

import az.gov.adra.dataTransferObjects.ActivityDTO;
import az.gov.adra.entity.Activity;
import az.gov.adra.entity.ActivityReview;
import az.gov.adra.exception.ActivityCredentialsException;

import java.util.List;

public interface ActivityService {

//    List<ActivityDTO> findActiviesByLastAddedTime();

    List<Activity> findAllActivities(int offset);

    ActivityDTO findActivityByActivityId(int id) throws ActivityCredentialsException;

    List<ActivityReview> findReviewsByActivityId(int id) throws ActivityCredentialsException;

    void addActivityReview(ActivityReview activityReview) throws ActivityCredentialsException;

//    void addActivity(Activity activity) throws ActivityCredentialsException;
//
//    void incrementViewCountByActivityId(int id) throws ActivityCredentialsException;
//
//    void incrementPositiveCountByActivityId(int id) throws ActivityCredentialsException;
//
//    void incrementNegativeCountByActivityId(int id) throws ActivityCredentialsException;
//
//    List<ActivityRespond> findActivityRespondsByRespond(int id, int respond) throws ActivityCredentialsException;
//
//    void updateActivityRespond(ActivityRespond activityRespond) throws ActivityCredentialsException;
//
//    Map<Integer, Integer> findRespondedActivitiesByLastAddedTime(int id);
//
//    Map<Integer, Integer> findRespondedActivity(int employeeId, int activityId);
//
//    List<ActivityDTO> findActivitiesByEmployeeId(int id, int fetchNext);
//
//    void updateActivityByActivityId(Activity activity) throws ActivityCredentialsException;
//
//    List<Activity> findRandomActivities();
//
//    int findCountOfAllActivities();
//
//    void deleteActivityByActivityId(int activityId) throws ActivityCredentialsException;
//
//    List<Activity> findActivitiesByKeyword(String keyword);

}
