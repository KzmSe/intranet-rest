package az.gov.adra.repository.interfaces;

import az.gov.adra.exception.EmployeeCredentialsException;

public interface EmployeeRepository {

    void isEmployeeExistWithGivenId(int id) throws EmployeeCredentialsException;

}
