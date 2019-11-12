package az.gov.adra.controller;

import az.gov.adra.entity.Activity;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.service.interfaces.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
