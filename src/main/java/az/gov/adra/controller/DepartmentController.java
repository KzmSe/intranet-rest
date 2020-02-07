package az.gov.adra.controller;

import az.gov.adra.entity.Department;
import az.gov.adra.entity.response.GenericResponse;
import az.gov.adra.entity.response.GenericResponseBuilder;
import az.gov.adra.service.interfaces.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;


    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_HR')")
    public GenericResponse findAllDepartments() {
        List<Department> departments = departmentService.findAllDepartments();
        return new GenericResponseBuilder()
                .withStatus(HttpStatus.OK.value())
                .withDescription("list of all departments")
                .withData(departments)
                .build();
    }

}
