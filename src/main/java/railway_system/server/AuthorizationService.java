package railway_system.server;

import org.glassfish.json.JsonUtil;
import org.w3c.dom.ls.LSOutput;
import railway_system.dao.BaseDao;
import railway_system.dao.BaseDaoImpl;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password){
        try{
            authenticate(username, password);

            String token = issueToken(username);

            return Response.ok(token).build();
        }catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
    private void authenticate(String username, String password) throws Exception{
        BaseDao baseDao = new BaseDaoImpl();
        if(!baseDao.authenticated(username, Encryptor.encrypInput(password))){
            throw new Exception("not authenticated");
        }
    }

    private String issueToken(String username){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        BaseDao baseDao = new BaseDaoImpl();
        String token = encoder.encodeToString(bytes);
        baseDao.setToken(username, token);
        return token;
    }


}
