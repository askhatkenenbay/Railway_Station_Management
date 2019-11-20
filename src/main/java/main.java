
import railway_system.dao.CrudDaoImpl;
import railway_system.entity.Ticket;
import railway_system.entity.TrainType;
import railway_system.server.Encryptor;

public class main {
    public static void main(String[] args){
//        new CrudDaoImpl().readPdfFile();
//        System.out.println("done fine");
        System.out.println(Encryptor.encrypInput("qwerty"));
    }
}
