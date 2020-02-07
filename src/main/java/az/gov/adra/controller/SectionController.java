package az.gov.adra.controller;

import az.gov.adra.constant.MessageConstants;
import az.gov.adra.entity.Section;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.entity.response.GenericResponseBuilder;
import az.gov.adra.exception.DepartmentCredentialsException;
import az.gov.adra.exception.SectionCredentialsException;
import az.gov.adra.service.interfaces.DepartmentService;
import az.gov.adra.service.interfaces.SectionService;
import az.gov.adra.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SectionController {

    @Autowired
    private SectionService sectionService;
    @Autowired
    private DepartmentService departmentService;


    @GetMapping("/sections")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findAllSections() {
        List<Section> sections = sectionService.findAllSections();
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("list of all sections")
                .withData(sections)
                .build();
    }

    @GetMapping("/departments/{departmentId}/sections")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findAllSections(@PathVariable(value = "departmentId", required = false) Integer id) throws SectionCredentialsException, DepartmentCredentialsException {
        if (ValidationUtil.isNull(id)) {
            throw new SectionCredentialsException(MessageConstants.ERROR_MESSAGE_ONE_OR_MORE_FIELDS_ARE_EMPTY);
        }

        departmentService.isDepartmentExistWithGivenId(id);

        List<Section> sections = sectionService.findSectionsByDepartmentId(id);
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("list of specific sections of department")
                .withData(sections)
                .build();
    }

}
