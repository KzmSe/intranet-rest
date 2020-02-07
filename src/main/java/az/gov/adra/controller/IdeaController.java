package az.gov.adra.controller;

import az.gov.adra.constant.IdeaConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.IdeaDTO;
import az.gov.adra.entity.Idea;
import az.gov.adra.entity.User;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.entity.response.GenericResponseBuilder;
import az.gov.adra.exception.IdeaCredentialsException;
import az.gov.adra.service.interfaces.IdeaService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    //@PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addIdea(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "title", required = false) String title,
                        @RequestParam(value = "description", required = false) String description,
                        @RequestParam(value = "choice", required = false) String choice,
                        @RequestParam(value = "file", required = false) MultipartFile file
                        /*Principal principal*/) throws IdeaCredentialsException, IOException {
        boolean fileIsExist = false;

        if (ValidationUtil.isNullOrEmpty(choice, title, description)) {
            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (!choice.equals(IdeaConstants.IDEA_CHOICE_IDEA) && !choice.equals(IdeaConstants.IDEA_CHOICE_COMPLAINT)) {
            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_CHOICE_OF_IDEA_IS_INCORRECT);
        }

        if (!ValidationUtil.isNull(file) && !file.isEmpty()) {
            fileIsExist = true;
        }

        if (fileIsExist) {
            if (!(file.getOriginalFilename().endsWith(".jpg")
                    || file.getOriginalFilename().endsWith(".jpeg")
                    || file.getOriginalFilename().endsWith(".png")
                    || file.getOriginalFilename().endsWith(".doc")
                    || file.getOriginalFilename().endsWith(".docx")
                    || file.getOriginalFilename().endsWith(".xls")
                    || file.getOriginalFilename().endsWith(".xlsx")
                    || file.getOriginalFilename().endsWith(".ppt")
                    || file.getOriginalFilename().endsWith(".pptx")
                    || file.getOriginalFilename().endsWith(".pdf")
                    || file.getOriginalFilename().endsWith(".txt"))) {
                throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (file.getSize() >= maxFileSize) {
                throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }
        }

        //principal
        User user = new User();
        user.setUsername(username);

        Idea idea = new Idea();
        idea.setUser(user);
        idea.setChoice(choice);
        idea.setTitle(title);
        idea.setDescription(description);
        idea.setDateOfReg(LocalDateTime.now().toString());
        idea.setStatus(IdeaConstants.IDEA_STATUS_WAITING);

        if (fileIsExist) {
            Path pathToSaveFile = Paths.get(imageUploadPath, "ideas", user.getUsername());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "&&" + file.getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(file.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("ideas", user.getUsername(), fileName);

            idea.setImgUrl(pathToSaveDb.toString());

        } else {
            idea.setImgUrl(null);
        }

        ideaService.addIdea(idea);
    }

    @GetMapping("/ideas/count")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findCountOfAllIdeas() {
        int count = ideaService.findCountOfAllIdeas();
        IdeaDTO dto = new IdeaDTO();
        dto.setTotalCount(count);
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("count of all ideas and complaints")
                .withData(dto)
                .build();
    }

}
