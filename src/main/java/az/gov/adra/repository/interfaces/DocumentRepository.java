package az.gov.adra.repository.interfaces;

import az.gov.adra.dataTransferObjects.DocumentDTO;

import java.util.List;

public interface DocumentRepository {

    List<DocumentDTO> findAllDocuments(int offset);

    List<DocumentDTO> findTopDocumentsByLastAddedTime();

    int findCountOfAllDocuments();

    int findCountOfAllDocumentsByKeyword(String keyword);

    List<DocumentDTO> findDocumentsByKeyword(String keyword, int offset);
}
