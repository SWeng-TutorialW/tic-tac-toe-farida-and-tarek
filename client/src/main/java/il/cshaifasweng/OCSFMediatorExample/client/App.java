package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // הרשמה ל־EventBus
        EventBus.getDefault().register(this);

        // טען את init.fxml במקום primary.fxml
        FXMLLoader loader = new FXMLLoader(App.class.getResource("init.fxml"));
        Parent root = loader.load();

        // אתחול InitController
        InitController initController = loader.getController();
        initController.setStage(stage); // שמירת stage לצורך מעבר בהמשך

        // הצגת מסך פתיחה
        scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        scene.setRoot(root);
    }

    @Override
    public void stop() throws Exception {
        EventBus.getDefault().unregister(this);
        super.stop();
    }

    @Subscribe
    public void onWarningEvent(WarningEvent event) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING,
                    String.format("Message: %s\nTimestamp: %s\n",
                            event.getWarning().getMessage(),
                            event.getWarning().getTime().toString()));
            alert.show();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
