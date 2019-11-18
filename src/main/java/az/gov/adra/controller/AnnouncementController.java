package az.gov.adra.controller;

import az.gov.adra.constant.AnnouncementConstants;
import az.gov.adra.constant.MessageConstants;
import az.gov.adra.entity.Announcement;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.exception.AnnouncementCredentialsException;
import az.gov.adra.service.interfaces.AnnouncementService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/announcements")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAllAnnouncements(@RequestParam(name = "importanceLevel", required = false) String importanceLevel,
                                                @RequestParam(name = "fetchNext", required = false) Integer fetchNext) throws AnnouncementCredentialsException {
        if (ValidationUtil.isNullOrEmpty(importanceLevel) || (!importanceLevel.equals(AnnouncementConstants.ANNOUNCEMENT_IMPORTANCE_LEVEL_NECESSARY) && !importanceLevel.equals(AnnouncementConstants.ANNOUNCEMENT_IMPORTANCE_LEVEL_UNNECESSARY))) {
            throw new AnnouncementCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        if (ValidationUtil.isNull(fetchNext)) {
            fetchNext = 10;
        }

        List<Announcement> allAnnouncements = announcementService.findAllAnnouncementsByImportanceLevel(importanceLevel, fetchNext);
        return GenericResponse.withSuccess(HttpStatus.OK, "list of announcements", allAnnouncements);
    }

    @GetMapping("/announcements/{announcementId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAnnouncementByAnnouncementId(@PathVariable(name = "announcementId", required = false) Integer id) throws AnnouncementCredentialsException {
        announcementService.isAnnouncementExistWithGivenId(id);

        if (ValidationUtil.isNull(id)) {
            throw new AnnouncementCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }
        Announcement announcement = announcementService.findAnnouncementByAnnouncementId(id);
        return GenericResponse.withSuccess(HttpStatus.OK, "specific announcement by id", announcement);
    }

    @GetMapping("/announcements/necessary")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findTopNecessaryAnnouncement() throws AnnouncementCredentialsException {
        Announcement announcement = announcementService.findTopAnnouncementByImportanceLevel(AnnouncementConstants.ANNOUNCEMENT_IMPORTANCE_LEVEL_NECESSARY);

        return GenericResponse.withSuccess(HttpStatus.OK, "top necessary announcement", announcement);
    }

}
