package railway_system.entity;

public class Ticket {
    private int train_id;
    private int place;
    private int carriage_number;
    private double price;
    private String seat_type;
    private  String date;
    private boolean isAvailable;

    public Ticket (int train_id, int place, int carriage_number, double price, String seat_type, String date, boolean isAvailable){
        this.train_id = train_id;
        this.place = place;
        this.carriage_number = carriage_number;
        this.price = price;
        this.seat_type = seat_type;
        this.date = date;
        this.isAvailable = isAvailable;
    }
}
