package railway_system.server;

import railway_system.dao.CrudDao;
import railway_system.dao.CrudDaoImpl;
import railway_system.dao.MainDao;
import railway_system.dao.MainDaoImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/manager")
public class ManagerService {
    public ManagerService(){}

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/make-payroll")
    public Response makePayroll(@Context SecurityContext securityContext, @FormParam("employee-id") int employeeId){
        MainDao mainDao = new MainDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if(!mainDao.checkManager(user_id)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }

        CrudDao crudDao = new CrudDaoImpl();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        crudDao.createPaycheck(employeeId, date);
        return Response.ok(Response.Status.ACCEPTED).build();
    }

    @POST
    @Secured
    @Path("/cancel-route")
    public Response cancelRoute(@Context SecurityContext securityContext, @FormParam("train-id") int trainId){
        MainDao mainDao = new MainDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if(!mainDao.checkManager(user_id)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        CrudDao crudDao = new CrudDaoImpl();
        crudDao.cancelRoute(trainId);
        return Response.ok(Response.Status.ACCEPTED).build();
    }

}
