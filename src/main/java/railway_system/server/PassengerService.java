package railway_system.server;

import railway_system.dao.BaseDao;
import railway_system.dao.BaseDaoImpl;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/passenger")
public class PassengerService {
    public PassengerService(){}

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response registerPassenger(@FormParam("fname") String fname, @FormParam("lname") String lname,
                                      @FormParam("email") String email, @FormParam("login") String login,
                                      @FormParam("password") String password){
        password = Encryptor.encrypInput(password);

        BaseDao baseDao = new BaseDaoImpl();
        if(baseDao.registerIndividual(fname, lname, email, login, password)){
            return Response.ok().build();
        }else{
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
