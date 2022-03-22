package Main.Controllers;
/**
 * <h6>Add Appointment</h6>
 * <p>The class provides functionality for the adding appointments
 *</p>
 * */
import Main.Models.*;
import Main.Scheduler;
import Main.Utilities.Helpers.Formatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.*;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {
    Stage stage;
    Parent scene;

    @FXML
    private Label idLabel;
    @FXML
    private TextField appointmentIdTextBox;
    @FXML
    private TextField titleTextBox;
    @FXML
    private TextField locationTextBox;
    @FXML
    private TextField typeTextBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private ComboBox customerIdDropDown;
    @FXML
    private ComboBox contactIdDropDown;
    @FXML
    private TextField userIdTextBox;
    @FXML
    private TextField startTimeTextBox;
    @FXML
    private TextField endTimeTextBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label businessHoursLabel;

    Database db = new Database();


    ZonedDateTime startHours = ZonedDateTime.parse("2022-01-01T08:00:00-05:00[America/New_York]");
    ZonedDateTime endHours = ZonedDateTime.parse("2022-01-01T22:00:00-05:00[America/New_York]");
    LocalDateTime startBusiness = startHours.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    LocalDateTime endBusiness = endHours.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    LocalTime startHour = startBusiness.toLocalTime();
    LocalTime endHour = endBusiness.toLocalTime();


    /** To Main Menu if Cancelled**/
    public void toMainMenu(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("AppointmentView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }





    /** To Appointment Dashboard if Cancelled**/
    public void toAppointmentDashboard(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("AppointmentView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    /**Save Part */
    @FXML
    public void saveAppointment( ActionEvent event) throws IOException {
        try {
            /**Convert input fields into correct types*/
            String title = titleTextBox.getText();
            String location =  locationTextBox.getText();
            String type = typeTextBox.getText();
            String description = descriptionTextArea.getText();
            String[] arrayOfStr = customerIdDropDown.getValue().toString().split(" ", 2);
            int customerId =  Integer.parseInt(arrayOfStr[0]);
            String[] contacts = contactIdDropDown.getValue().toString().split(" ", 2);
            int contactID =  Integer.parseInt(contacts[0]);
            LocalDate date =  datePicker.getValue();
            LocalDate endDate =  endDatePicker.getValue();
            LocalDateTime startTime = LocalTime.parse(startTimeTextBox.getText()).atDate(date) ;
            LocalDateTime endTime = LocalTime.parse(endTimeTextBox.getText()).atDate(endDate) ;

//Check the dates
          if(endTime.isBefore(startTime)){
                throw new Exception("End time must be after start time");
            }

            if(startTime.toLocalTime().isBefore(startHour) || endTime.toLocalTime().isAfter(endHour)){
                throw new Exception("Appointment must be within business hours " + startBusiness.getHour() + ":00 - " + endBusiness.getHour() +":00");
            }

            Formatter format = new Formatter();
            Timestamp startTS =  format.localToSqlTimestamp(startTime.toLocalTime(),date);
            Timestamp endTS = format.localToSqlTimestamp(endTime.toLocalTime(),endDate);

            if(db.checkAppointmentOverLap(customerId,startTS,endTS)){
                throw new Exception("Two appointments can't overlap for a customer");
            }

            //
            if (title.length() != 0  && description.length()  != 0 && type.length()  != 0  && startTime.toString().length()  != 0 && endTime.toString().length()  != 0) {
                Appointment appointment = new Appointment( title, description, location,
                        type, startTS, endTS, customerId, SessionLog.getLoggedOnUser(), contactID);
                Database db = new Database();
                String result = db.addAppointment(appointment);
                stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
                scene = FXMLLoader.load(Scheduler.class.getResource("AppointmentView.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }else{
                throw new Exception("Fields Can't be left blank");
            }
            /**If there is any error we catch is and print it out*/
        } catch (Exception e) {
            Alert invalidEntryAlert = new Alert(Alert.AlertType.ERROR);
            invalidEntryAlert.setTitle("Error");
            invalidEntryAlert.setHeaderText(e.toString());
            invalidEntryAlert.show();

        }
    }


    public  void initialize(URL url, ResourceBundle rb){
        //Prep business hours

        businessHoursLabel.setText(startHour.toString() + ":00 - " + endHour + ":00 " + ZoneId.systemDefault().toString());

        int user = SessionLog.getLoggedOnUser();
        idLabel.setText(String.valueOf(user));
        customerIdDropDown.setItems(db.getCustomersIds());
        contactIdDropDown.setItems(db.getContactIds());
    }
}
