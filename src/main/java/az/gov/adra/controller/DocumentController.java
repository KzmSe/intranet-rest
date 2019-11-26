package az.gov.adra.controller;

import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.DocumentDTO;
import az.gov.adra.dataTransferObjects.PaginationForDocumentDTO;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.DocumentCredentialsException;
import az.gov.adra.service.interfaces.DocumentService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    @Value("${file.upload.path.win}")
    private String imageUploadPath;


    @GetMapping("/documents")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAllDocuments(@RequestParam(value = "page", required = false) Integer page) throws DocumentCredentialsException {
        if (ValidationUtil.isNull(page)) {
            throw new DocumentCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = documentService.findCountOfAllDocuments();
        int totalPage = 0;
        int offset = 0;

        if (total != 0) {
            totalPage = (int) Math.ceil((double) total / 10);

            if (page != null && page >= totalPage) {
                offset = (totalPage - 1) * 10;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 10;
            };
        }

        List<DocumentDTO> documents = documentService.findAllDocuments(offset);
        PaginationForDocumentDTO dto = new PaginationForDocumentDTO();
        dto.setTotalPages(totalPage);
        dto.setDocumentDTOS(documents);

//        for (DocumentDTO document : documents) {
//            if (document.getFileUrl() != null) {
//                String fileUrl = new String(DatatypeConverter.parseHexBinary(document.getFileUrl()));
//                Path fullFilePath = Paths.get(imageUploadPath, fileUrl);
//                if (Files.exists(fullFilePath)) {
//                    File file = fullFilePath.toFile();
//                    document.setFile(file);
//                }
//            }
//        }

        return GenericResponse.withSuccess(HttpStatus.OK, "list of documents", dto);
    }

    @GetMapping("/documents/keyword")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findDocumentsByKeyword(@RequestParam(value = "page", required = false) Integer page,
                                                  @RequestParam(value = "keyword", required = false) String keyword) throws DocumentCredentialsException {
        if (ValidationUtil.isNull(page) || ValidationUtil.isNullOrEmpty(keyword)) {
            throw new DocumentCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = documentService.findCountOfAllDocumentsByKeyword(keyword.trim());
        int totalPage = 0;
        int offset = 0;

        if (total != 0) {
            totalPage = (int) Math.ceil((double) total / 10);

            if (page != null && page >= totalPage) {
                offset = (totalPage - 1) * 10;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 10;
            };
        }

        List<DocumentDTO> documents = documentService.findDocumentsByKeyword(keyword.trim(), offset);
        PaginationForDocumentDTO dto = new PaginationForDocumentDTO();
        dto.setTotalPages(totalPage);
        dto.setDocumentDTOS(documents);

        return GenericResponse.withSuccess(HttpStatus.OK, "documents by keyword", dto);
    }

    @GetMapping("/documents/top-three")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findDocumentsByLastAddedTime() {
        List<DocumentDTO> documents = documentService.findTopDocumentsByLastAddedTime();
        return GenericResponse.withSuccess(HttpStatus.OK, "last added documents", documents);
    }

}
