package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InitController {

    @FXML private TextField ipField;
    @FXML private TextField portField;

    private Stage stage;  // ה-stage שמועבר מ-App

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void connect() {
        String ip = ipField.getText().trim();
        String portText = portField.getText().trim();

        if (ip.isEmpty() || portText.isEmpty()) {
            showError("Please enter both IP and Port.");
            return;
        }

        try {
            int port = Integer.parseInt(portText);
            SimpleClient client = SimpleClient.createClient(ip, port);
            client.openConnection();
            client.sendToServer("add client");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
            Parent root = loader.load();

            PrimaryController controller = loader.getController();
            controller.setClient(client);
            client.setController(controller);

            Scene scene = new Scene(root, 640, 480);
            stage.setScene(scene);   // ← מחליף למסך הראשי של המשחק
            stage.show();

        } catch (NumberFormatException e) {
            showError("Invalid port number.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to connect to server.");
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message);
            alert.showAndWait();
        });
    }
}
