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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CrudDaoImpl implements CrudDao {
    //private static final String CREATE_TRAIN = "INSERT INTO train values(?,?,?,?)";
    private static final String CREATE_TRAIN_TYPE = "INSERT INTO train_type values (?,?,?,?)";
    private static final String CREATE_STATION = "INSERT INTO station values(?,?,?,?)";
    private static final String CREATE_TRAIN_LEG = "INSERT INTO train_leg values(?,?,?,?,?,?)";
    private static final String CREATE_SEAT = "INSERT INTO seats values(?,?,?,?)";
    private static final String CREATE_INDIVIDUAL = "INSERT INTO individual VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String CREATE_EMPLOYEE = "INSERT INTO employee VALUES(?,?,?,?,?,?,?)";
    private static final String CREATE_TICKET = "INSERT INTO ticket values(?,?,?,?,?,?,?,?,null,?,?,?,?,?)";
    private static final String CREATE_PAYCHECK = "INSERT INTO paycheck values(?,?,?)";
    private static final String CREATE_TRAIN = "INSERT INTO train(company_name, train_type_id, is_active) values (?,?,1)";
    private static final String CREATE_WEEK_DAY = "INSERT INTO week_day values(?,?)";
    private static final String CREATE_SEAT_INSTANCE = "INSERT INTO seat_instance values(?,?,?,?,?,null)";
    private static final String SELECT_WAGON_AMOUNT_CAPACITY = "select wagon_amount,wagon_capacity from train_type,train " +
            "where train.id=? and train.train_type_id=train_type.id;";
    private static final String SELECT_TRAINID_AND_ORDER_BY_TRAINID = "select train_id, `order` from train_leg where train_id=?";
    private static final String SELECT_EMPLOYEES_SALARY = "select salary from employee where id=?";
    private static final String SELECT_MAX_TRAIN_ID = "select MAX(id) as `Max` from train";
    private static final String READ_PDF_FILE = "SELECT pdf_file FROM ticket";
    private static final String READ_STATIONS = "select * from station";
    private static final String READ_STATION_BY_ID = "select * from station where id=?";
    private static final String READ_TICKET_BY_INDIVIDUAL_ID = "select * from ticket where individual_id=?";
    private static final String READ_WAITING_REFUND_TICKET = "select * from ticket where waiting_refund=1";
    private static final String READ_ALL_EMPLOYEE = "select salary,type,work_since,employee.id,individual_id,first_name,second_name\n" +
            "from employee, individual where  employee.id=individual.id";
    private static final String READ_TRAIN_TYPE_BY_ID = "select * from train T, train_type where T.id = ? and train_type.id=T.train_type_id";
    private static final String READ_SEAT_INFO = "select wagon_amount,wagon_capacity,count(train_leg.order) as `order` from train_type,train_leg,train where\n" +
            "train.id = ? and train_type.id = train.train_type_id  and train_leg.train_id=train.id";
    private static final String UPDATE_INDIVIDUAL_LOGIN = "UPDATE individual set remember=? where login=?";
    private static final String UPDATE_TRAIN_ACTIVITY = "UPDATE train SET is_active = ? where id = ?";
    private static final String UPDATE_TICKET_REFUND = "UPDATE ticket set waiting_refund = ? where id = ?";
    private static final String DELETE_TICKET_BY_ID = "delete from ticket where id=?";


    @Override
    public void createTrainType(TrainType trainType) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_TRAIN_TYPE);
            preparedStatement.setInt(1, trainType.getId());
            preparedStatement.setInt(2, trainType.getWagonAmount());
            preparedStatement.setInt(3, trainType.getWagonCapacity());
            preparedStatement.setInt(4, trainType.getPrice());
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
    public void createTrainLeg(TrainLeg trainLeg) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_TRAIN_LEG);

            preparedStatement.setString(1, trainLeg.getArrival_time());
            preparedStatement.setString(2, trainLeg.getDeparture_time());
            preparedStatement.setInt(3, trainLeg.getOrder());
            preparedStatement.setInt(4, trainLeg.getStation_id());
            preparedStatement.setInt(5, trainLeg.getTrain_id());
            preparedStatement.setInt(6, trainLeg.getArrival_day());

            preparedStatement.executeUpdate();
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


