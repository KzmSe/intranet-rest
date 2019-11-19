package az.gov.adra.service.interfaces;

import az.gov.adra.dataTransferObjects.DocumentDTO;

import java.util.List;

public interface DocumentService {

    List<DocumentDTO> findAllDocuments(int offset);

    int findCountOfAllDocuments();

    List<DocumentDTO> findDocumentsByKeyword(String keyword);

}
