import railway_system.dao.BaseDaoImpl;
import railway_system.dao.CrudDaoImpl;
import railway_system.dao.DaoException;
import railway_system.entity.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class main {
    public static void main(String[] args){
        Employee employee = new Employee(2000,"manager","08:00:00","18:00:00","MWF",
                444,25);
        try {
            new CrudDaoImpl().createEmployee(employee);
        } catch (DaoException e) {
            e.printStackTrace();
        }
        System.out.println("done fine");
    }
}
