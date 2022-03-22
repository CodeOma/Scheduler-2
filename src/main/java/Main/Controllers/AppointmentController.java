package Main.Controllers;
/**
 * <h6>Appointment Dashboard</h6>
 * <p>The class provides functionality for the appointment dashboard.\n
 * The populateAppointments function uses a lambda expression. The Appointment as the variable
 * cellData is processed. A method is called to get the correct data type an return it.
 * \n
 * The filter appointments by month and week uses a lambda expression to filter appointments that are within a give month.
 *</p>
 * */
import Main.Models.Appointment;
import Main.Models.Database;
import Main.Models.SessionLog;
import Main.Scheduler;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


public class AppointmentController implements Initializable {
    Stage stage;
    Parent scene;

    //Import JavaFx
    @FXML
    Button addButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;
    @FXML
    Button backButton;
    @FXML
    Button nextTimeButton;
    @FXML
    Button previousTimeButton;
    //Future improvement
    //    @FXML
    //    RadioButton yearFilterButton;
    @FXML
    RadioButton monthRadio;
    @FXML
    RadioButton weekRadio;
    @FXML
    RadioButton allRadio;
    @FXML
    Label viewLabel;
    @FXML
    ToggleGroup viewMode;
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
    TableColumn<Appointment, Integer> customerColumn;
    @FXML
    TableColumn<Appointment, String> typeColumn;
    @FXML
    TableColumn<Appointment, String> dateColumn;
    @FXML
    TableColumn<Appointment, String> startTimeColumn;
    @FXML
    TableColumn<Appointment, String> endTimeColumn;
    @FXML
    TableColumn<Appointment, Integer> customerIdColumn;
    @FXML
    TableColumn<Appointment, Integer> contactColumn;
    @FXML
    TableColumn<Appointment, Integer> userIdColumn;

    @FXML
    ToggleGroup filterToggle;
    @FXML
    Label selectedTimeLabel;

    @FXML
    Button prevButton;
    @FXML
    Button nextButton;
    // Markers for date filtering.
    ZonedDateTime startRangeMarker;
    ZonedDateTime endRangeMarker;



    LocalDate now = LocalDate.now();
    TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
    private Month month = now.getMonth();
    private int week = now.get(woy);
    private Database db = new Database();
    private int user = SessionLog.getLoggedOnUser();
    private ObservableList<Appointment> appointmentList = db.getAppointments();

    public void switchScreen(ActionEvent event, String switchPath) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(switchPath));
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }




    public void handleToggle(){
        try{
            if(monthRadio.isSelected()){
                prevButton.setVisible(true);
                nextButton.setVisible(true);
                System.out.println(month.toString());
                viewLabel.setText(month.toString());
                filterAppointmentsByMonth(appointmentList, month);
            }
            if(weekRadio.isSelected()){
                viewLabel.setText("Week " + week);
                prevButton.setVisible(true);
                nextButton.setVisible(true);
                filterAppointmentsByWeek(appointmentList, week);
            }
            if(allRadio.isSelected()){
                prevButton.setVisible(false);
                nextButton.setVisible(false);
                appointmentTable.setItems(appointmentList);
                viewLabel.setText("Calender");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


    /** To Main Menu if Cancelled**/
    public void toDashboard(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("Dashboard.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }


    public void deleteAppointment(ActionEvent event) {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();

        // check that user selected an appointment in the table
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select an appointment to delete");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " +
                    selected.getTitle() + "\n ID " + selected.getAppointmentId() + "\n Type: "
                    + selected.getType() +
                    "? ");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Boolean success = db.deleteAppointment(selected.getAppointmentId());
                populateAppointments(db.getAppointments());
                if (success) {

                    ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                    Alert deletedAppt = new Alert(Alert.AlertType.CONFIRMATION, "Appointment deleted", clickOkay);
                    deletedAppt.showAndWait();
                } else {
                    ButtonType clickOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
                    Alert deleteAppt = new Alert(Alert.AlertType.WARNING, "Failed to delete Appointment", clickOkay);
                    deleteAppt.showAndWait();
                }
            }
            else {
                return;
            }
        }
    }



    /** To add appointment View*/
    public void toAddAppointment(ActionEvent event) throws IOException {

        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("AddAppointmentView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }


    /** Filters appointment by Month. Uses  a lambda expression to loop through appointments*/
    public void filterAppointmentsByMonth(ObservableList appointments, Month month) throws SQLException {

        //filter appointments for month
        LocalDate now = LocalDate.now();

        /** lambda expression used to filter appointments by month*/
        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        viewLabel.setText(month.toString());
        filteredData.setPredicate(row -> {
            return row.getDate().getMonth() == month;
        });

        appointmentTable.setItems(filteredData);
    }

    /** Filters appointment by week. Uses  a lambda expression to loop through appointments*/
    public void filterAppointmentsByWeek(ObservableList appointments, int week ) throws SQLException {
        LocalDate now = LocalDate.now();

        //filter appointments for month
        FilteredList<Appointment> filteredData = new FilteredList<>(appointments);
        TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        /** lambda expression used to filter appointments by month*/
        filteredData.setPredicate(row -> week == row.getDate().get(woy));
        viewLabel.setText("Week " +  week);
        appointmentTable.setItems(filteredData);
    }

    /** Goes to previous month or week */
    public void  onPrev(){
        try {
            if (weekRadio.isSelected()) {
                if(week == 1){
                    week = 52;
                }else{
                    week -= 1;
                }
                filterAppointmentsByWeek(appointmentList, week);
            }
            if (monthRadio.isSelected()) {
                if(month == Month.JANUARY){
                    month = Month.DECEMBER;
                }else{
                    month = month.minus(1);
                }
                filterAppointmentsByMonth(appointmentList, month);
            }
        }catch(Exception e){

        }

    }

    /** Goes to next month or week */
    public void  onNext(){
        try {
            if (weekRadio.isSelected()) {
            if(week == 52){
                week = 1;
            }else{
                week += 1;
            }
                filterAppointmentsByWeek(appointmentList, week);
            }
            if (monthRadio.isSelected()) {
            if(month == Month.DECEMBER){
                month = Month.JANUARY;
            }else{
               month = month.plus(1);
            }
            filterAppointmentsByMonth(appointmentList, month);
            }
        }catch (Exception e){
        }
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

   /** Takes appointment list an populates TableView */
    public void populateAppointments(ObservableList<Appointment> inputList) {
        appointmentTable.setItems(inputList);
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAppointmentId()).asObject());
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        customerColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomerID()).asObject());
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().toLocalDateTime().toLocalTime().toString()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().toLocalDateTime().toLocalTime().toString()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        userIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserID()).asObject());
        contactColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getContactID()).asObject());
    }


    /**lambda expressions used to extract correct attribute from Appointment Class*/
    @Override
    public void initialize(URL location, ResourceBundle resources)   {
try {
    appointmentTable.setItems(appointmentList);

    idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAppointmentId()).asObject());
    titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
    descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
    typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
    customerColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomerID()).asObject());
    startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().toLocalDateTime().toLocalTime().toString()));
    endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().toLocalDateTime().toLocalTime().toString()));
    dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
    userIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserID()).asObject());
    contactColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getContactID()).asObject());
}catch (Exception e){
    e.printStackTrace();
}


    }

}
