package Main.Models;
/**
 * Appointment Class
 * Contains the attributes and methods for an Appointment
 */

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class Appointment {

    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private String type;
    private int customerID;
    private int userID;
    private int contactID;
    /** Time */
    private Timestamp startTime;
    private Timestamp endTime;
    private LocalDateTime  createDate;
    /** */
    private String createBy;
    private LocalDateTime timeAdded;
    private String lastUpdated;


    Random rand = new Random();
    Database db = new Database();
    public Appointment(String title, String description, String location,
                       String type, Timestamp startTime, Timestamp endTime,
                        Integer customerID, Integer userID,
                       int contactID) {

        this.appointmentId = rand.nextInt(900) + 100;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;

    }


    public Appointment(int id,String title, String description, String location,
                       String type, Timestamp startTime, Timestamp endTime,
                       Integer customerID, Integer userID,
                       int contactID) {

        this.appointmentId = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;



    }




    /** Getters */
    public Integer getAppointmentId() {
        return this.appointmentId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLocation() {
        return this.location;
    }

    public String getType() {
        return  this.type;
    }

    public Integer getCustomerID() {
        return this.customerID;
    }

    public Integer getUserID() {
        return this.userID;
    }

    public int getContactID() {
        return contactID;
    }

    public String getCustomerName() {
        return db.getCustomerName(customerID);
    }


    public LocalDate getDate() {
        return startTime.toLocalDateTime().toLocalDate();
    }

    public LocalDate getEndDate() {
        return endTime.toLocalDateTime().toLocalDate();
    }

    public Timestamp  getStartTime() {
        return this.startTime;
    }

    public Timestamp getEndTime() {
        return this.endTime;
    }

    public LocalDateTime  getCreateDate() {
        return this.createDate;
    }

    public String getCreateBy() {
        return this.createBy;
    }



}
