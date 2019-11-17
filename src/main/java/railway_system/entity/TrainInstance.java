package railway_system.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import railway_system.dao.MainDao;

@Getter
@Setter
@ToString
public class TrainInstance {
    private int id;
    private int from_id;
    private int to_id;
    private String from;
    private String to;
    private int from_order;
    private int to_order;
    private String departure_time;
    private String arrival_time;
    public TrainInstance(TrainLeg from, TrainLeg to){
        this.id = from.train_id;
        this.from_id = from.station_id;
        this.to_id = to.station_id;
        this.departure_time = from.departure_time;
        this.arrival_time = to.arrival_time;
        this.from_order = from.order;
        this.to_order = to.order;

        MainDao mainDao = new MainDaoIml();
        this.from = mainDao.getStation(this.from_id).getCity();
        this.to = mainDao.getStation(this.to_id).getCity();
    }

}
