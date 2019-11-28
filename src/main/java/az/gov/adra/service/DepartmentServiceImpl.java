package az.gov.adra.service;

import az.gov.adra.entity.Department;
import az.gov.adra.exception.DepartmentCredentialsException;
import az.gov.adra.repository.interfaces.DepartmentRepository;
import az.gov.adra.service.interfaces.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Department> findAllDepartments() {
        return departmentRepository.findAllDepartments();
    }

    @Override
    public void isDepartmentExistWithGivenId(int id) throws DepartmentCredentialsException {
        departmentRepository.isDepartmentExistWithGivenId(id);
    }

}
