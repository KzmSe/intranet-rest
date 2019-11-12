package az.gov.adra.controller;

import az.gov.adra.constant.ActivityConstants;
import az.gov.adra.dataTransferObjects.ActivityDTO;
import az.gov.adra.entity.Activity;
import az.gov.adra.entity.ActivityReview;
import az.gov.adra.entity.Employee;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.ActivityCredentialsException;
import az.gov.adra.service.interfaces.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @Value("${spring.app-name}")
    private String appName;

    @GetMapping("/activities")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivities() {
        List<Activity> activities = activityService.findAllActivities(0);
        //Generic response
        GenericResponse response = new GenericResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setDescription("list of activities");
        response.setData(activities);
        response.setTimestamp(LocalDateTime.now());
        response.setAppName(appName);
        return response;
    }

    @GetMapping("/activities/{activityId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivityById(@PathVariable(name = "activityId") Integer id) throws ActivityCredentialsException {
        ActivityDTO activityDTO = activityService.findActivityByActivityId(id);
        //Generic response
        GenericResponse response = new GenericResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setDescription("activity by id");
        response.setData(activityDTO);
        response.setTimestamp(LocalDateTime.now());
        response.setAppName(appName);
        return response;
    }

    @GetMapping("/activities/{activityId}/reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findReviewsByActivityId(@PathVariable(name = "activityId") Integer id) throws ActivityCredentialsException {
        List<ActivityReview> reviews = activityService.findReviewsByActivityId(id);
        //Generic response
        GenericResponse response = new GenericResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setDescription("reviews by activity id");
        response.setData(reviews);
        response.setTimestamp(LocalDateTime.now());
        response.setAppName(appName);
        return response;
    }

    @PostMapping("/activities/{activityId}/reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addActivityReview(final Principal principal,
                                                @PathVariable(value = "activityId") Integer id,
                                                @RequestParam(value = "description") String description) throws ActivityCredentialsException {
        //primcipal
        ActivityReview review = new ActivityReview();
        Activity activity = new Activity();
        activity.setId(id);
        review.setActivity(activity);
        review.setDescription(description);
        review.setDateOfReg(LocalDateTime.now().toString());
        review.setStatus(ActivityConstants.ACTIVITY_REVIEW_STATUS_ACTIVE);

        Employee employee = new Employee();
        employee.setId(488);
        review.setEmployee(employee);

        //  URI location = ServletUriComponentsBuilder
        //      .fromCurrentRequest()
        //      .path("/{activityId}")
        //      .buildAndExpand(savedReview.getId()).toUri();
        //
        //  return ResponseEntity.created(location).build();        return type --->ResponseEntity<Object>

        activityService.addActivityReview(review);
    }

}
