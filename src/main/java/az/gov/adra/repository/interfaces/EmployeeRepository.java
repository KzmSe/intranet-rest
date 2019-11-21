package az.gov.adra.repository.interfaces;

import az.gov.adra.exception.EmployeeCredentialsException;

public interface EmployeeRepository {

    void isEmployeeExistWithGivenUsername(String username) throws EmployeeCredentialsException;

}
