package railway_system.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrainInstance {
    private int id;
    private int from_id;
    private int to_id;
    private String from;
    private String to;
    private String departure_time;
    private String arrival_time;
    public TrainInstance(TrainLeg from, TrainLeg to){
        this.id = id;
        this.from_id = from_id;
        this.to_id = to_id;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
    }

}
