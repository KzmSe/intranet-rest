package az.gov.adra.controller;

import az.gov.adra.constant.CommandConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.CommandDTO;
import az.gov.adra.entity.Command;
import az.gov.adra.entity.User;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.entity.response.GenericResponseBuilder;
import az.gov.adra.exception.CommandCredentialsException;
import az.gov.adra.service.interfaces.CommandService;
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
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class CommandController {

    @Autowired
    private CommandService commandService;
    @Value("${file.upload.path.win}")
    private String imageUploadPath;
    private final int maxFileSize = 3145728;

    @GetMapping("/commands")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findAllCommands(@RequestParam(name = "page", required = false) Integer page) throws CommandCredentialsException {
        if (ValidationUtil.isNull(page)) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = commandService.findCountOfAllCommands();
        int totalPages = 0;
        int offset = 0;

        if (total != 0) {
            totalPages = (int) Math.ceil((double) total / 9);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 9;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 9;
            };
        }

        List<CommandDTO> commands = commandService.findAllCommands(offset);
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("list of commands")
                .withData(commands)
                .withTotalPages(totalPages)
                .build();
    }

    @GetMapping("/commands/{commandId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findCommandById(@PathVariable(name = "commandId", required = false) Integer id) throws CommandCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        commandService.isCommandExistWithGivenId(id);
        CommandDTO command = commandService.findCommandByCommandId(id);
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("specific command by id")
                .withData(command)
                .build();
    }

    @PostMapping("/commands")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCommand(@RequestParam(value = "title", required = false) String title,
                           @RequestParam(value = "description", required = false) String description,
                           @RequestParam(value = "file", required = false) MultipartFile file,
                           Principal principal) throws CommandCredentialsException, IOException {
        boolean fileIsExist = false;

        if (ValidationUtil.isNullOrEmpty(title, description)) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (!ValidationUtil.isNull(file) && !file.isEmpty()) {
            fileIsExist = true;
        }

        if (fileIsExist) {
            if (!file.getOriginalFilename().endsWith(".pdf")) {
                throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (file.getSize() >= maxFileSize) {
                throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }
        }

        //principal
        User user = new User();
        user.setUsername(principal.getName());

        Command command = new Command();
        command.setUser(user);
        command.setTitle(title);
        command.setDescription(description);
        command.setDateOfReg(LocalDateTime.now().toString());
        command.setStatus(CommandConstants.COMMAND_STATUS_ACTIVE);

        if (fileIsExist) {
            Path pathToSaveFile = Paths.get(imageUploadPath, "commands", user.getUsername());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "&&" + file.getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(file.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("commands", user.getUsername(), fileName);

            command.setImgUrl(pathToSaveDb.toString());

        } else {
            command.setImgUrl(null);
        }

        commandService.addCommand(command);
    }

    @GetMapping("/commands/top-three")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findTopThreeCommands() {
        List<CommandDTO> commands = commandService.findTopThreeCommandsByLastAddedTime();
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("top three commands by last added time")
                .withData(commands)
                .build();
    }

}
