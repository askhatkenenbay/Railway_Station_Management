package railway_system.server;

import org.glassfish.json.JsonUtil;
import org.w3c.dom.ls.LSOutput;
import railway_system.dao.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.Base64;

@Path("/login")
public class AuthorizationService {
    public AuthorizationService(){}

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password){
        try{
            authenticate(username, password);

            String token = issueToken(username);
            String json = "{ \"token\": \"" + token + "\" }";
            return Response.ok(json).build();
        }catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }


    private void authenticate(String username, String password) throws Exception{
        MainDao mainDao = new MainDaoImpl();
        if(mainDao.authenticated(username, Encryptor.encrypInput(password))){
            throw new Exception("not authenticated");
        }
    }

    private String issueToken(String username){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        CrudDao crudDao = new CrudDaoImpl();
        String token = encoder.encodeToString(bytes);
        crudDao.setToken(username, token);
        return token;
    }


}
