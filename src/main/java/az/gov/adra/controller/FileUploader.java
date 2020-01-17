package az.gov.adra.controller;

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
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileUploader {

    @PostMapping("/savefile")
    public ResponseEntity<String> handleFileUpload(@RequestParam("title") String title,
                                                   @RequestParam("description") String description,
                                                   @RequestParam("file") MultipartFile file) throws IOException {
        String message = "file uploaded";

        Path folder = Paths.get("C:\\uploads\\files");
		String fileName = file.getOriginalFilename();
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }
        Path fullFilePath = Paths.get(folder.toString(), fileName);
        Files.copy(file.getInputStream(), fullFilePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
