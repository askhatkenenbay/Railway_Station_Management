package railway_system.dao;


import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.layout.Document;

import com.itextpdf.layout.element.*;


import railway_system.connection.ConnectionPool;
import railway_system.entity.*;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CrudDaoImpl implements CrudDao {
    private static final String CREATE_TRAIN = "INSERT INTO train values(?,?,?,?)";
    private static final String CREATE_TRAIN_TYPE = "INSERT INTO train_type values (?,?,?,?,?)";
    private static final String CREATE_STATION = "INSERT INTO station values(?,?,?,?)";
    private static final String CREATE_TRAIN_LEG = "INSERT INTO train_leg values(?,?,?,?,?)";
    private static final String CREATE_SEAT = "INSERT INTO seats values(?,?,?,?)";
    private static final String CREATE_INDIVIDUAL = "INSERT INTO individual VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String CREATE_EMPLOYEE = "INSERT INTO employee VALUES(?,?,?,?,?,?,?)";
    private static final String CREATE_TICKET = "INSERT INTO ticket VALUES(?,?,?,?,?,?,?,?,?,?)";
    private static final String SELECT_WAGON_AMOUNT_CAPACITY = "select wagon_amount,wagon_capacity from train_type,train where train.id=? and train.train_type_id=train_type.id;";
    private static final String SELECT_TRAINID_AND_ORDER_BY_TRAINID = "select train_id, `order` from train_leg where train_id=?";
    private static final String READ_PDF_FILE = "SELECT pdf_file FROM ticket";
    @Override
    public void createTrainType(TrainType trainType) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_TRAIN_TYPE);
            preparedStatement.setInt(1, trainType.getId());
            preparedStatement.setString(2, trainType.getName());
            preparedStatement.setInt(3, trainType.getWagonAmount());
            preparedStatement.setInt(4, trainType.getWagonCapacity());
            preparedStatement.setInt(5, trainType.getPrice());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Cannot create new train type");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void createStation(Station station) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_STATION);
            preparedStatement.setInt(1, station.getId());
            preparedStatement.setString(2, station.getName());
            preparedStatement.setString(3, station.getCity());
            preparedStatement.setString(4, station.getState());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("cannot create new station");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void createTrain(Train train) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_TRAIN);
            preparedStatement.setInt(1, train.getId());
            preparedStatement.setString(2, train.getCompanyName());
            preparedStatement.setString(3, train.getWeekDays());
            preparedStatement.setInt(4, train.getTrainTypeId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("cannot create new train");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void createTrainLeg(int trainId, List<Integer> listOfStationId, List<String> arriveTime, List<String> departureTime) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_TRAIN_LEG);
            preparedStatement.setInt(5, trainId);
            for (int i = 0; i < listOfStationId.size(); i++) {
                preparedStatement.setString(1, arriveTime.get(i));
                preparedStatement.setString(2, departureTime.get(i));
                preparedStatement.setInt(3, i);
                preparedStatement.setInt(4, listOfStationId.get(i));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("cannot create new train leg");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void createSeats(int traindId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement statement = null;
        PreparedStatement tempStatement = null;
        ResultSet resultSet = null;
        ResultSet tempResultSet = null;
        int wagonAmount;
        int wagonCapacity;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            statement = connection.prepareStatement(SELECT_WAGON_AMOUNT_CAPACITY);
            statement.setInt(1, traindId);
            resultSet = statement.executeQuery();
            resultSet.next();
            wagonAmount = resultSet.getInt("wagon_amount");
            wagonCapacity = resultSet.getInt("wagon_capacity");
            tempStatement = connection.prepareStatement(SELECT_TRAINID_AND_ORDER_BY_TRAINID);
            tempStatement.setInt(1, traindId);
            tempResultSet = tempStatement.executeQuery();
            preparedStatement = connection.prepareStatement(CREATE_SEAT);
            while (tempResultSet.next()) {
                for (int i = 0; i < wagonAmount; i++) {
                    for (int j = 0; j < wagonCapacity; j++) {
                        preparedStatement.setInt(1, j + 1);
                        preparedStatement.setInt(2, i + 1);
                        preparedStatement.setInt(3, tempResultSet.getInt("order"));
                        preparedStatement.setInt(4, tempResultSet.getInt("train_id"));
                        preparedStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (tempResultSet != null) {
                    tempResultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(tempStatement);
            close(statement);
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public void createIndividual(Individual individual) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_INDIVIDUAL);
            preparedStatement.setString(1, individual.getFirstName());
            preparedStatement.setString(2, individual.getSecondName());
            preparedStatement.setString(3, individual.getEmail());
            preparedStatement.setString(4, individual.getLogin());
            preparedStatement.setString(5, individual.getPassword());
            preparedStatement.setString(6, individual.getActivation());
            preparedStatement.setString(7, individual.getRemember());
            preparedStatement.setString(8, individual.getReset());
            preparedStatement.setInt(9, individual.getActivated());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Cannot create new individual");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }


    @Override
    public void createEmployee(Employee employee) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_EMPLOYEE);
            preparedStatement.setInt(1, employee.getPaymentForHour());
            preparedStatement.setString(2, employee.getType());
            preparedStatement.setString(3, employee.getWorkStartTime());
            preparedStatement.setString(4, employee.getWorkEndTime());
            preparedStatement.setString(5, employee.getWorkDays());
            preparedStatement.setInt(6, employee.getId());
            preparedStatement.setInt(7, employee.getIndividualId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Cannot create new employee");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }


    @Override
    public void createTicket(Ticket ticket) {
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TICKET)) {
            createPdfFile(ticket);
            preparedStatement.setInt(1,ticket.getTicketId());
            preparedStatement.setInt(2,ticket.getTrainId());
            preparedStatement.setInt(3,ticket.getStationIdFrom());
            preparedStatement.setInt(4,ticket.getStationIdTo());
            preparedStatement.setInt(5,ticket.getIndividualId());
            preparedStatement.setString(6,ticket.getFirstName());
            preparedStatement.setString(7,ticket.getSecondName());
            preparedStatement.setString(8,ticket.getDocumentType());
            preparedStatement.setString(9,ticket.getDocumentId());
            preparedStatement.setBytes(10, Files.readAllBytes(Paths.get("C:/Users/Askhat/Desktop/Railway_Station_Management/ticket.pdf")));
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void readPdfFile(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(READ_PDF_FILE);
            resultSet = preparedStatement.executeQuery();
            File file = new File("ticketRead.pdf");
            FileOutputStream output = new FileOutputStream(file);
            resultSet.next();
            InputStream input = resultSet.getBinaryStream("pdf_file");
            byte[] buffer = new byte[1024];
            while (input.read(buffer) > 0) {
                output.write(buffer);
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            close(preparedStatement);
            close(connection);
        }
    }

    public void createPdfFile(Ticket ticket) {
        String destination = "C:/Users/Askhat/Desktop/Railway_Station_Management/ticket.pdf";
        try {
            PdfWriter writer = new PdfWriter(destination);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            //adding image
            String imFile = "C:/Users/Askhat/Desktop/Railway_Station_Management/logo.png";
            ImageData data = ImageDataFactory.create(imFile);
            Image image = new Image(data);
            image.setFixedPosition(25, 725);
            image.setWidth(100);
            image.setHeight(100);
            document.add(image);
            //adding company name
            Text companyName = new Text("The Train Station Inc.");
            companyName.setFont(PdfFontFactory.createFont(FontConstants.COURIER_BOLD));
            companyName.setFontColor(Color.ORANGE);
            Paragraph paragraph = new Paragraph();
            paragraph.add(companyName);
            paragraph.setFixedPosition(150, 775, 200);
            document.add(paragraph);
            //adding ticket id
            Text orderNumber = new Text("Order Number: " + ticket.getTicketId());
            orderNumber.setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD));
            orderNumber.setFontColor(Color.BLACK);
            paragraph = new Paragraph();
            paragraph.add(orderNumber);
            paragraph.setFixedPosition(350, 775, 200);
            document.add(paragraph);
            //adding table one
            Table table = new Table(new float[]{250, 250});
            table.addCell(new Cell().add("Electronic Travel Document\n" + java.util.Calendar.getInstance().getTime()));
            table.addCell(new Cell().add(ticket.getFirstName() + " " + ticket.getSecondName() + "\n"
                    + ticket.getDocumentType() + " " + ticket.getDocumentId()));
            table.addCell(new Cell().add("Departure Time: \tDeparture Station Name: \nDeparture Time: \tDeparture Station City: "));
            table.addCell(new Cell().add("Arrival Time: \tArrival Station Name: \nArrival Time: \tArrival Station City: "));
            table.setFixedPosition(25, 600, 500);
            document.add(table);
            //add train image
            imFile = "C:/Users/Askhat/Desktop/Railway_Station_Management/train.png";
            data = ImageDataFactory.create(imFile);
            image = new Image(data);
            image.setFixedPosition(5, 20);
            image.setWidth(575);
            image.setHeight(150);
            document.add(image);
            //-------------------------
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
