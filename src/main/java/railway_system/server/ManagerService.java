package railway_system.server;

import com.google.gson.Gson;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import railway_system.dao.*;
import railway_system.entity.Employee;
import railway_system.entity.Individual;
import railway_system.entity.TrainLeg;
import railway_system.filters.Logged;
import railway_system.filters.Secured;

import javax.print.DocFlavor;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.*;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Logged
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
    @Logged
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
    @Logged
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
    @Logged
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
    @Logged
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

    @GET
    @Secured
    @Path("/logs")
    public Response getLogs(@Context SecurityContext securityContext) {
        if (!checkIsManager(securityContext)) {
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        File file = new File("D:\\Projects\\IdeaProjects\\RailwaySystem\\Railway_System_Management\\log\\logging.log");
        List<String> lines = new LinkedList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            for(String tmp; (tmp = br.readLine()) != null;) {
                if (tmp.length() < 1) {
                    continue;
                }
                if (lines.add(tmp) && lines.size() > 20)
                    lines.remove(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        StringBuilder sb = new StringBuilder();
//        for(String line : lines){
//            sb.append(line).append("\n");
//        }
        Gson gson = new Gson();
        String[] arr = lines.toArray(new String[0]);
        String json = gson.toJson(arr, String[].class);
//        String json = "{ \"logs\": \"" + sb.toString() + "\" }";
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

    @POST
    @Secured
    @Path("/logs-status")
    public Response changeLogStatus(@Context SecurityContext securityContext, @FormParam("status") boolean status) {
        if(!checkIsManager(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        if(status){
            changeLogging(Level.INFO);
        }
        else{
            changeLogging(Level.OFF);
        }
        return Response.ok().build();
    }

    @GET
    @Secured
    @Path("/logs-status")
    public Response getLogStatus(@Context SecurityContext securityContext) {
        if(!checkIsManager(securityContext)){
            return Response.ok(Response.Status.FORBIDDEN).build();
        }
        Level level = Level.OFF;
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for ( Logger logger : loggers ) {
            level = logger.getLevel();
        }
        String json = "{\"status\": " + (level == Level.INFO)+"}";
        System.out.println(json);
        return Response.ok(json).build();
    }

    private void changeLogging(Level level){
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for ( Logger logger : loggers ) {
            logger.setLevel(level);
        }
    }
}
