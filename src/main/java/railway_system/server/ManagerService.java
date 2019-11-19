package railway_system.server;

import com.google.gson.Gson;
import railway_system.dao.*;
import railway_system.entity.Employee;
import railway_system.entity.TrainLeg;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Path("/manager")
public class ManagerService {
    @Context
    SecurityContext securityContext;

    public ManagerService(){}

    boolean checkIsManager(SecurityContext securityContext){
        MainDao mainDao = new MainDaoImpl();
        Principal principal = securityContext.getUserPrincipal();
        int user_id = Integer.parseInt(principal.getName());
        if(!mainDao.checkManager(user_id)){
            return false;
        }
        return true;
    }

    @POST
    @Secured
    @Path("/make-payroll")
    public Response makePayroll(@Context SecurityContext securityContext, @FormParam("employee-id") int employeeId){
        if(!checkIsManager(securityContext)){
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
    public Response createTrainLine(@Context SecurityContext securityContext, @FormParam("train-type-id") int type_id,
                                    @FormParam("weekdays") String weekdays, @FormParam("company-name") String company_name){
        if(!checkIsManager(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }

        CrudDao crudDao = new CrudDaoImpl();
        int trainId = crudDao.createTrain(company_name, type_id);
        int weekId = Integer.parseInt(weekdays);
        if(crudDao.createWeekdays(trainId, weekId)){
            String json = "{ \"trainId\": " + trainId + " }";
            return Response.ok(json).build();
        }else{
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Secured
    @Path("/create-train-leg")
    public Response createTrainLeg(@Context SecurityContext securityContext, @FormParam("train-id") int train_id, @FormParam("order") int order,
                                   @FormParam("station_id") int station_id, @FormParam("arrival-time") String arrival_time,
                                   @FormParam("departure_time") String departure_time, @FormParam("arrival_day") int arrival_day){
        if(!checkIsManager(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }

        TrainLeg trainLeg = new TrainLeg(train_id, order, station_id, arrival_time, departure_time, arrival_day);
        CrudDao crudDao = new CrudDaoImpl();
        try {
            crudDao.createTrainLeg(trainLeg);
        } catch (DaoException e) {
            e.printStackTrace();
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(Response.Status.ACCEPTED).build();
    }

    @POST
    @Secured
    @Path("/cancel-train-line")
    public Response cancelTrainLine(@Context SecurityContext securityContext, @FormParam("train-id") int trainId){
        if(!checkIsManager(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }

        CrudDao crudDao = new CrudDaoImpl();
        if(crudDao.updateTrainActivity(trainId, 0)) {
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
//                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
//                message.setSubject("Mail Subject");
//
//                String msg = "This is my first email using JavaMailer";
//
//                MimeBodyPart mimeBodyPart = new MimeBodyPart();
//                mimeBodyPart.setContent(msg, "text/html");
//                Multipart multipart = new MimeMultipart();
//                multipart.addBodyPart(mimeBodyPart);
//                message.setContent(multipart);
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
        if(!checkIsManager(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }

        CrudDao crudDao = new CrudDaoImpl();
        if(crudDao.updateTrainActivity(trainId, 1)) {
            return Response.ok(Response.Status.ACCEPTED).build();
        }else{
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
    }


    @GET
    @Secured
    @Path("/employees")
    public Response getEmployees(@Context SecurityContext securityContext){
        if(!checkIsManager(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        Gson gson = new Gson();
        List<Employee> employees = new CrudDaoImpl().readAllEmployees();
        Employee[] arr =  employees.toArray(new Employee[employees.size()]);
        String json = gson.toJson(arr, Employee[].class);
        return Response.ok(json).build();
    }

}
