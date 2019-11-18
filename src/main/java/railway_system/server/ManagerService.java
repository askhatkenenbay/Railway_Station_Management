package railway_system.server;

import railway_system.dao.CrudDao;
import railway_system.dao.CrudDaoImpl;
import railway_system.dao.MainDao;
import railway_system.dao.MainDaoImpl;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
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
import java.util.Properties;

@Path("/manager")
public class ManagerService {
    @Context
    SecurityContext securityContext;

    public ManagerService(){}

    @POST
    @Secured
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

        if(crudDao.createPaycheck(employeeId, date)) {
            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Secured
    @Path("/create-train-line")
    public Response createTrainLine(@FormParam("train-type-id") int type_id,
                                    @FormParam("weekdays") String weekdays, @FormParam("company-name") String company_name){
        MainDao mainDao = new MainDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if(!mainDao.checkManager(user_id)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }

        CrudDao crudDao = new CrudDaoImpl();
        int trainId = crudDao.createTrainLine(company_name, type_id);
        int weekId = Integer.parseInt(weekdays);
        if(crudDao.createWeekdays(trainId, weekId)){
            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Secured
    @Path("/cancel-train-line")
    public Response cancelTrainLine(@Context SecurityContext securityContext, @FormParam("train-id") int trainId){
        MainDao mainDao = new MainDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if(!mainDao.checkManager(user_id)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }

        CrudDao crudDao = new CrudDaoImpl();
        if(crudDao.cancelTrainLine(trainId)) {
//            String username = "manager@mail.com";
//            String password = "12345678";
//
//            Properties prop = new Properties();
//            prop.put("mail.smtp.auth", true);
//            prop.put("mail.smtp.starttls.enable", "true");
//            prop.put("mail.smtp.host", "smtp.mailtrap.io");
//            prop.put("mail.smtp.port", "25");
//            prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
//            Session session = Session.getInstance(prop, new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication(username, password);
//                }
//                Message message = new MimeMessage(session);
//                message.setFrom(new InternetAddress("from@gmail.com"));
//                message.setRecipients(
//                                Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
//                message.setSubject("Mail Subject");
//
//                                String msg = "This is my first email using JavaMailer";
//
//                                MimeBodyPart mimeBodyPart = new MimeBodyPart();
//                mimeBodyPart.setContent(msg, "text/html");
//
//                                Multipart multipart = new MimeMultipart();
//                multipart.addBodyPart(mimeBodyPart);
//
//                message.setContent(multipart);
//
//                Transport.send(message);
//
//            });

            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Secured
    @Path("/reopen-train-line")
    public Response reopenTrainLine(@Context SecurityContext securityContext, @FormParam("train-id") int trainId){
        MainDao mainDao = new MainDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if(!mainDao.checkManager(user_id)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }

        CrudDao crudDao = new CrudDaoImpl();
        if(crudDao.reopenTrainLine(trainId)) {
            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
    }





}
