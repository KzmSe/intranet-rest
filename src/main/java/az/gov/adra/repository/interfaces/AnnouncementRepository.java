package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.Announcement;
import az.gov.adra.exception.AnnouncementCredentialsException;

import java.util.List;

public interface AnnouncementRepository {

    List<Announcement> findAllAnnouncements(int offset);

    Announcement findAnnouncementByAnnouncementId(int id);

    List<Announcement> findTopThreeAnnouncementsByLastAddedTime();

    int findCountOfAllAnnouncements();

    void isAnnouncementExistWithGivenId(int id) throws AnnouncementCredentialsException;

}
