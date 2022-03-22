package Main.Utilities;
import javafx.scene.control.Alert;
/**
 * Alert Class
 * Prints out English or French login Error.
 */
public class Alerts {

    /**
     * Login Error
     */
    public static void loginError(Boolean isEnglish) {
        if (isEnglish) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Error");
            alert.setContentText("Incorrect username or password.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Erruer");
            alert.setContentText("Identifiant ou mot de passe incorrect.");
            alert.showAndWait();
        }
    }


//    public static void showError(String error) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("");
//        alert.setHeaderText("Error");
//        alert.setContentText(error);
//        alert.showAndWait();
//    }
}