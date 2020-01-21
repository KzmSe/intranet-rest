package az.gov.adra.controller;

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

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/announcements")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findAllAnnouncements(@RequestParam(name = "page", required = false) Integer page,
                                                HttpServletResponse response) throws AnnouncementCredentialsException {
        if (ValidationUtil.isNull(page)) {
            throw new AnnouncementCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        int total = announcementService.findCountOfAllAnnouncements();
        int totalPages = 0;
        int offset = 0;

        if (total != 0) {
            totalPages = (int) Math.ceil((double) total / 9);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 9;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 9;
            };
        }

        List<Announcement> allAnnouncements = announcementService.findAllAnnouncements(offset);
        response.setIntHeader("Total-Pages", totalPages);
        return GenericResponse.withSuccess(HttpStatus.OK, "list of announcements", allAnnouncements);
    }

    @GetMapping("/announcements/{announcementId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findAnnouncementByAnnouncementId(@PathVariable(name = "announcementId", required = false) Integer id) throws AnnouncementCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new AnnouncementCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        announcementService.isAnnouncementExistWithGivenId(id);
        Announcement announcement = announcementService.findAnnouncementByAnnouncementId(id);
        return GenericResponse.withSuccess(HttpStatus.OK, "specific announcement by id", announcement);
    }

    @GetMapping("/announcements/top-three")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findTopThreeAnnouncements() {
        List<Announcement> announcements = announcementService.findTopThreeAnnouncementsByLastAddedTime();
        return GenericResponse.withSuccess(HttpStatus.OK, "top three announcements by last added time", announcements);
    }

}
