package Main.Utilities;


import java.io.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;


public class Log {

    //File login_activity.txt
    private static final String logPath = "login_activity.txt";

    public static void auditLogin(String userName, Boolean successBool){
        try {

            //
            BufferedWriter log = new BufferedWriter(new FileWriter(logPath, true));
            log.append(ZonedDateTime.now(ZoneOffset.UTC).toString() + " UTC-LOGIN ATTEMPT USER: " + userName +
                    " LOGGED IN: " + successBool.toString() + "\n");
            log.flush();
            log.close();
        }
        catch (Exception error) {
            error.printStackTrace();
        }

    }

    public static Map<String, Integer> reportLoginAttempts(){
        Map<String, Integer> loginAttempts = new HashMap<String,Integer>();
        var success = 0;
        var fail = 0;
        try{

        try (BufferedReader br = new BufferedReader(new FileReader("login_activity.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] items = line.split(" ");
                 if (items[items.length-1].equals("true")){
                     success += 1;
                 }else{
                     fail += 1;
                 }

            }
        }
        loginAttempts.put("Success",success);
        loginAttempts.put("Fail",fail);
        }catch (Exception e){

        }
        return loginAttempts;
    }

}
