package az.gov.adra.service;

import az.gov.adra.exception.EmployeeCredentialsException;
import az.gov.adra.repository.interfaces.EmployeeRepository;
import az.gov.adra.service.interfaces.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void isEmployeeExistWithGivenUsername(String username) throws EmployeeCredentialsException {
        employeeRepository.isEmployeeExistWithGivenUsername(username);
    }

}
