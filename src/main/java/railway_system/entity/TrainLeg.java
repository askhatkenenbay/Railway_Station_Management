package railway_system.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class TrainLeg {
    int train_id;
    int order;
    int station_id;
    String arrival_time;
    String departure_time;
    int arrival_day;
}
