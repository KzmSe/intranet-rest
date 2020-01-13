package az.gov.adra.repository.interfaces;

import az.gov.adra.entity.Department;
import az.gov.adra.exception.DepartmentCredentialsException;

import java.util.List;

public interface DepartmentRepository {

    List<Department> findAllDepartments();

    void isDepartmentExistWithGivenId(int id) throws DepartmentCredentialsException;

}
