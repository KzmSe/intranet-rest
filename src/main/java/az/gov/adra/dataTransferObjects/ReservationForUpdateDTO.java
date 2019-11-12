package az.gov.adra.dataTransferObjects;

import az.gov.adra.entity.Employee;
import az.gov.adra.entity.Room;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationForUpdateDTO {

    private Long id;
    private Employee employee;
    private String fullname;
    private String topic;
    private Date date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Room room;
    private int status;
    private int status2;
    private List<Room> rooms;

}

