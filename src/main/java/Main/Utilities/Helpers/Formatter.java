package Main.Utilities.Helpers;
/**
 * Formatter Class
 * Formatter converts time to Timestamp for MySQL.
 * Formatter also converts Timestamp which is in UTC to Local Java time.
 */

import Main.Models.SessionLog;
import Main.Utilities.DbConnection;
import java.sql.*;
import java.time.*;




public class Formatter {
    private final ZoneId localZoneID = ZoneId.systemDefault(); //local zoneId
    DbConnection db = new DbConnection();
    Connection connection = db.connection();


    public Timestamp localToSqlTimestamp(LocalTime time, LocalDate date){
        /** Add local time to date in order to have LocalDateTime format inorder to use Zoned */
        LocalDateTime dateTime = LocalDateTime.of(date,time);
        /** Convert Local to UTC (Coordinated Universal Time) */
        ZonedDateTime dateTimeUTC = dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

        /** SQL Timestamp */
        Timestamp timestamp = Timestamp.valueOf(dateTimeUTC.toLocalDateTime());
        return timestamp;
    }

    public Timestamp sqlToLocalTimestamp(Timestamp timestamp){
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        /** Convert Local to UTC (Coordinated Universal Time) */
        ZonedDateTime dateTimeUTC = dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(localZoneID);

        /** SQL Timestamp */
        Timestamp ts = Timestamp.valueOf(dateTimeUTC.toLocalDateTime());
        return ts;
//        /** Add local time to date in order to have LocalDateTime format inorder to use Zoned */
//        LocalDateTime dateTime = timestamp.toLocalDateTime();
//        /** Convert Local to UTC (Coordinated Universal Time) */
//        ZonedDateTime dateTimeUTC = dateTime.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
//
//        /** SQL Timestamp */
//        Timestamp timestamp = Timestamp.valueOf(dateTimeUTC.toLocalDateTime());
//
//        return timestamp;
    }



    private boolean noScheduleConflicts(Timestamp start, Timestamp end, int customerId, int appointmentId) throws SQLException {
        try{
            //edited appointment
            int userId = SessionLog.getLoggedOnUser();

            PreparedStatement pst = connection.prepareStatement(
                    "SELECT * FROM appointments "
                            + "WHERE (? BETWEEN startTime AND endTime OR ? BETWEEN startTime AND endTime OR ? < start AND ? > end) "
                            + "AND (userId = ? AND id != ?) OR (customer = ? AND id != ?)");
            pst.setTimestamp(1, start);
            pst.setTimestamp(2, end);
            pst.setTimestamp(3, start);
            pst.setTimestamp(4, end);
            pst.setString(5, String.valueOf(userId));
            pst.setInt(6, appointmentId);
            pst.setInt(7, customerId);
            pst.setInt(8, appointmentId);
            System.out.println(pst.toString());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean  noScheduleConflicts(Timestamp start, Timestamp end, int customerId) throws SQLException {
        try{
            //edited appointment
            int userId = SessionLog.getLoggedOnUser();

            PreparedStatement pst = connection.prepareStatement(
                    "SELECT * FROM appointments "
                            + "WHERE (? BETWEEN startTime AND endTime OR ? BETWEEN startTime AND endTime OR ? < start AND ? > end) "
                            + "AND (userId = ? ) OR (customer = ?)");
            pst.setTimestamp(1, start);
            pst.setTimestamp(2, end);
            pst.setTimestamp(3, start);
            pst.setTimestamp(4, end);
            pst.setString(5, String.valueOf(userId));
            pst.setInt(6, customerId);
            System.out.println(pst.toString());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public Boolean validateBusinessHours(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDate apptDate) {
        // (8am to 10pm EST, Not including weekends)
        // Turn into zonedDateTimeObject, so we can evaluate whatever time was entered in user time zone against EST

        ZonedDateTime startZonedDateTime = ZonedDateTime.of(startDateTime, SessionLog.getUserTimeZone());
        ZonedDateTime endZonedDateTime = ZonedDateTime.of(endDateTime, SessionLog.getUserTimeZone());

        ZonedDateTime startBusinessHours = ZonedDateTime.of(apptDate, LocalTime.of(8,0),
                ZoneId.of("America/New_York"));
        ZonedDateTime endBusinessHours = ZonedDateTime.of(apptDate, LocalTime.of(22, 0),
                ZoneId.of("America/New_York"));

        // If startTime is before or after business hours
        // If end time is before or after business hours
        // if startTime is after endTime - these should cover all possible times entered and validate input.
        if (startZonedDateTime.isBefore(startBusinessHours) | startZonedDateTime.isAfter(endBusinessHours) |
                endZonedDateTime.isBefore(startBusinessHours) | endZonedDateTime.isAfter(endBusinessHours) |
                startZonedDateTime.isAfter(endZonedDateTime)) {
            return false;

        }
        else {
            return true;
        }

    }
}

