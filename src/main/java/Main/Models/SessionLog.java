package Main.Models;
/**
 * <h6>Session Log Class</h6>
 * <p>Contains the information of the user logged in.</p>
 */

import java.time.ZoneId;
import java.util.Locale;

public class SessionLog {

    private static int usersLoggedIn;
    private static Locale userLocale;
    private static ZoneId userTimeZone;


    public static int getLoggedOnUser() {
        return usersLoggedIn;
    }

    public static Locale getUserLocale() {
        return userLocale;
    }

    public  static  ZoneId getUserTimeZone(){
        return userTimeZone;
    }

    public static void setLoggedOnUser(int id) {
        usersLoggedIn =  id;
    }

    public static void setUserLocale(Locale locale) {
        userLocale = locale;
    }

    public  static  void setUserTimeZone(ZoneId timeZone){
        userTimeZone = timeZone;
    }
    public static void logOff() {
        usersLoggedIn = 0;
        userLocale = null;
        userTimeZone = null;
    }
}
