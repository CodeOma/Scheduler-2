package Main.Models;
/**
 * <p>Country Class</p>
 * <p>Contains the attributes and methods for a Country</p>
 */

import java.sql.SQLException;
import java.text.DateFormat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Random;

public class Countries {
        private int countryId;
        private String country;
        private DateFormat createDate;
        private String createdBy;
        private DateFormat lastUpdate;
        private String lastUpdateBy;

        Random rand = new Random();



        Countries(int countryId){
                HashMap<Integer, String> countryMap = new HashMap<Integer, String>();
                countryMap.put(1,"US");
                countryMap.put(2,"UK");
                countryMap.put(3,"Canada");

            this.countryId = countryId;
            this.country = countryMap.get(countryId);

        }

        /** getters*/
        public int getCountryId(){
            return countryId;
        }

        public String getCountry(){
        return country;
}

        public static ObservableList<String> getAllCountries() throws SQLException {

        ObservableList<String> allCities = FXCollections.observableArrayList();

        allCities.addAll("US","UK","Canada", "");
        return allCities;


}


}
