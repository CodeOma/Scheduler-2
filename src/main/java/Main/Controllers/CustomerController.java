package Main.Controllers;

/**
 * <h6>Customer Dashboard</h6>
 * <p>The class provides functionality for the Customer Dashboard
 *</p>
 * */
import Main.Models.Customer;
import Main.Models.Database;
import Main.Models.SessionLog;
import Main.Scheduler;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
import java.util.Optional;
import java.util.ResourceBundle;





public class CustomerController implements Initializable {
    Stage stage;
    Parent scene;


    @FXML
    private TableView<Customer> customerTable;


    @FXML
    private TableColumn<Customer, Integer> idColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, String> postalColumn;
    @FXML
    private TableColumn<Customer, Integer> divisionColumn;
    @FXML
    private TableColumn<Customer, String> countryColumn;
    @FXML
    private TableColumn<Customer, String> phoneNumberColumn;

    Database db = new Database();

    /** Navigation to other pages */
    /** To Dashboard **/
    public void toDashboard(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("Dashboard.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    /** To Add Customer **/
    public void toAddCustomer(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("AddCustomerView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    /** To Edit Customer **/
    public void toEditCustomer(ActionEvent event) throws IOException {
        try{
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Scheduler.class.getResource("EditCustomerView.fxml"));
        fxmlLoader.load();
        EditCustomerController controller = fxmlLoader.getController();
        controller.getCustomer(customerTable.getSelectionModel().getSelectedItem());
        stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
        Parent scene = fxmlLoader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();

        }catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select a customer to edit");
            alert.show();
        }

    }

    /** delete customer */
    public void deleteCustomer() {
        try {
            Customer customer = customerTable.getSelectionModel().getSelectedItem();
            if(db.getAppointmentsByCustomer(customer.getId()).size() > 0){
                throw new Exception("Customer cannot be deleted with appointments");
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " +
                    customer.getCustomerName() +
                    "? ");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                db.deleteCustomer(customer.getId());
                ObservableList<Customer> customers = db.getCustomers();
                customerTable.setItems(customers);
            }

        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            if(e.toString().contains("cannot be deleted")){
                alert.setContentText(e.getMessage().toString());
            }else{
                alert.setHeaderText("No customer selected");
                alert.setContentText("Please select a customer to delete.");
            }

            alert.show();
        }

    }

    /** Initializes tables with Customer Data */
    public  void initialize(URL url, ResourceBundle rb){
        ObservableList<Customer> customers = db.getCustomers();
        customerTable.setItems(customers);
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        countryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCountry()));
        postalColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCity()));
        divisionColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDivisionId()).asObject());
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        phoneNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));


    }


}
