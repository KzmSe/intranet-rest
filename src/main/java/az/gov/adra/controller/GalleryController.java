package az.gov.adra.controller;

import az.gov.adra.entity.Gallery;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.entity.response.GenericResponseBuilder;
import az.gov.adra.service.interfaces.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping("/galleries")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findAllGalleries(@RequestParam(value = "page", required = false) Integer page) {
        int total = galleryService.findCountOfAllGalleries();
        int totalPages = 0;
        int offset = 0;

        if (total != 0) {
            totalPages = (int) Math.ceil((double) total / 8);

            if (page != null && page >= totalPages) {
                offset = (totalPages - 1) * 8;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 8;
            };
        }

        List<Gallery> galleries = galleryService.findAllGalleries(offset);
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("list of galleries")
                .withData(galleries)
                .withTotalPages(totalPages)
                .build();
    }

}
