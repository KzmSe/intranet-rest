package az.gov.adra.service.interfaces;

import az.gov.adra.exception.EmployeeCredentialsException;

public interface EmployeeService {

    void isEmployeeExistWithGivenId(int id) throws EmployeeCredentialsException;

}
