package az.gov.adra.controller;

import az.gov.adra.dataTransferObjects.PaginationForGalleryDTO;
import az.gov.adra.entity.Gallery;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.service.interfaces.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping("/galleries")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAllGalleries(@RequestParam(value = "page", required = false) Integer page) {
        int total = galleryService.findCountOfAllGalleries();
        int totalPage = 0;
        int offset = 0;

        if (total != 0) {
            totalPage = (int) Math.ceil((double) total / 10);

            if (page != null && page >= totalPage) {
                offset = (totalPage - 1) * 10;

            } else if (page != null && page > 1) {
                offset = (page - 1) * 10;
            };
        }

        List<Gallery> galleries = galleryService.findAllGalleries(offset);
        PaginationForGalleryDTO dto = new PaginationForGalleryDTO();
        dto.setTotalPages(totalPage);
        dto.setGalleries(galleries);

        return GenericResponse.withSuccess(HttpStatus.OK, "list of galleries", dto);
    }

}
