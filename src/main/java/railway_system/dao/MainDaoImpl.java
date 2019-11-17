package railway_system.dao;

import javafx.util.Pair;
import railway_system.entity.Seat;
import railway_system.entity.Station;
import railway_system.entity.TrainLeg;

import java.util.ArrayList;

public class MainDaoImpl implements MainDao {
    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsFromTo(int weekDay, int from_id, int to_id) {
        return null;
    }

    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsFrom(int weekDay, int from_id) {
        return null;
    }

    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrainsTo(int weekDay, int to_id) {
        return null;
    }

    @Override
    public ArrayList<Pair<TrainLeg, TrainLeg>> getTrains(int weekDay) {
        return null;
    }

    @Override
    public Station getStation(int id) {
        return null;
    }

    @Override
    public ArrayList<Seat> getSeats(String date, int train_id, int fromOrder, int toOrder) {
        return null;
    }

    @Override
    public Boolean refundTicket(int user_id, int train_id, int ticketId) {
        return null;
    }

    @Override
    public boolean checkIfAvailable(String date, int seat_number, int wagon_number, int from_order, int to_order, int train_id) {
        return false;
    }

    @Override
    public void updateSeatInstances(String date, int seat_num, int wagon_num, int from_order, int to_order, int train_id, int ticket_id) {

    }

    @Override
    public boolean authenticated(String username, String password) {
        return false;
    }
}
