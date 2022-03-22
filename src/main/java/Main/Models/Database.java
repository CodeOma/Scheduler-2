package Main.Models;
/**
 * Database Class
 * This class is the connection between the UI and Database.
 * All fetches to the database can be found here.
 */

import Main.Utilities.DbConnection;
import Main.Utilities.Helpers.Formatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static ArrayList<Customer> CustomerList = new ArrayList<>();
    private static ArrayList<Appointment> AppointmentList = new ArrayList<>();
    DbConnection db = new DbConnection();
    Connection connection = db.connection();
    Formatter format = new Formatter();



    public void fetch(String inquiry){
        try{
            Statement statement = connection.createStatement();
            ResultSet queryOutput = statement.executeQuery(inquiry);
            while(queryOutput.next()){
                System.out.println(queryOutput.getString("User_name"));
            }
        }
        catch(Exception e){
            e.printStackTrace();

        }
    }

    /** Login */
    public int login(String userName, String password){
        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT *  FROM Users WHERE user_Name ='" + userName + "'";

            ResultSet queryOutput = statement.executeQuery(inquiry);
            while (queryOutput.next()) {
                String passwords  = queryOutput.getString("password");
                if (queryOutput.getString("password").equals(password)) {
//                    SessionLog.setUserTimeZone();
                    return queryOutput.getInt("User_ID");
                } else {
                    return 0;
                }
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return 0;
    }


    /** Add Customer */
    public String addCustomer(Customer customer){
        try {
            int newAppointmentID = -1;
            //Insert new address into DB
            PreparedStatement pStatement = connection.prepareStatement("INSERT INTO customers (Customer_ID,  Customer_Name, address," +
                    "Phone, Division_ID, Postal_Code, Created_By, Create_Date, Last_Update, Last_Updated_BY) "
                    + "VALUES (?,  ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?)", Statement.RETURN_GENERATED_KEYS);

            pStatement.setInt(1, customer.getId());
            pStatement.setString(2, customer.getCustomerName());
            pStatement.setString(3, customer.getAddress());
            pStatement.setString(4, customer.getPhoneNumber());
            pStatement.setInt(5, customer.getDivisionId());
            pStatement.setString(6, customer.getPostalCode());
            pStatement.setInt(7, SessionLog.getLoggedOnUser());
            pStatement.setInt(8, SessionLog.getLoggedOnUser());

            int inquiry = pStatement.executeUpdate();
            System.out.println(inquiry);
            ResultSet output = pStatement.getGeneratedKeys();
            System.out.println(output);
            return "Saved Successfully";



        } catch(Exception e){
            System.out.println(e.getMessage());
            return "Error when saving";
        }

    }

    /** Edit customer */
    public String editCustomer(Customer customer){
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE customers SET Customer_Name=?, address= ?," +
                    "Phone=?, Division_ID=?, Postal_Code=?," +
                    "Last_Updated_By =?, last_Update=NOW() WHERE Customer_ID = ?", Statement.RETURN_GENERATED_KEYS);

            pStatement.setString(1, customer.getCustomerName());
            pStatement.setString(2, customer.getAddress());
            pStatement.setString(3, customer.getPhoneNumber());
            pStatement.setInt(4, customer.getDivisionId());
            /** Get logged on user to set last updated by*/
            pStatement.setString(5, customer.getPostalCode());
            pStatement.setInt(6, SessionLog.getLoggedOnUser());
            pStatement.setInt(7, customer.getId());

            int inquiry = pStatement.executeUpdate();
            System.out.println(pStatement);
            System.out.println(inquiry);
            ResultSet output = pStatement.getGeneratedKeys();
            System.out.println(output);
            return "Saved Successfully";

        } catch(Exception e){
            System.out.println(e.getMessage());
            return "Error when saving";
        }

    }

    /** Remove Customer */
    public static void removeCustomer(Customer customer){
        CustomerList.remove(customer);
        System.out.println(CustomerList);
    }


    /** Add appointment to DB */
    public String addAppointment(Appointment appointment){
        Integer userId = SessionLog.getLoggedOnUser();
        try {
            PreparedStatement pStatement = connection.prepareStatement("INSERT INTO Appointments (User_ID,Title,Description,Type,Location," +
                    "Customer_ID,Start,End, Contact_ID,  Create_Date, Last_Update, Last_Updated_BY) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, NOW(), NOW(), ?)", Statement.RETURN_GENERATED_KEYS);
            pStatement.setInt(1, userId);
            pStatement.setString(2, appointment.getTitle());
            pStatement.setString(3, appointment.getDescription());
            pStatement.setString(4, appointment.getType());
            pStatement.setString(5, appointment.getLocation());
            pStatement.setInt(6, appointment.getCustomerID());
            pStatement.setTimestamp(7, appointment.getStartTime());
            pStatement.setTimestamp(8, appointment.getEndTime());
            pStatement.setInt(9, appointment.getContactID());
            pStatement.setInt(10, userId);


            int inquiry = pStatement.executeUpdate();
            ResultSet output = pStatement.getGeneratedKeys();
            return "Saved Successfully";
        } catch(Exception e){
            System.out.println(e.getMessage());
            return "Error when saving";
        }

    }

    /** Edit appointment */
    public String editAppointment(Appointment appointment){
//        CURRENT_TIMESTAMP
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE Appointments SET  Title=? , Description=? , Type=? , Location=?," +
                    "Customer_ID=?, Contact_ID=?, Start=?,End=?, Last_Updated_By=?, Last_Update = NOW() WHERE Appointment_ID = ?", Statement.RETURN_GENERATED_KEYS);
            pStatement.setString(1, appointment.getTitle());
            pStatement.setString(2, appointment.getDescription());
            pStatement.setString(3, appointment.getType());
            pStatement.setString(4, appointment.getLocation());
            pStatement.setInt(5, appointment.getCustomerID());
            pStatement.setInt(6, appointment.getContactID());
            pStatement.setTimestamp(7, appointment.getStartTime());
            pStatement.setTimestamp(8, appointment.getEndTime());
            pStatement.setInt(9, SessionLog.getLoggedOnUser());
            pStatement.setInt(10, appointment.getAppointmentId());

            int inquiry = pStatement.executeUpdate();
            ResultSet output = pStatement.getGeneratedKeys();
            return "Saved Successfully";
        } catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("error");

            e.printStackTrace();
            return "Error when saving";
        }

    }

    /** Get Customers */
    public  ObservableList<Customer> getCustomers(){
        Connection connection = db.connection();
        ObservableList<Customer> userCustomerList = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
//            String inquiry = "SELECT *  FROM customers WHERE User_ID ='" + User_ID + "'";
            String inquiry = "SELECT *  FROM customers";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                Customer customer = new Customer(
                        output.getInt("Customer_ID"),
                        output.getString("Customer_Name"),
                        output.getString("Address"),
                        output.getString("Postal_Code"),
                        output.getString("Phone"),
                        getCountry(output.getInt("Division_ID")).getCountry(),
                        output.getInt("Division_ID"));
                System.out.println(customer.getId());
                userCustomerList.add(customer);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return userCustomerList;
    }

    /** Get Divisions */
    public  ObservableList<Divisions> getDivisions(){
        ObservableList<Divisions> divList = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT *  FROM first_level_divisions";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                Divisions division = new Divisions(
                        output.getInt("Division_ID"),
                        output.getString("Division_Namee"),
                        output.getInt("Country_ID")
                );
                divList.add(division);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return divList;
    }

    /** Get Customers IDs */
    public  ObservableList<String> getCustomersIds(){
        Connection connection = db.connection();
        ObservableList<String> userCustomerList = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
//            String inquiry = "SELECT * FROM customer WHERE User_ID ='" + User_ID + "'";
            String inquiry = "SELECT * FROM customers";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                String name = output.getInt("Customer_ID") + " " + output.getString("Customer_Name");
                userCustomerList.add(name);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

        return userCustomerList;
    }

    /** Get contact IDs */
    public  ObservableList<String> getContactIds(){
        ObservableList<String> contactList = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT * FROM contacts";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                String name = output.getInt("Contact_ID") + " " + output.getString("Contact_Name");
                contactList.add(name);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }

        return contactList;
    }

    /** Get Type count per customer */
    public  class TypeCount{
        private String type = "";
        private int count = 0;
        public TypeCount(String type,int count ){
            this.type = type;
            this.count = count;
        }
        public String getType(){
            return type;
        }
        public int getCount(){
            return count;
        }

    }

    /** Get Types by month*/
    public  ObservableList<TypeCount> getTypesByMonth(String month, String year){


        ObservableList<TypeCount> typesList = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            PreparedStatement pStatement = connection.prepareStatement("SELECT type, COUNT(DISTINCT type) as count FROM Appointments WHERE Start>=? AND Start<? Group BY type");
           String start = year + "-" + month + "-01 00:00:00";
            String end;
            int yr = Integer.parseInt(year);
            int yrPlusOne = yr + 1;
            if(month.equals("12")){
              end = yrPlusOne + "-01-01 00:00:00";
           }else{
                end = year + "-" + (Integer.parseInt(month)+1) + "-01 00:00:00";           }
            pStatement.setString(1,start);
            pStatement.setString(2,end);
            ResultSet output = pStatement.executeQuery();
            while (output.next()) {
                String type = output.getString("type");
                int count = output.getInt("count");
                System.out.println(type);
                System.out.println(count);
                typesList.add(new TypeCount(type,count));
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return typesList;
    }

    /** Get coutnry */
    public Countries getCountry(int id){
        Connection connection = db.connection();
        Countries country = null;
        try {
            Statement statement = connection.createStatement();
            System.out.println(id);

            String divInquiry = "SELECT Country_ID FROM first_level_divisions Where Division_ID ='"+ id +"'";

            ResultSet output = statement.executeQuery(divInquiry);

            while(output.next()){
                country = new Countries(
                        output.getInt("Country_ID"));
            }

        } catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        return country;

    }

    /** Get user Ids */
    public  ObservableList<String> getUsersIds(){
        Connection connection = db.connection();
        ObservableList<String> usersList = FXCollections.observableArrayList();
        usersList.add("All");

        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT User_ID,user_Name FROM Users";

            ResultSet output = statement.executeQuery(inquiry);
            System.out.println(output);
            while (output.next()) {
                String name = output.getInt("User_ID") + " " + output.getString("user_Name");
                usersList.add(name);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return usersList;
    }

    /** Get Division IDS */
    public  ObservableList<String> getDivisionIds(){
        Connection connection = db.connection();
        ObservableList<String> divList = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT Division_ID,Division FROM first_level_divisions";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                String division = output.getInt("Division_ID") + " " +
                        output.getString("Division");
                divList.add(division);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return divList;
    }

    /** Fetch customer names*/
    public String getCustomerName(int ID){
        Connection connection = db.connection();
        ObservableList<Appointment> userAppointmentList = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT Customer_Name FROM Customers WHERE Customer_ID ='" + ID + "'";
            ResultSet output = statement.executeQuery(inquiry);
           String  name = output.getString("Customer_Name");
             return name;
            }catch(Exception e){
            return "Null";
        }
        }

        /** Fetch types */
    public  ObservableList<String> getTypes(){
        Connection connection = db.connection();
        ObservableList<String> typesList = FXCollections.observableArrayList();
        typesList.add("All");
        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT DISTINCT type FROM Appointments";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                String type = output.getString("type");
                typesList.add(type);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return typesList;
    }

    /**Remove customer*/
    public static void removeCustomer(Appointment appointment){
        AppointmentList.remove(appointment);
        System.out.println(AppointmentList);
    }


    /** Get appointments */
    public  ObservableList<Appointment> getAppointments(){
        Connection connection = db.connection();
        ObservableList<Appointment> userAppointmentList = FXCollections.observableArrayList();

        try {

            Statement statement = connection.createStatement();
            String inquiry = "SELECT *  FROM Appointments";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                System.out.println(output.getInt("Contact_ID"));
                Appointment appointment = new Appointment(
                        output.getInt("Appointment_ID"),
                        output.getString("Title"),
                        output.getString("Description"),
                        output.getString("Location"),
                        output.getString("Type"),
                        format.sqlToLocalTimestamp(output.getTimestamp("Start")),
                        format.sqlToLocalTimestamp(output.getTimestamp("End")),
                        output.getInt("Customer_ID"),
                        output.getInt("User_ID"),
                        output.getInt("Contact_ID")
                );
                userAppointmentList.add(appointment);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return userAppointmentList;
    }

    /** Check if there is an appointment overlap for a customer */
    public  Boolean checkAppointmentOverLap(int customerId, Timestamp start, Timestamp end){
        Connection connection = db.connection();
        ObservableList<Appointment> userAppointmentList = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT *  FROM Appointments WHERE (Start BETWEEN '"+ start +
                    "' AND '" + end + "') AND Customer_ID =" + customerId + "";

            System.out.println(inquiry);
            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                Appointment appointment = new Appointment(
                        output.getInt("Appointment_ID"),
                        output.getString("Title"),
                        output.getString("Description"),
                        output.getString("Location"),
                        output.getString("Type"),
                        format.sqlToLocalTimestamp(output.getTimestamp("Start")),
                        format.sqlToLocalTimestamp(output.getTimestamp("End")),
                        output.getInt("Customer_ID"),
                        output.getInt("User_ID"),
                        output.getInt("Contact_ID")
                );
                userAppointmentList.add(appointment);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println(userAppointmentList.size());

        if(userAppointmentList.size()>0){
            return true;
        }else{
            return false;
        }
    }

    /** Check if there is an appointment overlap for a customer during Edit*/
    public  Boolean checkAppointmentOverLap(int customerId, Timestamp start, Timestamp end, int id){
        Connection connection = db.connection();
        ObservableList<Appointment> userAppointmentList = FXCollections.observableArrayList();
        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT *  FROM Appointments WHERE (Start BETWEEN '"+ start +
                    "' AND '" + end + "') AND Customer_ID =" + customerId + "";

            System.out.println(inquiry);
            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                Appointment appointment = new Appointment(
                        output.getInt("Appointment_ID"),
                        output.getString("Title"),
                        output.getString("Description"),
                        output.getString("Location"),
                        output.getString("Type"),
                        format.sqlToLocalTimestamp(output.getTimestamp("Start")),
                        format.sqlToLocalTimestamp(output.getTimestamp("End")),
                        output.getInt("Customer_ID"),
                        output.getInt("User_ID"),
                        output.getInt("Contact_ID")
                );
                userAppointmentList.add(appointment);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println(userAppointmentList.size());

        if(userAppointmentList.size()>0 && userAppointmentList.get(0).getAppointmentId() != id){
            return true;
        }else{
            return false;
        }
    }

    /** Get appointments by custoemr */
    public  ObservableList<Appointment> getAppointmentsByCustomer(int customerId){
        Connection connection = db.connection();
        ObservableList<Appointment> userAppointmentList = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT *  FROM Appointments WHERE Customer_ID =" + customerId + "";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                Appointment appointment = new Appointment(
                        output.getInt("Appointment_ID"),
                        output.getString("Title"),
                        output.getString("Description"),
                        output.getString("Location"),
                        output.getString("Type"),
                        format.sqlToLocalTimestamp(output.getTimestamp("Start")),
                        format.sqlToLocalTimestamp(output.getTimestamp("End")),
                        output.getInt("Customer_ID"),
                        output.getInt("User_ID"),
                        output.getInt("Contact_ID")
                );
                userAppointmentList.add(appointment);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return userAppointmentList;
    }

    /** Get appointments by conact */
    public  ObservableList<Appointment> getAppointmentsByContact(int contactId){
        Connection connection = db.connection();
        ObservableList<Appointment> userAppointmentList = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT *  FROM Appointments WHERE Contact_ID =" + contactId + "";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                Appointment appointment = new Appointment(
                        output.getInt("Appointment_ID"),
                        output.getString("Title"),
                        output.getString("Description"),
                        output.getString("Location"),
                        output.getString("Type"),
                        format.sqlToLocalTimestamp(output.getTimestamp("Start")),
                        format.sqlToLocalTimestamp(output.getTimestamp("End")),
                        output.getInt("Customer_ID"),
                        output.getInt("User_ID"),
                        output.getInt("Contact_ID")
                );
                userAppointmentList.add(appointment);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return userAppointmentList;
    }

    /** Fetch all appointments*/
    public  ObservableList<Appointment> getAllAppointments(){
        Connection connection = db.connection();
        ObservableList<Appointment> userAppointmentList = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            String inquiry = "SELECT *  FROM Appointments";

            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                Appointment appointment = new Appointment(
                        output.getInt("Appointment_ID"),
                        output.getString("Title"),
                        output.getString("Description"),
                        output.getString("Location"),
                        output.getString("Type"),
                        format.sqlToLocalTimestamp(output.getTimestamp("Start")),
                        format.sqlToLocalTimestamp(output.getTimestamp("End")),
                        output.getInt("Customer_ID"),
                        output.getInt("User_ID"),
                        output.getInt("Contact_ID")

                );
                userAppointmentList.add(appointment);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return userAppointmentList;
    }

    /** Fetch appointments within 15 minutes */
    public  ObservableList<Appointment> getAppointmentsWithin15(){
        ObservableList<Appointment> userAppointmentList = FXCollections.observableArrayList();
        try {
            Timestamp now =format.localToSqlTimestamp(LocalTime.now(), LocalDate.now());
            Timestamp nowPlus15 = format.localToSqlTimestamp(LocalTime.now().plusMinutes(15), LocalDate.now());
            Statement statement = connection.createStatement();
            String inquiry = "SELECT *  FROM Appointments WHERE (Start BETWEEN '"+ now +"' AND '" + nowPlus15 + "')";
            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                Appointment appointment = new Appointment(
                        output.getInt("Appointment_ID"),
                        output.getString("Title"),
                        output.getString("Description"),
                        output.getString("Location"),
                        output.getString("Type"),
                        format.sqlToLocalTimestamp(output.getTimestamp("Start")),
                        format.sqlToLocalTimestamp(output.getTimestamp("End")),
                        output.getInt("Customer_ID"),
                        output.getInt("User_ID"),
                        output.getInt("Contact_ID")
                );
                userAppointmentList.add(appointment);
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return userAppointmentList;
    }

    /** Delete customer */
    public boolean deleteCustomer(int id){
        try{
            Statement statement = connection.createStatement();
            String inquiry = "DELETE FROM Customers WHERE Customer_ID =" + id + "";

             statement.executeUpdate(inquiry);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /** Get appointment count per Month */
    public Map<String, Integer> getNumberPerMonth(){
        final Map<String, Integer> monthAppCount = new HashMap<>();

        try {
            Statement statement = connection.createStatement();
            String inquiry = "Select date_format(Start, '%M') as Month,count(Appointment_ID) as Count " +
                    "from Appointments group by date_format(Start, '%M');";
            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                monthAppCount.put(output.getString("Month"), output.getInt("Count"));
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return  monthAppCount;
    }

    /** Get appointment count per custoemr */
    public Map<String, Integer> getNumberPerCustomer(){
        final Map<String, Integer> appCount = new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            String inquiry = "Select Customer_ID, count(Appointment_ID) as Count from Appointments group by Customer_ID;";
            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                appCount.put(output.getString("Customer_ID"), output.getInt("Count"));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return  appCount;
    }

    public Map<String, Integer> getNumberPerContact(){
        final Map<String, Integer> appCount = new HashMap<>();

        try {
            Statement statement = connection.createStatement();
            String inquiry = "Select Contact_ID, count(Appointment_ID) as Count from Appointments group by Contact_ID;";
            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                appCount.put(output.getString("Contact_ID"), output.getInt("Count"));
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return  appCount;
    }
    public Map<String, Integer> getNumberPerType(){
        final Map<String, Integer> typeAppCount = new HashMap<>();

        try {
            Statement statement = connection.createStatement();
            String inquiry = "Select Type ,count(Appointment_ID) as Count from Appointments group by type;";
            ResultSet output = statement.executeQuery(inquiry);
            while (output.next()) {
                typeAppCount.put(output.getString("Type"), output.getInt("Count"));
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return  typeAppCount;
    }


    public boolean deleteAppointment(int id){
        try{
            Connection connection = db.connection();
            Statement statement = connection.createStatement();
            String inquiry = "DELETE FROM Appointments WHERE Appointment_ID ='" + id + "'";
            statement.executeUpdate(inquiry);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}

