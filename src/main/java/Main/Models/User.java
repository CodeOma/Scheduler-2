package Main.Models;
import Main.Utilities.DbConnection;
import Main.Utilities.Log;
/**
 * User Class
 * Contains the attributes and methods for an Appointment
 */


import java.time.ZoneId;
import java.util.Random;
public class User {
    private int userId;
    private String userName;
    private String userPassword;
    private static ZoneId zoneId;

    DbConnection db = new DbConnection();

    // To generate Random User ID
    Random rand = new Random();


    //Public Constructor
    public void User(String userName, String userPassword){
        this.userId = rand.nextInt(900) + 100;
        this.userName = userName;
        this.userPassword = userPassword;
    }


    /**
     * Getters */
    public int getUserId(){
        return this.userId;
    }
    public String getUserName(){
        return this.userName;
    }


    /**
     * Setters */
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void getUserPassword(String userPassword){
        this.userPassword = userPassword;
    }


    /**
     *  Authentication */
    public boolean login(String userName, String password){
        try{
        Database db = new Database();
        int userId = db.login(userName, password);
        if(userId != 0){
            SessionLog.setLoggedOnUser(userId);
            Log.auditLogin(userName,true);
            return true;
        }else{
            Log.auditLogin(userName,false);
            return false;
        }
        }catch (Exception e){
            e.toString();
        }
        return false;
    }

    //Future improvement add Encryption
    public static boolean authenticate(String username, String password){

//        Log.auditLogin(userName, logon);

        return true;
    }

}
