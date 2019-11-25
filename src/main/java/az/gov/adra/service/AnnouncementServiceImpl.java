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
    public List<Announcement> findAllAnnouncements(int offset) {
        return announcementRepository.findAllAnnouncements(offset);
    }

    @Override
    public Announcement findAnnouncementByAnnouncementId(int id) {
        return announcementRepository.findAnnouncementByAnnouncementId(id);
    }

    @Override
    public List<Announcement> findTopThreeAnnouncementsByLastAddedTime() {
        return announcementRepository.findTopThreeAnnouncementsByLastAddedTime();
    }

    @Override
    public int findCountOfAllAnnouncements() {
        return announcementRepository.findCountOfAllAnnouncements();
    }

    @Override
    public void isAnnouncementExistWithGivenId(int id) throws AnnouncementCredentialsException {
        announcementRepository.isAnnouncementExistWithGivenId(id);
    }

}
