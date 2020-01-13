package az.gov.adra.controller;

import az.gov.adra.entity.Region;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.service.interfaces.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegionController {

    @Autowired
    private RegionService regionService;


    @GetMapping("/regions")
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAllRegions() {
        List<Region> regions = regionService.findAllRegions();
        return GenericResponse.withSuccess(HttpStatus.OK, "list of all regions", regions);
    }

}
