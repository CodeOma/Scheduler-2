package Main.Controllers;
/**
 * <h6>Edit Customers</h6>
 * <p>The class provides functionality for the editing customers
 *</p>
 * */
import Main.Models.*;
import Main.Scheduler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditCustomerController implements Initializable {
    Stage stage;
    Parent scene;

    @FXML
    private TextField nameTextBox;
    @FXML
    private TextField addressTextBox;
    @FXML
    private ComboBox divisionIdDropDown;
    @FXML
    private ComboBox countryDropDown;
    @FXML
    private TextField phoneNumberTextBox;
    @FXML
    private TextField postalCodeTextBox;
    @FXML
    private TextField idTextBox;

    Database db = new Database();
    ObservableList<String> divList = FXCollections.observableArrayList();
    ObservableList<String> filteredDivList = FXCollections.observableArrayList();



    /** To Main Customer View if Cancelled**/
    public void toCustomerDashboard(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("CustomerView.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** Get the name of each division */
    private String getDivisionName(int id){
        String div ="";
        for(int i = 0; i < divList.size(); i++) {
            String[] arrayOfStr = divList.get(i).split(" ", 2);
            int divisionId = Integer.parseInt(arrayOfStr[0]);
            String division = arrayOfStr[1];
            if (divisionId == id) {
                div = division;
            }
        }
        return div;
    }

    /** Update divs based on country filter */
    public void updateDivs(){
        ObservableList<String> temp = FXCollections.observableArrayList();

        String country = countryDropDown.getValue().toString();
        for(int i = 0; i < divList.size(); i++){
            String[] arrayOfStr = divList.get(i).split(" ", 2);
            int divisionId = Integer.parseInt(arrayOfStr[0]);
            if(country.equals("US")) {
                if(divisionId <= 54){
                    temp.add(divList.get(i));
                }
            }else if(country.equals("Canada")) {
                if (divisionId <= 72 && divisionId >= 55) {
                    temp.add(divList.get(i));
                }
            }else{
                if (divisionId > 72) {
                    temp.add(divList.get(i));
                }

            }
        }
        divisionIdDropDown.setItems(temp);
    }

    /** Get Customers */
    public void getCustomer(Customer customer) {
        /** Get all customer data to prefill form */
        idTextBox.setText(String.valueOf(customer.getId()));
        nameTextBox.setText(customer.getCustomerName());
        countryDropDown.setValue(customer.getCountry());
        postalCodeTextBox.setText(customer.getPostalCode());
        divisionIdDropDown.setValue(customer.getDivisionId() + " " + getDivisionName(customer.getDivisionId()));
        phoneNumberTextBox.setText(customer.getPhoneNumber());
        addressTextBox.setText(customer.getAddress());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> country = FXCollections.observableArrayList();
        try {
            country.addAll("US","UK","Canada");
            countryDropDown.setItems(country);
            divList.setAll(db.getDivisionIds());
            divisionIdDropDown.setItems(db.getDivisionIds());
        }catch (Exception e){
        }
    }





    /**Save Customer */
    @FXML
    public void saveCustomer( ActionEvent event) throws IOException {
        try {
            /**Convert input fields into correct types*/
            int id = Integer.parseInt(idTextBox.getText());
            String name = nameTextBox.getText();
            String country = countryDropDown.getValue().toString();
            String postalCode = postalCodeTextBox.getText();
            String[] arrayOfStr = divisionIdDropDown.getValue().toString().split(" ", 2);
            int divisionId = Integer.parseInt(arrayOfStr[0]);
            String address  = addressTextBox.getText();
            String phoneNumber = phoneNumberTextBox.getText();

            if (name.length() != 0  && country.length()  != 0 && address.length()  != 0 && phoneNumber.length()  > 9 && phoneNumber.length()  < 13) {
                Customer customer = new Customer(id ,name,address,postalCode,phoneNumber,country, divisionId);
                Database db = new Database();
                String newId = db.editCustomer(customer);
                if(newId.equals("Saved Successfully")){
                    stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
                    scene = FXMLLoader.load(Scheduler.class.getResource("CustomerView.fxml"));
                    stage.setScene(new Scene(scene));
                    stage.show();
                }else{
                    Alert invalidEntryAlert = new Alert(Alert.AlertType.ERROR);
                    invalidEntryAlert.setTitle("Error");
                    invalidEntryAlert.show();
                }

            }else{
                throw new Exception("Please check fields. Phone number must be 9+ digits.");
            }
        } catch (Exception e) {
            Alert invalidEntryAlert = new Alert(Alert.AlertType.ERROR);
            invalidEntryAlert.setTitle("Error");
            invalidEntryAlert.setHeaderText(e.toString());
            invalidEntryAlert.show();
        }
    }



}



