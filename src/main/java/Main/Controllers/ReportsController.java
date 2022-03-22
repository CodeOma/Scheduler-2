package Main.Controllers;

import Main.Models.Appointment;
import Main.Models.Customer;
import Main.Models.Database;
import Main.Models.SessionLog;
import Main.Scheduler;
import Main.Utilities.Log;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;


public class ReportsController implements Initializable {
    Stage stage;
    Parent scene;


    @FXML
    ComboBox typeList;
    @FXML
    ComboBox contactList;
    @FXML
    ComboBox customerList;
    @FXML
    ComboBox yearList;
    @FXML
    Label viewLabel;
    @FXML
    Label typeLabel;
    @FXML
    Label reportLabel;
    @FXML
    Label reportsLabel;
    @FXML
    Label countLabel;
    @FXML
    AnchorPane typeReport;
    @FXML
    TableView<Appointment> appointmentTable;
    @FXML
    TableColumn<Appointment, Integer> idColumn;
    @FXML
    TableColumn<Appointment, String> titleColumn;
    @FXML
    TableColumn<Appointment, String> descriptionColumn;
    @FXML
    TableColumn<Appointment, String> locationColumn;

    @FXML
    TableColumn<Appointment, Integer> contactColumn;
    @FXML
    TableColumn<Appointment, String> typeColumn;
    @FXML
    TableColumn<Appointment, String> dateColumn;
    @FXML
    TableColumn<Appointment, String> startTimeColumn;
    @FXML
    TableColumn<Appointment, String> endTimeColumn;

    @FXML
    TableView<Database.TypeCount> typesTable;
    @FXML
    TableColumn<Database.TypeCount, String> typesColumn;
    @FXML
    TableColumn<Database.TypeCount, String> countColumn;
    @FXML
    Label appointmentsLabel;
    @FXML
    TableColumn<Appointment, Integer> customerIdColumn;
    @FXML
    TableColumn<Appointment, String> typeColumn1;

    LocalDate now = LocalDate.now();
    TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
    private Month month = now.getMonth();
    private int week = now.get(woy);
    private Database db = new Database();
    private int user = SessionLog.getLoggedOnUser();
    private ObservableList<Appointment> appointmentList = db.getAppointments();



    /** To Main Menu if Cancelled**/
    public void toDashboard(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("Dashboard.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** Filters by month */
    public void filterByMonth(){
        try{
            LocalDate now = LocalDate.now();
            filterAppointmentsByMonth(appointmentList, month);

        }catch(Exception e){
        }
    }

    /** Filter appointments by Month. uses lambda expression used to filter appointments by Month */
    public void filterAppointmentsByMonth(ObservableList appointments, Month month) throws SQLException {
        //filter appointments for month
        LocalDate now = LocalDate.now();
//        LocalDate nowPlus1Month = now.plusMonths(1);
//        LocalDate thisMonth = now.plusMonths(1);
        //lambda expression used to filter appointments by month
        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        viewLabel.setText(month.toString());
        filteredData.setPredicate(row -> {
            return row.getDate().getMonth() == month;
        });

        appointmentTable.setItems(filteredData);
    }

    /** Filter appointments by Week. uses lambda expression used to filter appointments by week */
    public void filterAppointmentsByWeek(ObservableList appointments, int week ) throws SQLException {
        LocalDate now = LocalDate.now();

        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();

        filteredData.setPredicate(row -> week == row.getDate().get(woy));
        viewLabel.setText("Week " +  week);
        appointmentTable.setItems(filteredData);
    }



    public void filterAppointmentsByCustomer(ObservableList appointments) throws SQLException {
        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        filteredData.setPredicate(row -> row.getCustomerID() == Integer.parseInt(customerList.getValue().toString()));
        appointmentTable.setItems(filteredData);
    }

    public void filterAppointmentsByUser(ObservableList appointments) throws SQLException {
        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        filteredData.setPredicate(row -> row.getUserID() == Integer.parseInt(contactList.getValue().toString()));
        appointmentTable.setItems(filteredData);
    }


    /** To Edit Customer **/
    public void toEditAppointment(ActionEvent event) throws IOException {
        try{
            stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Scheduler.class.getResource("EditAppointmentView.fxml"));
            fxmlLoader.load();
            EditAppointmentController controller = fxmlLoader.getController();
            controller.getAppointment(appointmentTable.getSelectionModel().getSelectedItem());
            stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
            Parent scene = fxmlLoader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();

        }catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select an appointment to edit");
            alert.show();
        }

    }

    /** Updates data if contact is changed */
    public void onContactChange(){
        if(contactList.getValue().toString().equals("All")){
                    setTable(db.getAppointments());
        }else{
            String[] arrayOfStr = contactList.getValue().toString().split(" ", 2);
            int contact  =  Integer.parseInt(arrayOfStr[0]);
            ObservableList list = db.getAppointmentsByContact(contact);
            setTable(list);
            countLabel.setText("Count: " + String.valueOf(list.size()));

        }

    }

    /** Updates data if customer is changed */
    public void onCustomerChange(){
        if(customerList.getValue().toString().equals("All")){
            setTable(db.getAllAppointments());
        }else{
            String[] arrayOfStr = customerList.getValue().toString().split(" ", 2);
            int customer  =  Integer.parseInt(arrayOfStr[0]);
            ObservableList list = db.getAppointmentsByCustomer(customer);
            setTable(list);
            countLabel.setText("Count: " + String.valueOf(list.size()));
        }
    }

