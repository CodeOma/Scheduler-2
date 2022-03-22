package Main.Models;
/**
 * Customer Class
 * Contains the attributes and methods for a Customer
 */

import java.util.Random;

public class Customer {

    private int customerId;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private String country;
    private int divisionId;

    Random rand = new Random();

    /**  With ID constructor used for edits*/
    public Customer(int id, String customerName, String address,String postalCode,
                    String phone, String country, int divisionId){

        this.customerId = id;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.country = country;
        this.divisionId = divisionId;
    }
    /**  Without ID constructor used for New*/
    public Customer(String customerName, String address,String postalCode,
                    String phone, String country, int divisionId){
        this.customerId = rand.nextInt(900)  + 100;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.country = country;
        this.divisionId = divisionId;
    }



    /** Getters */
    public int getId() {
        return this.customerId;
    }

    public String getCustomerName() {
        return this.customerName;
    }
    public String getPostalCode() {
        return this.postalCode;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPhoneNumber() {
        return this.phone;
    }

    public String getCity() {
        return this.postalCode;
    }
    public String getCountry() {
        return this.country;
    }

    public int getDivisionId() {
        return this.divisionId;
    }


    /** Setters */

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCountry(String country) {
        this.country = country;
    }




}
