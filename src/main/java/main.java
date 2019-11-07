
import railway_system.dao.CrudDaoImpl;

public class main {
    public static void main(String[] args){
        new CrudDaoImpl().createPdfFile(123456789,"Askhat","Kenenbay","stateId","N37835641");
        System.out.println("done fine");
    }
}
