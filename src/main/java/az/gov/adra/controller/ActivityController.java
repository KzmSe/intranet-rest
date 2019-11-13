package az.gov.adra.controller;

import az.gov.adra.constant.ActivityConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.ActivityDTO;
import az.gov.adra.entity.Activity;
import az.gov.adra.entity.ActivityRespond;
import az.gov.adra.entity.ActivityReview;
import az.gov.adra.entity.Employee;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.ActivityCredentialsException;
import az.gov.adra.service.interfaces.ActivityService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @Value("${spring.app-name}")
    private String appName;
    @Value("${file.upload.path.win}")
    private String imageUploadPath;
    private final int rightFileSize = 3145728;
    private final String defaultActivityHexCode = "616374697669746965735C64656661756C745F61637469766974792E6A7067";


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
    public GenericResponse findActivityById(@PathVariable(name = "activityId", required = false) Integer id) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

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
    public GenericResponse findReviewsByActivityId(@PathVariable(name = "activityId", required = false) Integer id) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

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
    @ResponseStatus(HttpStatus.CREATED)
    public void addActivityReview(final Principal principal,
                                                @PathVariable(value = "activityId") Integer id,
                                                @RequestParam(value = "description") String description) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id) || ValidationUtil.isNullOrEmpty(description)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

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

    @PostMapping("/activities")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addActivity(@RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "file", required = false) MultipartFile multipartFile) throws ActivityCredentialsException, IOException {

        if (!ValidationUtil.isNullOrEmpty(title, description)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (!multipartFile.isEmpty()) {
            if (!(multipartFile.getOriginalFilename().endsWith(".jpg")
                    || multipartFile.getOriginalFilename().endsWith(".jpeg")
                    || multipartFile.getOriginalFilename().endsWith(".png"))) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (multipartFile.getSize() >= rightFileSize) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }
        }

        //principal
        Employee employee = new Employee();
        employee.setId(488);

        Activity activity = new Activity();
        activity.setEmployee(employee);
        activity.setTitle(title);
        activity.setDescription(description);
        activity.setViewCount(0);
        activity.setDateOfReg(LocalDateTime.now().toString());
        activity.setStatus(ActivityConstants.ACTIVITY_STATUS_ACTIVE);

        if (!multipartFile.isEmpty()) {
            Path pathToSaveFile = Paths.get(imageUploadPath, "activities", employee.getHId());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "##" + multipartFile.getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(multipartFile.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("activities", employee.getHId(), fileName);

            activity.setImgUrl(DatatypeConverter.printHexBinary(pathToSaveDb.toString().getBytes()));

        } else {
            activity.setImgUrl(defaultActivityHexCode);
        }

        activityService.addActivity(activity);
    }

    @GetMapping("/activities/{activityId}/responds")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivityResponds(@RequestParam(value = "activityId", required = false) Integer activityId,
                                                @RequestParam(value = "respond", required = false) Integer respond) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(activityId) || ValidationUtil.isNull(respond)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        List<ActivityRespond> activityResponds = activityService.findActivityRespondsByRespond(activityId, respond);
        //Generic response
        GenericResponse response = new GenericResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setDescription("activity responds");
        response.setData(activityResponds);
        response.setTimestamp(LocalDateTime.now());
        response.setAppName(appName);
        return response;
    }

    @PutMapping("/activities/{activityId}/responds")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    public void updateActivityRespond(@RequestParam(value = "activityId", required = false) Integer activityId,
                                      @RequestParam(value = "respond", required = false) Integer respond) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(activityId) || ValidationUtil.isNull(respond)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        //principal
        Employee employee = new Employee();
        employee.setId(488);

        ActivityRespond activityRespond = new ActivityRespond();
        Activity activity = new Activity();
        activity.setId(activityId);
        activityRespond.setActivity(activity);
        activityRespond.setEmployee(employee);
        activityRespond.setRespond(respond);
        activityRespond.setDateOfReg(LocalDateTime.now().toString());
        activityRespond.setStatus(ActivityConstants.ACTIVITY_RESPOND_STATUS_ACTIVE);

        activityService.updateActivityRespond(activityRespond);
    }

    @GetMapping("/employees/{employeeId}/activities")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivitiesByEmployeeId(@RequestParam(value = "employeeId",required = false) Integer employeeId,
                                                      @RequestParam(name = "fetchNext", required = false) Integer fetchNext) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(employeeId)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (fetchNext == null) {
            fetchNext = 6;
        }

        List<ActivityDTO> activities = activityService.findActivitiesByEmployeeId(employeeId, fetchNext);
        //Generic response
        GenericResponse response = new GenericResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setDescription("activity responds");
        response.setData(activities);
        response.setTimestamp(LocalDateTime.now());
        response.setAppName(appName);

        return response;
    }





}