//    @Override
//    public void createEmployee(Employee employee) throws DaoException {
//        Connection connection = null;
//        PreparedStatement preparedStatement = null;
//        try {
//            connection = ConnectionPool.INSTANCE.getConnection();
//            preparedStatement = connection.prepareStatement(CREATE_EMPLOYEE);
//            preparedStatement.setInt(1, employee.getPaymentForHour());
//            preparedStatement.setString(2, employee.getType());
//            preparedStatement.setString(3, employee.getWorkStartTime());
//            preparedStatement.setString(4, employee.getWorkEndTime());
//            preparedStatement.setString(5, employee.getWorkDays());
//            preparedStatement.setInt(6, employee.getId());
//            preparedStatement.setInt(7, employee.getIndividualId());
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new DaoException("Cannot create new employee");
//        } finally {
//            close(preparedStatement);
//            close(connection);
//        }
//    }

    @Override
    public void setToken(String username, String token) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_INDIVIDUAL_LOGIN);
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    @Override
    public boolean updateTicketRefund(int ticketId, int waiting_refund) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_TICKET_REFUND);
            preparedStatement.setInt(1, waiting_refund);
            preparedStatement.setInt(2, ticketId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
        return false;
    }

    @Override
    public List<Station> readStations() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Station> result = new LinkedList<>();
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(READ_STATIONS);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(new Station(resultSet.getInt("id"), resultSet.getString("name"),
                        resultSet.getString("city"), resultSet.getString("state")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return result;
    }

    @Override
    public TrainType readTrainType(int trainId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(READ_TRAIN_TYPE_BY_ID);
            preparedStatement.setInt(1,trainId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return new TrainType(resultSet.getInt("id"),resultSet.getInt("wagon_amount"),
                    resultSet.getInt("wagon_capacity"),resultSet.getInt("price"));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return null;
    }

    @Override
    public List<Ticket> readTicketsOfUser(int individual_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(READ_TICKET_BY_INDIVIDUAL_ID);
            preparedStatement.setInt(1, individual_id);
            resultSet = preparedStatement.executeQuery();
            return helperTicket(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return new LinkedList<>();
    }

    @Override
    public List<Ticket> readWaitingTickets() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(READ_WAITING_REFUND_TICKET);
            resultSet = preparedStatement.executeQuery();
            return helperTicket(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return new LinkedList<>();
    }

    private List<Ticket> helperTicket(ResultSet resultSet) throws SQLException {
        LinkedList<Ticket> result = new LinkedList<>();
        while (resultSet.next()) {
            result.add(new Ticket(resultSet.getInt("id"), resultSet.getInt("train_id"),
                    resultSet.getInt("station_id_from"), resultSet.getInt("station_id_to"),
                    resultSet.getInt("individual_id"), resultSet.getString("first_name"),
                    resultSet.getString("second_name"), resultSet.getString("document_type"),
                    resultSet.getString("document_id"), resultSet.getBoolean("waiting_refund"),
                    resultSet.getString("arrival_datetime"), resultSet.getString("departure_datetime"),
                    resultSet.getInt("seat_number"), resultSet.getInt("wagon_number")));
        }
        return result;
    }

    @Override
    public List<Employee> readAllEmployees() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(READ_ALL_EMPLOYEE);
            resultSet = preparedStatement.executeQuery();
            return helperEmployee(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return new LinkedList<>();
    }

    private List<Employee> helperEmployee(ResultSet resultSet) throws SQLException {
        LinkedList<Employee> employees = new LinkedList<>();
        while (resultSet.next()) {
            employees.add(new Employee(resultSet.getInt("salary"), resultSet.getString("type"),
                    resultSet.getString("work_since"), resultSet.getInt("id"),
                    resultSet.getInt("individual_id"), resultSet.getString("first_name"), resultSet.getString("second_name")));
        }
        return employees;
    }

    @Override
    public boolean updateTrainActivity(int trainId, int activity) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_TRAIN_ACTIVITY);
            preparedStatement.setInt(1, activity);
            preparedStatement.setInt(2, trainId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
        return false;
    }

    @Override
    public void deleteTicket(int ticket_id) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(DELETE_TICKET_BY_ID);
            preparedStatement.setInt(1, ticket_id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("problems with deleting ticket");
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    private static final String GET_MAX_TICKET_ID = "Select MAX(id) as id from ticket";
    @Override
    public int createTicket(Ticket ticket) {
        int id = -1;
        PreparedStatement preparedStatementId = null;
        ResultSet resultSet = null;
        try (Connection connection = ConnectionPool.INSTANCE.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TICKET)) {
//            createPdfFile(ticket);
            //preparedStatement.setInt(1, ticket.getTicketId());
            preparedStatement.setInt(1, ticket.getTrainId());
            preparedStatement.setInt(2, ticket.getStationIdFrom());
            preparedStatement.setInt(3, ticket.getStationIdTo());
            preparedStatement.setInt(4, ticket.getIndividualId());
            preparedStatement.setString(5, ticket.getFirstName());
            preparedStatement.setString(6, ticket.getSecondName());
            preparedStatement.setString(7, ticket.getDocumentType());
            preparedStatement.setString(8, ticket.getDocumentId());
            preparedStatement.setBoolean(9,ticket.isWaitingRefund());
            preparedStatement.setString(10,ticket.getArrivalDatetime());
            preparedStatement.setString(11,ticket.getDepartureDatetime());
            preparedStatement.setInt(12,ticket.getSeatNumber());
            preparedStatement.setInt(13,ticket.getWagonNumber());
            preparedStatement.executeUpdate();
            preparedStatementId = connection.prepareStatement(GET_MAX_TICKET_ID);
            resultSet = preparedStatementId.executeQuery();
            resultSet.next();
            id = resultSet.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatementId);
        }
        return id;
    }

    @Override
    public boolean createPaycheck(int employeeId, String date) {
        Connection connection = null;
        PreparedStatement preparedStatementSalary = null;
        PreparedStatement preparedStatementPaycheck = null;
        ResultSet resultSetSalary = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatementSalary = connection.prepareStatement(SELECT_EMPLOYEES_SALARY);
            preparedStatementSalary.setInt(1, employeeId);
            resultSetSalary = preparedStatementSalary.executeQuery();
            resultSetSalary.next();
            preparedStatementPaycheck = connection.prepareStatement(CREATE_PAYCHECK);
            preparedStatementPaycheck.setInt(1, employeeId);
            preparedStatementPaycheck.setString(2, date);
            preparedStatementPaycheck.setInt(3, resultSetSalary.getInt("salary"));
            preparedStatementPaycheck.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (resultSetSalary != null) {
                    resultSetSalary.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatementSalary);
            close(preparedStatementPaycheck);
            close(connection);
        }
        return true;
    }

    @Override
    public int createTrain(String companyName, int typeId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatementTrainId = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_TRAIN);
            preparedStatement.setString(1, companyName);
            preparedStatement.setInt(2, typeId);
            preparedStatement.executeUpdate();
            preparedStatementTrainId = connection.prepareStatement(SELECT_MAX_TRAIN_ID);
            resultSet = preparedStatementTrainId.executeQuery();
            resultSet.next();
            return resultSet.getInt("Max");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(preparedStatementTrainId);
            close(connection);
        }
        return -1;
    }

    @Override
    public boolean createWeekdays(int trainId, int weekId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(CREATE_WEEK_DAY);
            preparedStatement.setInt(1, weekId);
            preparedStatement.setInt(2, trainId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(preparedStatement);
            close(connection);
        }
        return false;
    }

    @Override
    public void createSeatInstances(int trainId, String date) throws DaoException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement preparedStatementHelper = null;
        ResultSet resultSet = null;
        try{
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatementHelper = connection.prepareStatement(READ_SEAT_INFO);
            preparedStatementHelper.setInt(1,trainId);
            resultSet = preparedStatementHelper.executeQuery();
            resultSet.next();
            int wagonNumber = resultSet.getInt("wagon_amount");
            int wagonCapacity = resultSet.getInt("wagon_capacity");
            int order = resultSet.getInt("order");
            //-------------------------------------------------------------
            preparedStatement = connection.prepareStatement(CREATE_SEAT_INSTANCE);
            preparedStatement.setString(1,date);
            preparedStatement.setInt(5,trainId);
            for (int i = 1; i <=order; i++) {
                for (int j = 1; j <=wagonNumber; j++) {
                    for (int k = 1; k <=wagonCapacity; k++) {
                        preparedStatement.setInt(2,k);
                        preparedStatement.setInt(3,j);
                        preparedStatement.setInt(4,i);
                        preparedStatement.execute();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Already Created");
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(preparedStatementHelper);
            close(connection);
        }
    }

    @Override
    public Station getStation(int id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionPool.INSTANCE.getConnection();
            preparedStatement = connection.prepareStatement(READ_STATION_BY_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Station(resultSet.getInt("id"), resultSet.getString("name"),
                        resultSet.getString("city"), resultSet.getString("state"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(preparedStatement);
            close(connection);
        }
        return null;
    }


    public void readPdfFile() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
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
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
