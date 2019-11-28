package az.gov.adra.controller;

import az.gov.adra.entity.Department;
import az.gov.adra.entity.response.GenericResponse;
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
    @PreAuthorize("hasRole('ROLE_USER')")
    public GenericResponse findAllDepartments() {
        List<Department> departments = departmentService.findAllDepartments();
        return GenericResponse.withSuccess(HttpStatus.OK, "list of all departments", departments);
    }

}
