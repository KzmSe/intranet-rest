package az.gov.adra.service;

import az.gov.adra.dataTransferObjects.ActivityDTO;
import az.gov.adra.entity.Activity;
import az.gov.adra.entity.ActivityRespond;
import az.gov.adra.entity.ActivityReview;
import az.gov.adra.exception.ActivityCredentialsException;
import az.gov.adra.repository.interfaces.ActivityRepository;
import az.gov.adra.service.interfaces.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private ActivityRepository activityRepository;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

//    @Override
//    public List<ActivityDTO> findActiviesByLastAddedTime() {
//        return activityRepository.findActiviesByLastAddedTime();
//    }

    @Override
    public List<Activity> findAllActivities(int offset) {
        return activityRepository.findAllActivities(offset);
    }

    @Override
    public ActivityDTO findActivityByActivityId(int id) {
        return activityRepository.findActivityByActivityId(id);
    }

    @Override
    public List<ActivityReview> findReviewsByActivityId(int id) {
        return activityRepository.findReviewsByActivityId(id);
    }

    @Override
    public void addActivityReview(ActivityReview activityReview) throws ActivityCredentialsException {
        activityRepository.addActivityReview(activityReview);
    }

    @Override
    public void addActivity(Activity activity) throws ActivityCredentialsException {
        activityRepository.addActivity(activity);
    }

//    @Override
//    public void incrementViewCountByActivityId(int id) throws ActivityCredentialsException {
//        activityRepository.incrementViewCountByActivityId(id);
//    }
//
//    @Override
//    public void incrementPositiveCountByActivityId(int id) throws ActivityCredentialsException {
//        activityRepository.incrementPositiveCountByActivityId(id);
//    }
//
//    @Override
//    public void incrementNegativeCountByActivityId(int id) throws ActivityCredentialsException {
//        activityRepository.incrementNegativeCountByActivityId(id);
//    }

    @Override
    public List<ActivityRespond> findActivityRespondsByRespond(int id, int respond) {
        return activityRepository.findActivityRespondsByRespond(id, respond);
    }

    @Override
    public void updateActivityRespond(ActivityRespond activityRespond) throws ActivityCredentialsException {
        activityRepository.updateActivityRespond(activityRespond);
    }

//    @Override
//    public Map<Integer, Integer> findRespondedActivitiesByLastAddedTime(int id) {
//        return activityRepository.findRespondedActivitiesByLastAddedTime(id);
//    }
//
//    @Override
//    public Map<Integer, Integer> findRespondedActivity(int employeeId, int activityId) {
//        return activityRepository.findRespondedActivity(employeeId, activityId);
//    }

    @Override
    public List<ActivityDTO> findActivitiesByEmployeeId(int id, int fetchNext) {
        return activityRepository.findActivitiesByEmployeeId(id, fetchNext);
    }

    @Override
    public void isActivityExistWithGivenId(int id) throws ActivityCredentialsException {
        activityRepository.isActivityExistWithGivenId(id);
    }

    @Override
    public void updateActivityById(Activity activity) throws ActivityCredentialsException {
        activityRepository.updateActivityById(activity);
    }

    @Override
    public List<Activity> findActivitiesRandomly() {
        return activityRepository.findActivitiesRandomly();
    }

//    @Override
//    public int findCountOfAllActivities() {
//        return activityRepository.findCountOfAllActivities();
//    }

    @Override
    public void deleteActivityByActivityIdAndEmployeeId(Activity activity) throws ActivityCredentialsException {
        activityRepository.deleteActivityByActivityIdAndEmployeeId(activity);
    }

    @Override
    public List<Activity> findActivitiesByKeyword(String keyword) {
        return activityRepository.findActivitiesByKeyword(keyword);
    }

}
