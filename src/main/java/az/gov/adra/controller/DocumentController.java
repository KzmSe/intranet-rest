package az.gov.adra.controller;

import az.gov.adra.constant.CommandConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.CommandDTO;
import az.gov.adra.dataTransferObjects.DocumentDTO;
import az.gov.adra.entity.Command;
import az.gov.adra.entity.Employee;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.CommandCredentialsException;
import az.gov.adra.exception.DocumentCredentialsException;
import az.gov.adra.service.interfaces.DocumentService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.Document;
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
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    @Value("${file.upload.path.win}")
    private String imageUploadPath;

    //+
    @GetMapping("/documents")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAllDocuments(@RequestParam(value = "page", required = false) Integer page) {
        int documentCount = documentService.findCountOfAllDocuments();
        int totalPage = (int) Math.ceil((double) documentCount / 12);
        int offset = 0;

        if (page != null && page >= totalPage) {
            offset = (totalPage - 1) * 12;

        } else if (page != null && page > 1) {
            offset = (page - 1) * 12;
        }
        ;

        List<DocumentDTO> documents = documentService.findAllDocuments(offset);
        for (DocumentDTO document : documents) {
            if (document.getFileUrl() != null) {
                String fileUrl = new String(DatatypeConverter.parseHexBinary(document.getFileUrl()));
                Path fullFilePath = Paths.get(imageUploadPath, fileUrl);
                if (Files.exists(fullFilePath)) {
                    File file = fullFilePath.toFile();
                    document.setFile(file);
                }
            }
        }

        return GenericResponse.withSuccess(HttpStatus.OK, "list of documents", documentCount);
    }

    //+
    @GetMapping("/documents/keyword")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findDocumentsByKeyword(@RequestParam(value = "keyword", required = false) String keyword) throws DocumentCredentialsException {
        if (ValidationUtil.isNullOrEmpty(keyword)) {
            throw new DocumentCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        List<DocumentDTO> documents = documentService.findDocumentsByKeyword(keyword.trim());

        return GenericResponse.withSuccess(HttpStatus.OK, "documents by keyword", documents);
    }

}
