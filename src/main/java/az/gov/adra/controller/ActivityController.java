package az.gov.adra.controller;

import az.gov.adra.constant.ActivityConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.ActivityDTO;
import az.gov.adra.entity.*;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.ActivityCredentialsException;
import az.gov.adra.exception.UserCredentialsException;
import az.gov.adra.service.interfaces.ActivityService;
import az.gov.adra.service.interfaces.UserService;
import az.gov.adra.util.ResourceUtil;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserService userService;
    @Value("${file.upload.path.win}")
    private String imageUploadPath;
    private final int maxFileSize = 3145728;
    private final String defaultActivityHexCode = "616374697669746965735C64656661756C745F61637469766974792E6A7067";


    @GetMapping("/activities")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivities(@RequestParam(name = "page", required = false) Integer page,
                                          HttpServletResponse response) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(page)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = activityService.findCountOfAllActivities();
        int totalPages = 0;
        int offset = 0;

        if (total != 0) {
            totalPages = (int) Math.ceil((double) total / 10);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 10;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 10;
            };
        }

        List<Activity> activities = activityService.findAllActivities(offset);
        for (Activity activity : activities) {
            if (activity.getImgUrl() == null) {
                continue;
            }
            activity.setImgUrl(ResourceUtil.convertToString(activity.getImgUrl()));
        }

        response.setIntHeader("Total-Pages", totalPages);
        return GenericResponse.withSuccess(HttpStatus.OK, "list of activities", activities);
    }

    @GetMapping("/activities/{activityId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivityById(@PathVariable(name = "activityId", required = false) Integer id) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        activityService.isActivityExistWithGivenId(id);

        ActivityDTO activityDTO = activityService.findActivityByActivityId(id);
        activityDTO.setImgUrl(ResourceUtil.convertToString(activityDTO.getImgUrl()));

        return GenericResponse.withSuccess(HttpStatus.OK, "specific activity by id", activityDTO);
    }

    @GetMapping("/activities/{activityId}/reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findReviewsByActivityId(@PathVariable(name = "activityId", required = false) Integer id,
                                                   @RequestParam(name = "offset", required = false) Integer offset) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id) || ValidationUtil.isNull(offset)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        activityService.isActivityExistWithGivenId(id);

        List<ActivityReview> reviews = activityService.findReviewsByActivityId(id, offset);
        return GenericResponse.withSuccess(HttpStatus.OK, "reviews of specific activity", reviews);
    }

    @PostMapping("/activities/{activityId}/reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addActivityReview(@PathVariable(value = "activityId") Integer id,
                                  @RequestParam(value = "description") String description) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id) || ValidationUtil.isNullOrEmpty(description)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        activityService.isActivityExistWithGivenId(id);

        //primcipal
        ActivityReview review = new ActivityReview();
        Activity activity = new Activity();
        activity.setId(id);
        review.setActivity(activity);
        review.setDescription(description.trim());
        review.setDateOfReg(LocalDateTime.now().toString());
        review.setStatus(ActivityConstants.ACTIVITY_REVIEW_STATUS_ACTIVE);

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        review.setUser(user);

        //  URI location = ServletUriComponentsBuilder
        //      .fromCurrentRequest()
        //      .path("/{activityId}")
        //      .buildAndExpand(savedReview.getId()).toUri();
        //
        //  return ResponseEntity.created(location).build();        return type --->ResponseEntity<Object>

        activityService.addActivityReview(review);
    }

    @DeleteMapping("activity/reviews/{reviewId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteActivityReview(@PathVariable(value = "reviewId") Integer id,
                                     Principal principal) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        activityService.isActivityReviewExistWithGivenId(id);

        //principal
        User user = new User();
        user.setUsername(principal.getName());

        ActivityReview review = new ActivityReview();
        review.setId(id);
        review.setUser(user);

        activityService.deleteActivityReview(review);
    }

    @PostMapping("/activities")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addActivity(@RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "description", required = false) String description,
                            @RequestParam(value = "file", required = false) MultipartFile multipartFile) throws ActivityCredentialsException, IOException {

        if (ValidationUtil.isNullOrEmpty(title, description)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (!multipartFile.isEmpty()) {
            if (!(multipartFile.getOriginalFilename().endsWith(".jpg")
                    || multipartFile.getOriginalFilename().endsWith(".jpeg")
                    || multipartFile.getOriginalFilename().endsWith(".png"))) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (multipartFile.getSize() >= maxFileSize) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }
        }

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Activity activity = new Activity();
        activity.setUser(user);
        activity.setTitle(title.trim());
        activity.setDescription(description.trim());
        activity.setViewCount(0);
        activity.setDateOfReg(LocalDateTime.now().toString());
        activity.setStatus(ActivityConstants.ACTIVITY_STATUS_WAITING);

        if (!multipartFile.isEmpty()) {
            Path pathToSaveFile = Paths.get(imageUploadPath, "activities", user.getUsername());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "##" + multipartFile.getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(multipartFile.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("activities", user.getUsername(), fileName);

            activity.setImgUrl(DatatypeConverter.printHexBinary(pathToSaveDb.toString().getBytes()));

        } else {
            activity.setImgUrl(defaultActivityHexCode);
        }

        activityService.addActivity(activity);
    }

    @GetMapping("/activities/{activityId}/responds")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivityResponds(@PathVariable(value = "activityId", required = false) Integer id,
                                                @RequestParam(value = "respond", required = false) Integer respond,
                                                @RequestParam(value = "offset", required = false) Integer offset) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id) || ValidationUtil.isNull(respond) || ValidationUtil.isNull(offset)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (respond.compareTo(ActivityConstants.ACTIVITY_RESPOND_STATUS_ACTIVE) != 0 && respond.compareTo(ActivityConstants.ACTIVITY_RESPOND_STATUS_INACTIVE) != 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ACTIVITY_RESPOND_MUST_BE_1_OR_0);
        }

        activityService.isActivityExistWithGivenId(id);

        List<ActivityRespond> responds = activityService.findActivityRespondsByRespond(id, respond, offset);
        return GenericResponse.withSuccess(HttpStatus.OK, "responds of specific activity", responds);
    }

    @PutMapping("/activities/{activityId}/responds")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    public void updateActivityRespond(@PathVariable(value = "activityId", required = false) Integer id,
                                      @RequestParam(value = "respond", required = false) Integer respond) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id) || ValidationUtil.isNull(respond)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (respond.compareTo(ActivityConstants.ACTIVITY_RESPOND_STATUS_ACTIVE) != 0 && respond.compareTo(ActivityConstants.ACTIVITY_RESPOND_STATUS_INACTIVE) != 0) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ACTIVITY_RESPOND_MUST_BE_1_OR_0);
        }

        activityService.isActivityExistWithGivenId(id);

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        ActivityRespond activityRespond = new ActivityRespond();
        Activity activity = new Activity();
        activity.setId(id);
        activityRespond.setActivity(activity);
        activityRespond.setUser(user);
        activityRespond.setRespond(respond);
        activityRespond.setDateOfReg(LocalDateTime.now().toString());
        activityRespond.setStatus(ActivityConstants.ACTIVITY_RESPOND_STATUS_ACTIVE);

        activityService.updateActivityRespond(activityRespond);
    }

    @GetMapping("/users/{username}/activities")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivitiesByUsername(@PathVariable(value = "username",required = false) String username,
                                                    @RequestParam(name = "page", required = false) Integer page,
                                                    HttpServletResponse response) throws ActivityCredentialsException, UserCredentialsException {
        if (ValidationUtil.isNullOrEmpty(username) || ValidationUtil.isNull(page)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = activityService.findCountOfAllActivitiesByUsername(username);
        int totalPages = 0;
        int offset = 0;

        if(total != 0) {
            totalPages = (int) Math.ceil((double) total / 3);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 3;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 3;
            };
        }

        userService.isUserExistWithGivenUsername(username);

        List<ActivityDTO> activities = activityService.findActivitiesByUsername(username, offset);
        for (ActivityDTO activityDTO : activities) {
            if (activityDTO.getImgUrl() == null) {
                continue;
            }
            activityDTO.setImgUrl(ResourceUtil.convertToString(activityDTO.getImgUrl()));
        }

        response.setIntHeader("Total-Pages", totalPages);
        return GenericResponse.withSuccess(HttpStatus.OK, "activities of specific employee", activities);
    }

    @GetMapping("/users/{username}/activities/top-three")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findTopThreeActivitiesByUsername(@PathVariable(value = "username",required = false) String username) throws ActivityCredentialsException, UserCredentialsException {
        if (ValidationUtil.isNullOrEmpty(username)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        userService.isUserExistWithGivenUsername(username);

        List<ActivityDTO> activities = activityService.findTopThreeActivitiesByUsername(username);
        for (ActivityDTO activityDTO : activities) {
            if (activityDTO.getImgUrl() == null) {
                continue;
            }
            activityDTO.setImgUrl(ResourceUtil.convertToString(activityDTO.getImgUrl()));
        }

        return GenericResponse.withSuccess(HttpStatus.OK, "top three activities of specific employee", activities);
    }

    @PutMapping("/activities/{activityId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    public void updateActivity(@PathVariable(value = "activityId", required = false) Integer id,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "description", required = false) String description,
                               @RequestParam(value = "file", required = false) MultipartFile multipartFile) throws ActivityCredentialsException, IOException {
        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Activity activity = new Activity();

        if (ValidationUtil.isNull(id) || ValidationUtil.isNullOrEmpty(title, description)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (ValidationUtil.isNull(multipartFile) || multipartFile.isEmpty()) {
            activity.setImgUrl("none");

        } else {
            if (!(multipartFile.getOriginalFilename().endsWith(".jpg")
                    || multipartFile.getOriginalFilename().endsWith(".jpeg")
                    || multipartFile.getOriginalFilename().endsWith(".png"))) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (multipartFile.getSize() >= maxFileSize) {
                throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }

            Path pathToSaveFile = Paths.get(imageUploadPath, "activities", user.getUsername());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "##" + multipartFile.getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(multipartFile.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("activities", user.getUsername(), fileName);

            activity.setImgUrl(DatatypeConverter.printHexBinary(pathToSaveDb.toString().getBytes()));
        }

        activity.setId(id);
        activity.setUser(user);
        activity.setTitle(title);
        activity.setDescription(description);

        activityService.updateActivity(activity);
    }

    @DeleteMapping("/activities/{activityId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    public void deleteActivity(@PathVariable(value = "activityId", required = false) Integer id) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        activityService.isActivityExistWithGivenId(id);

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Activity activity = new Activity();
        activity.setId(id);
        activity.setUser(user);
        activity.setDateOfDel(LocalDateTime.now().toString());

        activityService.deleteActivity(activity);
    }

    @GetMapping("/activities/keyword")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivitiesByKeyword(@RequestParam(value = "page", required = false) Integer page,
                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                                   HttpServletResponse response) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(page) || ValidationUtil.isNullOrEmpty(keyword)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = activityService.findCountOfAllActivitiesByKeyword(keyword.trim());
        int totalPages = 0;
        int offset = 0;

        if (total != 0) {
            totalPages = (int) Math.ceil((double) total / 10);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 10;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 10;
            };
        }

        List<Activity> activities = activityService.findActivitiesByKeyword(keyword.trim(), offset);
        for (Activity activity : activities) {
            if (activity.getImgUrl() == null) {
                continue;
            }
            activity.setImgUrl(ResourceUtil.convertToString(activity.getImgUrl()));
        }

        response.setIntHeader("Total-Pages", totalPages);
        return GenericResponse.withSuccess(HttpStatus.OK, "activities by keyword", activities);
    }

    @GetMapping("/activities/random")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivitiesRandomly() {
        List<Activity> activities = activityService.findActivitiesRandomly();
        return GenericResponse.withSuccess(HttpStatus.OK, "random activities", activities);
    }

    @GetMapping("/activities/top-three")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findActivitiesByLastAddedTime() {
        List<ActivityDTO> activities = activityService.findTopActivitiesByLastAddedTime();
        for (ActivityDTO activityDTO : activities) {
            if (activityDTO.getImgUrl() == null) {
                continue;
            }
            activityDTO.setImgUrl(ResourceUtil.convertToString(activityDTO.getImgUrl()));
        }

        return GenericResponse.withSuccess(HttpStatus.OK, "last added activities", activities);
    }

    @GetMapping("/activities/top-three/responds")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findTopThreeActivitiesByLastAddedTime() {
        //principal
        User user = new User();
        user.setUsername("aminhasanov21@gmail.com");

        Map<Integer, Integer> activities = activityService.findTopThreeActivitiesByLastAddedTime(user.getUsername());
        return GenericResponse.withSuccess(HttpStatus.OK, "responds of top three activities by username and last added time", activities);
    }

//    @GetMapping("/activities/count")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public GenericResponse findCountOfAllActivities() {
//        int count = activityService.findCountOfAllActivities();
//        return GenericResponse.withSuccess(HttpStatus.OK, "count of all activities", count);
//    }

    @PutMapping("/activities/{activityId}/view-count")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    public void incrementViewCountOfActivityById(@PathVariable(name = "activityId", required = false) Integer id) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }
        activityService.isActivityExistWithGivenId(id);
        activityService.incrementViewCountOfActivityById(id);
    }

    @GetMapping("/activities/{activityId}/respond")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findRespondOfActivity(@PathVariable(name = "activityId", required = false) Integer id) throws ActivityCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new ActivityCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        activityService.isActivityExistWithGivenId(id);

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Map<Integer, Integer> respond = activityService.findRespondOfActivity(user.getUsername(), id);
        return GenericResponse.withSuccess(HttpStatus.OK, "respond of specific activity", respond);
    }

}