    /** Sets contact to login report mode */
    public void setContactReport(){
        contactList.setVisible(true);
        yearList.setVisible(false);
        typeList.setVisible(false);
        customerList.setVisible(false);
        countLabel.setVisible(true);
        countLabel.setText("");
        //
        appointmentTable.setVisible(true);
        typeLabel.setText("Contact");
        appointmentsLabel.setVisible(true);
        typesTable.setVisible(false);
        typeReport.setVisible(false);
        reportLabel.setVisible(false);

        String label = "Appointments by Contact ID \n" +
                "Contact ID: Appointment Count \n";

        for (Map.Entry<String, Integer> pair : db.getNumberPerContact().entrySet()) {
            label = label + String.format(" %s : %s \n", pair.getKey(), pair.getValue());
        }
        reportsLabel.setText(label);
    }

    /** Sets report to customer report mode */
    public void setCustomerReport(){
        customerList.setVisible(true);
        yearList.setVisible(false);
        typeList.setVisible(false);
        contactList.setVisible(false);
        countLabel.setVisible(true);
        countLabel.setText("");

        //
        appointmentTable.setVisible(true);
        appointmentsLabel.setVisible(true);
        typeLabel.setText("Customer");
        typesTable.setVisible(false);
        typeReport.setVisible(false);
        reportLabel.setVisible(false);

        String label = "Appointments by Customer ID \n" +
                "Customer ID: Appointment Count\n";
        for (Map.Entry<String, Integer> pair : db.getNumberPerCustomer().entrySet()) {
            label = label + String.format(" %s : %s \n", pair.getKey(), pair.getValue());
        }
        reportsLabel.setText(label);

    }

    /** Sets report to type report mode */
    public void setTypeReport(){
        typeList.setVisible(true);
        yearList.setVisible(true);
        contactList.setVisible(false);
        customerList.setVisible(false);
        countLabel.setVisible(false);
        //
        appointmentTable.setVisible(false);
        typeLabel.setText("Types per Month");
        typesTable.setVisible(true);
        typeReport.setVisible(true);
        appointmentsLabel.setVisible(false);
        reportLabel.setVisible(true);

        String label = "Months \n" ;
        for (Map.Entry<String, Integer> pair : db.getNumberPerMonth().entrySet()) {
            label = label + String.format(" %s : %s \n", pair.getKey(), pair.getValue());
        }
        label = label + "\n Types \n";
        for (Map.Entry<String, Integer> pair : db.getNumberPerType().entrySet()) {
            label = label + String.format(" %s : %s \n", pair.getKey(), pair.getValue());
        }
        reportsLabel.setText(label);
    }

    /** Sets report to login report mode */
    public void setLoginReport(){
        String label = "Login Attempts \n" ;
        for (Map.Entry<String, Integer> pair : Log.reportLoginAttempts().entrySet()) {
            label = label + String.format(" %s : %s \n", pair.getKey(), pair.getValue());
        }
        reportsLabel.setText(label);
    }

    /** Fetches number of appointments by type and month */
    public void numberOfAppByType() throws SQLException {
        var months = new String[ 13 ];
        months[ 0 ] = null;
        months[ 1 ] = "January";
        months[ 2 ] = "February";
        months[ 3 ] = "March";
        months[ 4 ] = "April";
        months[ 5 ] = "May";
        months[ 6 ] = "June";
        months[ 7 ] = "July";
        months[ 8 ] = "August";
        months[ 9 ] = "September";
        months[ 10 ] = "October";
        months[ 11 ] = "November";
        months[ 12 ] = "December";
        ObservableList<Database.TypeCount> types = FXCollections.observableArrayList();
        String month = typeList.getValue().toString();
        String year = yearList.getValue().toString();
        try {
            types.addAll(db.getTypesByMonth(month, year));
            int numberOfTypes = types.size();
            typesTable.setItems(types);
            typesColumn.setCellValueFactory(cellDate -> new SimpleStringProperty(cellDate.getValue().getType()));
            countColumn.setCellValueFactory(cellDate -> new SimpleStringProperty(String.valueOf(cellDate.getValue().getCount())));
            reportLabel.setText("Number of types for " + months[Integer.parseInt(month)] + ": " + numberOfTypes);
        }catch (Exception e ){

        }
    }

/** Takes list of appointments an input them into TableView*/
    private void setTable(ObservableList<Appointment> appointmentsList){
        try {
            appointmentTable.setItems(appointmentsList);
            idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAppointmentId()).asObject());
            titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
            descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
            locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
            typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
            contactColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getContactID()).asObject());
            startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().toLocalDateTime().toLocalTime().toString()));
            endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().toLocalDateTime().toLocalTime().toString()));
            dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
            customerIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomerID()).asObject());
            typeColumn1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));

        }catch (Exception e){
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)   {
        try {
            ObservableList<String> months = FXCollections.observableArrayList();
            months.setAll("1","2","3","4","5","6","7","8","9","10","11","12");
            ObservableList<String> years = FXCollections.observableArrayList();
            years.setAll("2020","2021","2022","2023","2024","2025");

            yearList.setItems(years);
            typeList.setItems(months);
            customerList.setItems(db.getCustomersIds());
            contactList.setItems(db.getContactIds());

        }catch (Exception e){
        }

    }


}
