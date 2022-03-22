package Main.Utilities;
/**
 * Class connect application to MySQL Database and establishes a connection.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

        private int port = 3306;
        // DB credentials stored separately in sqlDatabases.properties, for security reasons.
        private String dbName = "client_schedule";
        private String userName = "sqlUser";
        private String password = "Passw0rd!";
        private String jdbcDriver = "com.mysql.jdbc.Driver";
        private String serverUrl = "jdbc:mysql://localhost:3306/" + dbName;
        private Connection databaseConnection;

        public void setDbName(String dbNameInput) {
            this.dbName = dbNameInput;
        }
        public void setDbUserName(String dbUserNameInput) {
            this.userName = dbUserNameInput;
        }
        public  void setDbPassword(String dbPasswordInput) {
            password = dbPasswordInput;
        }


        public void connectDB() {
//            String Data
            try {
                Class.forName(jdbcDriver);
                this.databaseConnection = DriverManager.getConnection(serverUrl, userName, password);
            }
            catch (SQLException error) {
                System.out.println("here");
                System.out.println(error.getMessage());
            }
            catch (ClassNotFoundException error) {
                System.out.println("2nd here");
                System.out.println(error.getMessage());
            }

        }


        public  Connection connection() {
            connectDB();
            return this.databaseConnection;
        }


}
