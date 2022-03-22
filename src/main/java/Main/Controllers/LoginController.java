package Main.Controllers;
/**
 * <h6>Login</h6>
 * <p>The class provides functionality for the login screen.
 *</p>
 * */
import Main.Models.Appointment;
import Main.Models.Database;
import Main.Models.SessionLog;
import Main.Models.User;
import Main.Scheduler;
import Main.Utilities.Alerts;
import javafx.collections.ObservableList;
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
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    Stage stage;
    Parent scene;


// Import JavaFx
    @ FXML
    private TextField userNameTextBox;
    @FXML
    private PasswordField userPasswordTextBox;
    @ FXML
    private Button loginButton;
    @ FXML
    private Button clearButton;
    @ FXML
    private Button exitButton;
    @ FXML
    private Label zoneLabel;
    @ FXML
    private Label usernameLabel;
    @ FXML
    private Label passwordLabel;

//    Import Classes

    Database db = new Database();
    Alerts alerter = new Alerts();
    DashboardController controller = new DashboardController();
    Boolean isEnglish = true;

    /** Login */
    public void login(ActionEvent event) throws IOException, SQLException {
        try {
            String userName = userNameTextBox.getText();
            String password = userPasswordTextBox.getText();

            User user = new User();
            boolean authenticated = user.login(userName, password);

            if(!authenticated){
                alerter.loginError(isEnglish);
                return;
            }
            stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
            scene = FXMLLoader.load(Scheduler.class.getResource("Dashboard.fxml"));
            stage.setScene(new Scene(scene));

            if (authenticated) {
                // Get appointments in 15 minutes and display notification if there is.
                ObservableList<Appointment> upcoming = db.getAppointmentsWithin15();

                if (upcoming.size() > 0) {
                    for (Appointment app : upcoming) {
                        String message = "You have an appointment soon: " + app.getTitle() + " with " + app.getCustomerID() + " At: " +
                                app.getStartTime().toLocalDateTime().toLocalTime().toString();
                        ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                        Alert invalidInput = new Alert(Alert.AlertType.WARNING, message, ok);
                        invalidInput.showAndWait();
                    }
                }

                stage.show();

            } else {
                alerter.loginError(isEnglish);
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }


    /** Closes Application */
    @FXML
    public void onExit(ActionEvent event) {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        stage.close();
    }

    /** Navigation to other pages */
    /** To Dashboard **/
    public void toDashboard(ActionEvent event) throws IOException {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(Scheduler.class.getResource("Dashboard.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }





    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Locale userLocale = Locale.getDefault();
        zoneLabel.setText(ZoneId.systemDefault().toString());

        System.out.println(userLocale.getLanguage());
        if(userLocale.getLanguage().equals("fr")){
            isEnglish = false;
            loginButton.setText("Connexion");
            exitButton.setText("sortir");
            usernameLabel.setText("Nom d'utilisateur");
            passwordLabel.setText("Le mot de passe");
        }

    }

}


