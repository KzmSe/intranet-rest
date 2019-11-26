package az.gov.adra.controller;

import az.gov.adra.constant.IdeaConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.entity.Employee;
import az.gov.adra.entity.Idea;
import az.gov.adra.entity.User;
import az.gov.adra.exception.IdeaCredentialsException;
import az.gov.adra.service.interfaces.IdeaService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class IdeaController {

    @Autowired
    private IdeaService ideaService;
    @Value("${file.upload.path.win}")
    private String imageUploadPath;
    private final int maxFileSize = 3145728;

    @PostMapping("/ideas")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addIdea(@RequestParam(value = "choice", required = false) String choice,
                        @RequestParam(value = "title", required = false) String title,
                        @RequestParam(value = "description", required = false) String description,
                        @RequestParam(value = "file", required = false) MultipartFile multipartFile) throws IdeaCredentialsException, IOException {
        if (ValidationUtil.isNullOrEmpty(choice, title, description)) {
            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (!choice.equals(IdeaConstants.IDEA_CHOICE_IDEA) && !choice.equals(IdeaConstants.IDEA_CHOICE_COMPLAINT)) {
            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_CHOICE_OF_IDEA_IS_INCORRECT);
        }

        if (!multipartFile.isEmpty()) {
            if (!(multipartFile.getOriginalFilename().endsWith(".jpg")
                    || multipartFile.getOriginalFilename().endsWith(".jpeg")
                    || multipartFile.getOriginalFilename().endsWith(".png")
                    || multipartFile.getOriginalFilename().endsWith(".doc")
                    || multipartFile.getOriginalFilename().endsWith(".docx")
                    || multipartFile.getOriginalFilename().endsWith(".xls")
                    || multipartFile.getOriginalFilename().endsWith(".xlsx")
                    || multipartFile.getOriginalFilename().endsWith(".ppt")
                    || multipartFile.getOriginalFilename().endsWith(".pptx")
                    || multipartFile.getOriginalFilename().endsWith(".pdf")
                    || multipartFile.getOriginalFilename().endsWith(".txt"))) {
                throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (multipartFile.getSize() >= maxFileSize) {
                throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }
        }

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Idea idea = new Idea();
        idea.setUser(user);
        idea.setChoice(choice);
        idea.setTitle(title);
        idea.setDescription(description);
        idea.setDateOfReg(LocalDateTime.now().toString());
        idea.setStatus(IdeaConstants.IDEA_STATUS_INACTIVE);

        if (!multipartFile.isEmpty()) {
            Path pathToSaveFile = Paths.get(imageUploadPath, "ideas", user.getUsername());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "##" + multipartFile.getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(multipartFile.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("ideas", user.getUsername(), fileName);

            idea.setImgUrl(DatatypeConverter.printHexBinary(pathToSaveDb.toString().getBytes()));

        } else {
            idea.setImgUrl(null);
        }

        ideaService.addIdea(idea);
    }

}
