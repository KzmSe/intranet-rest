package az.gov.adra.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Department {

    private Integer id;
    private String name;
    private List<Region> regions;
    private LocalDateTime dateOfReg;
    private LocalDateTime dateOfDel;
    private Integer status;

    public Department() {
        this.regions = new LinkedList<>();
    }

    public void addRegion(Region region) {
        if (regions == null) {
            regions = new LinkedList<>();
        }
        regions.add(region);
    }
}
