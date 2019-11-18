package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.Announcement;
import az.gov.adra.exception.AnnouncementCredentialsException;

import java.util.List;

public interface AnnouncementRepository {

    List<Announcement> findAllAnnouncementsByImportanceLevel(String importanceLevel, int fetchNext);

    Announcement findAnnouncementByAnnouncementId(int id);

    Announcement findTopAnnouncementByImportanceLevel(String importanceLevel);

    void isAnnouncementExistWithGivenId(int id) throws AnnouncementCredentialsException;

}
