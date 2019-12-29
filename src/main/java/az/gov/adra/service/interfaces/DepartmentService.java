package az.gov.adra.service.interfaces;

import az.gov.adra.entity.Department;
import az.gov.adra.exception.DepartmentCredentialsException;

import java.util.List;

public interface DepartmentService {

    List<Department> findAllDepartments();

    void isDepartmentExistWithGivenId(int id) throws DepartmentCredentialsException;

}
