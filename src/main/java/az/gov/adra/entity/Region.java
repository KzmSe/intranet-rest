package az.gov.adra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
public class Region {

    private Integer id;
    private String name;
    private List<Department> departments;
    private String dateOfReg;
    private String dateOfDel;
    private Integer status;

    public Region() {
        this.departments = new LinkedList<>();
    }

    public void addDepartment(Department department) {
        if (departments == null) {
            departments = new LinkedList<>();
        }
        departments.add(department);
    }

}
