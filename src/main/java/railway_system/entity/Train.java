package railway_system.entity;

public class Train {
    private int id;
    private String arrival_time;
    private String departure_time;
    private int origin_id;
    private int destination_id;
    private int carriage_capacity;
    private int carriage_amount;
    private String carriage_type;
    private int arrival_day;
    private String weekDays;

    public Train(int id, String arrival_time, String departure_time, int carriage_capacity, int carriage_amount,
                 String carriage_type, String weekDays, int destination_id, int origin_id, int arrival_day) {
        this.id = id;
        this.arrival_day = arrival_day;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.origin_id = origin_id;
        this.destination_id = destination_id;
        this.carriage_capacity = carriage_capacity;
        this.carriage_amount = carriage_amount;
        this.carriage_type = carriage_type;
        this.weekDays = weekDays;
    }

    public int getId() {
        return id;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public int getOrigin_id() {
        return origin_id;
    }

    public int getDestination_id() {
        return destination_id;
    }

    public int getCarriage_capacity() {
        return carriage_capacity;
    }

    public int getCarriage_amount() {
        return carriage_amount;
    }

    public String getCarriage_type() {
        return carriage_type;
    }

    public int getArrival_day() {
        return arrival_day;
    }

    public String getWeekDays() {
        return weekDays;
    }

    @Override
    public String toString() {
        return "Train{" +
                "id=" + id +
                ", arrival_time='" + arrival_time + '\'' +
                ", departure_time='" + departure_time + '\'' +
                ", origin_id=" + origin_id +
                ", destination_id=" + destination_id +
                ", carriage_capacity=" + carriage_capacity +
                ", carriage_amount=" + carriage_amount +
                ", carriage_type='" + carriage_type + '\'' +
                ", arrival_day=" + arrival_day +
                ", weekDays='" + weekDays + '\'' +
                '}';
    }
}
