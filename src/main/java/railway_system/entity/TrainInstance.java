package railway_system.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import railway_system.dao.CrudDao;
import railway_system.dao.CrudDaoImpl;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Getter
@Setter
@ToString
public class TrainInstance {
    private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    private int id;
    private int from_id;
    private int to_id;
    private String from;
    private String to;
    private int from_order;
    private int to_order;
    private String departure_time;
    private String arrival_time;
    private String initial_date;
    private int price;

    public TrainInstance(TrainLeg from, TrainLeg to, Calendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        this.id = from.train_id;
        this.from_id = from.station_id;
        this.to_id = to.station_id;
        c = (Calendar) c.clone();
        this.departure_time = sdf.format(c.getTime()) + " " + from.departure_time;

        this.from_order = from.order;
        this.to_order = to.order;

        c.add(Calendar.DATE, -from.arrival_day);
        this.initial_date = sdf.format(c.getTime());

        c.add(Calendar.DATE, to.arrival_day);  // number of days to add
        this.arrival_time = sdf.format(c.getTime()) + " " + to.arrival_time;


        CrudDao crudDao = new CrudDaoImpl();
        this.from = crudDao.getStation(this.from_id).getCity();
        this.to = crudDao.getStation(this.to_id).getCity();
        this.price = crudDao.readTrainType(id).getPrice() * (this.to_order - this.from_order);

    }

}
