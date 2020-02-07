package az.gov.adra.service;

import az.gov.adra.dataTransferObjects.DocumentDTO;
import az.gov.adra.repository.interfaces.DocumentRepository;
import az.gov.adra.service.interfaces.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private DocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public List<DocumentDTO> findAllDocuments(int offset) {
        return documentRepository.findAllDocuments(offset);
    }

    @Override
    public List<DocumentDTO> findTopDocumentsByLastAddedTime() {
        return documentRepository.findTopDocumentsByLastAddedTime();
    }

    @Override
    public int findCountOfAllDocuments() {
        return documentRepository.findCountOfAllDocuments();
    }

    @Override
    public int findCountOfAllDocumentsByKeyword(String keyword) {
        return documentRepository.findCountOfAllDocumentsByKeyword(keyword);
    }

    @Override
    public List<DocumentDTO> findDocumentsByKeyword(String keyword, int offset) {
        return documentRepository.findDocumentsByKeyword(keyword, offset);
    }
}
