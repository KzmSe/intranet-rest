package az.gov.adra.controller;

import az.gov.adra.entity.Gallery;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.service.interfaces.GalleryService;
import az.gov.adra.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping("/galleries")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAllGalleries(@RequestParam(value = "page", required = false) Integer page,
                                            HttpServletResponse response) {
        int total = galleryService.findCountOfAllGalleries();
        int totalPages = 0;
        int offset = 0;

        if (total != 0) {
            totalPages = (int) Math.ceil((double) total / 10);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 10;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 10;
            };
        }

        List<Gallery> galleries = galleryService.findAllGalleries(offset);
        for (Gallery gallery : galleries) {
            if (gallery.getImgUrl() == null) {
                continue;
            }
            gallery.setImgUrl(ResourceUtil.convertToString(gallery.getImgUrl()));
        }

        response.setIntHeader("Total-Pages", totalPages);
        return GenericResponse.withSuccess(HttpStatus.OK, "list of galleries", galleries);
    }

}
