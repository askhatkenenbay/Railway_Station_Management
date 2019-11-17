
import railway_system.dao.CrudDaoImpl;
import railway_system.entity.Ticket;
import railway_system.entity.TrainType;

public class main {
    public static void main(String[] args){
        new CrudDaoImpl().readPdfFile();
        System.out.println("done fine");
    }
}
