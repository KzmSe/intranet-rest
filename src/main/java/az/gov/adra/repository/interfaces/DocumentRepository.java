package az.gov.adra.repository.interfaces;

import az.gov.adra.dataTransferObjects.DocumentDTO;

import java.util.List;

public interface DocumentRepository {

    List<DocumentDTO> findAllDocuments(int offset);

    int findCountOfAllDocuments();

    List<DocumentDTO> findDocumentsByKeyword(String keyword);
}
