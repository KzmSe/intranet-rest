package az.gov.adra.service.interfaces;

import az.gov.adra.dataTransferObjects.DocumentDTO;

import java.util.List;

public interface DocumentService {

    List<DocumentDTO> findAllDocuments(int offset);

    List<DocumentDTO> findTopDocumentsByLastAddedTime();

    int findCountOfAllDocuments();

    int findCountOfAllDocumentsByKeyword(String keyword);

    List<DocumentDTO> findDocumentsByKeyword(String keyword, int offset);

}
