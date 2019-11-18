package az.gov.adra.service;

import az.gov.adra.entity.Announcement;
import az.gov.adra.exception.AnnouncementCredentialsException;
import az.gov.adra.repository.interfaces.AnnouncementRepository;
import az.gov.adra.service.interfaces.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private AnnouncementRepository announcementRepository;

    @Autowired
    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    public List<Announcement> findAllAnnouncementsByImportanceLevel(String importanceLevel, int fetchNext) {
        return announcementRepository.findAllAnnouncementsByImportanceLevel(importanceLevel, fetchNext);
    }

    @Override
    public Announcement findAnnouncementByAnnouncementId(int id) {
        return announcementRepository.findAnnouncementByAnnouncementId(id);
    }

    @Override
    public Announcement findTopAnnouncementByImportanceLevel(String importanceLevel) {
        return announcementRepository.findTopAnnouncementByImportanceLevel(importanceLevel);
    }

    @Override
    public void isAnnouncementExistWithGivenId(int id) throws AnnouncementCredentialsException {
        announcementRepository.isAnnouncementExistWithGivenId(id);
    }

}
