package railway_system.server;

import com.google.gson.Gson;
import railway_system.dao.*;
import railway_system.entity.Employee;
import railway_system.entity.Individual;
import railway_system.entity.TrainLeg;
import railway_system.filters.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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

    static Properties mailServerProperties;
    static Session getMailSession;
    static MimeMessage generateMailMessage;

    public static void sendEmail(int trainId) throws AddressException, MessagingException {
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("nurtas.ilyas@nu.edu.kz"));
        generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("askhat.kenenbay@nu.edu.kz"));
        generateMailMessage.setSubject("Trail Line is canceled");
        String emailBody = "We are sorry, but train line " + String.valueOf(trainId) +" is unavailable until further notice " + "<br><br> Regards, <br>InElonWeTrust Team";
        generateMailMessage.setContent(emailBody, "text/html");

        Transport transport = getMailSession.getTransport("smtp");

        transport.connect("smtp.gmail.com", "kim.denis.998@gmail.com", "manager.001");
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();
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
        for(int i = 0 ; i < weekdays.length(); i++){
            int weekId = weekdays.charAt(i) - 48;
            if(!crudDao.createWeekdays(trainId, weekId)) {
                return Response.ok(Response.Status.FORBIDDEN).build();
            }
        }
        String json = "{ \"trainId\": " + trainId + " }";
        return Response.ok(json).build();
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

    @POST
    @Secured
    @Path("/send-notification")
    public Response sendMail(@Context SecurityContext securityContext, @FormParam("train-id") int trainId){
        if(!checkIsManager(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        List<String> agentsEmails = new MainDaoImpl().getAgentsEmails();
        try {
            sendEmail(trainId);
        } catch (MessagingException e) {
            e.printStackTrace();
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(Response.Status.ACCEPTED).build();

    }

}
