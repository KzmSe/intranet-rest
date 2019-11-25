package az.gov.adra.controller;

import az.gov.adra.constant.CommandConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.CommandDTO;
import az.gov.adra.entity.Command;
import az.gov.adra.entity.Employee;
import az.gov.adra.entity.User;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.CommandCredentialsException;
import az.gov.adra.service.interfaces.CommandService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAllCommands(@RequestParam(name = "page", required = false) Integer page) throws CommandCredentialsException {
        if (ValidationUtil.isNull(page)) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = commandService.findCountOfAllCommands();
        int offset = 0;

        if (total != 0) {
            int totalPage = (int) Math.ceil((double) total / 10);

            if (page != null && page >= totalPage) {
                offset = (totalPage - 1) * 10;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 10;
            };
        }

        List<CommandDTO> commands = commandService.findAllCommands(offset);

//        for (CommandDTO command : commands) {
//            if (command.getImgUrl() != null) {
//                String imgUrl = new String(DatatypeConverter.parseHexBinary(command.getImgUrl()));
//                Path fullFilePath = Paths.get(imageUploadPath, imgUrl);
//                if (Files.exists(fullFilePath)) {
//                    File file = fullFilePath.toFile();
//                    command.setFile(file);
//                }
//            }
//        }

        return GenericResponse.withSuccess(HttpStatus.OK, "list of commands", commands);
    }

    @GetMapping("/commands/{commandId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findCommandById(@PathVariable(name = "commandId", required = false) Integer id) throws CommandCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        commandService.isCommandExistWithGivenId(id);

        CommandDTO command = commandService.findCommandByCommandId(id);

//        if (command.getImgUrl() != null) {
//            String imgUrl = new String(DatatypeConverter.parseHexBinary(command.getImgUrl()));
//            Path fullFilePath = Paths.get(imageUploadPath, imgUrl);
//            if (Files.exists(fullFilePath)) {
//                File file = fullFilePath.toFile();
//                command.setFile(file);
//            }
//        }

        return GenericResponse.withSuccess(HttpStatus.OK, "specific command by id", command);
    }

    @PostMapping("/commands")
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public void addCommand(@RequestParam(value = "title", required = false) String title,
                           @RequestParam(value = "description", required = false) String description,
                           @RequestParam(value = "file", required = false) MultipartFile multipartFile) throws CommandCredentialsException, IOException {
        if (ValidationUtil.isNullOrEmpty(title, description)) {
            throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (!multipartFile.isEmpty()) {
            if (!multipartFile.getOriginalFilename().endsWith(".pdf")) {
                throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_INVALID_FILE_TYPE);
            }

            if (multipartFile.getSize() >= maxFileSize) {
                throw new CommandCredentialsException(MessageConstants.ERROR_MESSAGE_FILE_SIZE_MUST_BE_SMALLER_THAN_5MB);
            }
        }

        //principal
        User user = new User();
        user.setUsername("safura@gmail.com");

        Command command = new Command();
        command.setUser(user);
        command.setTitle(title);
        command.setDescription(description);
        command.setDateOfReg(LocalDateTime.now().toString());
        command.setStatus(CommandConstants.COMMAND_STATUS_ACTIVE);

        if (!multipartFile.isEmpty()) {
            Path pathToSaveFile = Paths.get(imageUploadPath, "commands", user.getUsername());

            if (!Files.exists(pathToSaveFile)) {
                Files.createDirectories(pathToSaveFile);
            }

            String fileName = UUID.randomUUID() + "##" + multipartFile.getOriginalFilename();
            Path fullFilePath = Paths.get(pathToSaveFile.toString(), fileName);
            Files.copy(multipartFile.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);
            Path pathToSaveDb = Paths.get("commands", user.getUsername(), fileName);

            command.setImgUrl(DatatypeConverter.printHexBinary(pathToSaveDb.toString().getBytes()));

        } else {
            command.setImgUrl(null);
        }

        commandService.addCommand(command);
    }

    @GetMapping("/commands/top-three")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findTopThreeCommands() {

        List<CommandDTO> commands = commandService.findTopThreeCommandsByLastAddedTime();
        return GenericResponse.withSuccess(HttpStatus.OK, "top three commands by last added time", commands);
    }


}
