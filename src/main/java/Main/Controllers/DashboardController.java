package Main.Controllers;
/**
 * <h6>Dashboard</h6>
 * <p>The class provides functionality for the Dashboard
 *</p>
 * */
import Main.Models.Appointment;
import Main.Models.Database;
import Main.Models.SessionLog;
import Main.Scheduler;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
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
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    Stage stage;
    Parent scene;

    @FXML
    TableView<Appointment> appointmentTable;
    @FXML
    Label upcomingLabel;
    @FXML
    TableColumn<Appointment, String> titleColumn;
    @FXML
    TableColumn<Appointment, String> dateColumn;
    @FXML
    TableColumn<Appointment, String> startTimeColumn;

    public  int appSize =0;



    public void logOff(ActionEvent event) throws IOException {
//Future improvement: Add prompt to confirm they want to log off.
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("LoginView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();

    }
    /** Navigation*/

    /** To Customer Dashboard View */
    public void toCustomersPage(ActionEvent event) throws IOException {
            stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
            scene = FXMLLoader.load(Scheduler.class.getResource("CustomerView.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
    }

    /** To Reports Dashboard View */
    public void toReportsPage(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("ReportsView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** To Appointment Dashboard View */
    public void toAppointmentsPage(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("AppointmentView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();


    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Database db = new Database();
        ObservableList<Appointment> upcoming = db.getAppointmentsWithin15();
    if(upcoming.size() == 0){
        upcomingLabel.setText("No upcoming appointments");
        }
    if(upcoming.size() >0){
        appointmentTable.setItems(upcoming);
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().toString()));
    }
    }
}