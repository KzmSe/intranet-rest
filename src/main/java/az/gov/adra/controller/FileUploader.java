package az.gov.adra.controller;

import az.gov.adra.constant.ActivityConstants;
import az.gov.adra.constant.PostConstants;
import az.gov.adra.entity.Activity;
import az.gov.adra.entity.Post;
import az.gov.adra.entity.User;
import az.gov.adra.exception.ActivityCredentialsException;
import az.gov.adra.exception.PostCredentialsException;
import az.gov.adra.service.interfaces.ActivityService;
import az.gov.adra.service.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileUploader {

    @Autowired
    private ActivityService activityService;

    @PostMapping("/savefile")
    public ResponseEntity<String> handleFileUpload(@RequestParam("title") String title,
                                                   @RequestParam("description") String description,
                                                   @RequestParam("file") MultipartFile file) throws IOException, PostCredentialsException, ActivityCredentialsException {
        String message = "file uploaded";

//        Path folder = Paths.get("C:\\uploads\\files");
//		String fileName = file.getOriginalFilename();
//        if (!Files.exists(folder)) {
//            Files.createDirectories(folder);
//        }
//        Path fullFilePath = Paths.get(folder.toString(), fileName);
//        Files.copy(file.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);


        User user = new User();
        user.setUsername("safura@gmail.com");

        Activity activity = new Activity();
        activity.setUser(user);
        activity.setTitle(title.trim());
        activity.setDescription(description.trim());
        activity.setViewCount(0);
        activity.setDateOfReg(LocalDateTime.now().toString());
        activity.setStatus(ActivityConstants.ACTIVITY_STATUS_WAITING);
        activity.setImgUrl(file.getOriginalFilename());

        activityService.addActivity(activity);

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
