package az.gov.adra.controller;

import az.gov.adra.entity.Position;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.entity.response.GenericResponseBuilder;
import az.gov.adra.service.interfaces.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping("/positions")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findAllPositions() {
        List<Position> positions = positionService.findAllPositions();
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("list of all positions")
                .withData(positions)
                .build();
    }

}
