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

    public Train(int id, String arrival_time, String departure_time, int origin_id, int destination_id, int carriage_capacity, int carriage_amount, String carriage_type, int arrival_day){
        this.id = id;
        this.arrival_day = arrival_day;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.origin_id = origin_id;
        this.destination_id = destination_id;
        this.carriage_capacity = carriage_capacity;
        this.carriage_amount = carriage_amount;
        this.carriage_type = carriage_type;
    }
}
