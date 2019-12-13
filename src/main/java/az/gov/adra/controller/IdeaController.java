package az.gov.adra.controller;

import az.gov.adra.constant.IdeaConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.IdeaDTOForAddIdea;
import az.gov.adra.entity.Idea;
import az.gov.adra.entity.User;
import az.gov.adra.exception.IdeaCredentialsException;
import az.gov.adra.service.interfaces.IdeaService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public void addIdea(@RequestBody IdeaDTOForAddIdea dto,
                        Principal principal) throws IdeaCredentialsException, IOException {
        boolean fileIsExist = false;

        if (ValidationUtil.isNullOrEmpty(dto.getChoice(), dto.getTitle(), dto.getDescription())) {
            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (!dto.getChoice().equals(IdeaConstants.IDEA_CHOICE_IDEA) && !dto.getChoice().equals(IdeaConstants.IDEA_CHOICE_COMPLAINT)) {
            throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_CHOICE_OF_IDEA_IS_INCORRECT);
        }

        if (!ValidationUtil.isNull(dto.getFile()) && !dto.getFile().isEmpty()) {
            fileIsExist = true;
        }

        if (fileIsExist) {
            if (!(dto.getFile().getOriginalFilename().endsWith(".jpg")
                    || dto.getFile().getOriginalFilename().endsWith(".jpeg")
                    || dto.getFile().getOriginalFilename().endsWith(".png")
                    || dto.getFile().getOriginalFilename().endsWith(".doc")
                    || dto.getFile().getOriginalFilename().endsWith(".docx")
                    || dto.getFile().getOriginalFilename().endsWith(".xls")
                    || dto.getFile().getOriginalFilename().endsWith(".xlsx")
                    || dto.getFile().getOriginalFilename().endsWith(".ppt")
                    || dto.getFile().getOriginalFilename().endsWith(".pptx")
                    || dto.getFile().getOriginalFilename().endsWith(".pdf")
                    || dto.getFile().getOriginalFilename().endsWith(".txt"))) {
                throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (dto.getFile().getSize() >= maxFileSize) {
                throw new IdeaCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }
        }

        //principal
        User user = new User();
        user.setUsername(principal.getName());

        Idea idea = new Idea();
        idea.setUser(user);
        idea.setChoice(dto.getChoice());
        idea.setTitle(dto.getTitle());
        idea.setDescription(dto.getDescription());
        idea.setDateOfReg(LocalDateTime.now().toString());
        idea.setStatus(IdeaConstants.IDEA_STATUS_WAITING);

        if (fileIsExist) {
            Path pathToSaveFile = Paths.get(imageUploadPath, "ideas", user.getUsername());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "##" + dto.getFile().getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(dto.getFile().getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("ideas", user.getUsername(), fileName);

            idea.setImgUrl(DatatypeConverter.printHexBinary(pathToSaveDb.toString().getBytes()));

        } else {
            idea.setImgUrl(null);
        }

        ideaService.addIdea(idea);
    }

}
