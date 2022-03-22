package Main.Models;
/**
 * Appointment Class
 * Contains the attributes and methods for an Appointment
 */



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Divisions {

    Database db = new Database();
    private int divisionId;
    private String divisionName;
    private int divCountryID;
    private static ObservableList<Divisions> allDivisions = FXCollections.observableArrayList();

    public Divisions(int divisionId, String divisionName, int divCountryID) {
        this.divisionId = divisionId;
        this.divisionName = divisionName;
        this.divCountryID = divCountryID;

    }



    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * getDivCountryId is used to get the country ID from the local list
     * @return
     */
    public int getDivCountryID() {
        return divCountryID;
    }

    /**
     * setDivCountryId is used to set
     * @param divCountryID
     */
    public void setDivCountryID(int divCountryID) {
        this.divCountryID = divCountryID;
    }


    public static Countries getCountry(int id) {
    return null;
    }

}
