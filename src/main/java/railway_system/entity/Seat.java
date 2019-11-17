package railway_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Seat {
    private String date;
    private int seatNumber;
    private int wagonNumber;
    private int TrainLegOrder;
    private int TrainLegTrainId;
    private int ticketId;
}