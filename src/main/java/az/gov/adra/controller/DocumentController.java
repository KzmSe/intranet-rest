package az.gov.adra.controller;

import az.gov.adra.constant.MessageConstants;
import az.gov.adra.dataTransferObjects.DocumentDTO;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.entity.response.GenericResponseBuilder;
import az.gov.adra.exception.DocumentCredentialsException;
import az.gov.adra.service.interfaces.DocumentService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;


    @GetMapping("/documents")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findAllDocuments(@RequestParam(value = "page", required = false) Integer page) throws DocumentCredentialsException {
        if (ValidationUtil.isNull(page)) {
            throw new DocumentCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = documentService.findCountOfAllDocuments();
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

        List<DocumentDTO> documents = documentService.findAllDocuments(offset);
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("list of documents")
                .withData(documents)
                .withTotalPages(totalPages)
                .build();
    }

    @GetMapping("/documents/keyword")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findDocumentsByKeyword(@RequestParam(value = "page", required = false) Integer page,
                                                  @RequestParam(value = "keyword", required = false) String keyword) throws DocumentCredentialsException {
        if (ValidationUtil.isNull(page) || ValidationUtil.isNullOrEmpty(keyword)) {
            throw new DocumentCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = documentService.findCountOfAllDocumentsByKeyword(keyword.trim());
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

        List<DocumentDTO> documents = documentService.findDocumentsByKeyword(keyword.trim(), offset);
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("documents by keyword")
                .withData(documents)
                .withTotalPages(totalPages)
                .build();
    }

    @GetMapping("/documents/top-three")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findDocumentsByLastAddedTime() {
        List<DocumentDTO> documents = documentService.findTopDocumentsByLastAddedTime();
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("last added documents")
                .withData(documents)
                .build();
    }

}
