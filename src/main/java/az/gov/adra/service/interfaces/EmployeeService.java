package az.gov.adra.service.interfaces;

import az.gov.adra.exception.EmployeeCredentialsException;

public interface EmployeeService {

    void isEmployeeExistWithGivenUsername(String username) throws EmployeeCredentialsException;

}
