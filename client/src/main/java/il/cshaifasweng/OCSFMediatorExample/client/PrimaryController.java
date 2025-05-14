package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import il.cshaifasweng.OCSFMediatorExample.entities.GameMove;

public class PrimaryController {

	@FXML private Button button00;
	@FXML private Button button01;
	@FXML private Button button02;
	@FXML private Button button10;
	@FXML private Button button11;
	@FXML private Button button12;
	@FXML private Button button20;
	@FXML private Button button21;
	@FXML private Button button22;
	@FXML private Button exitBtn;
	@FXML private Button restartBtn;
	@FXML private Label statusLabel;

	private Map<String, Button> buttonMap = new HashMap<>();
	private boolean isMyTurn = false;
	private String playerSymbol;
	private SimpleClient client;

	@FXML
	public void initialize() {
		buttonMap.put("button00", button00);
		buttonMap.put("button01", button01);
		buttonMap.put("button02", button02);
		buttonMap.put("button10", button10);
		buttonMap.put("button11", button11);
		buttonMap.put("button12", button12);
		buttonMap.put("button20", button20);
		buttonMap.put("button21", button21);
		buttonMap.put("button22", button22);

		statusLabel.setText("Waiting for player...");
	}

	@FXML
	void handleButtonClick(ActionEvent event) {
		if (!isMyTurn) return;

		Button clicked = (Button) event.getSource();
		String id = clicked.getId();
		int row = Character.getNumericValue(id.charAt(6));
		int col = Character.getNumericValue(id.charAt(7));

		try {
			GameMove move = new GameMove(row, col, playerSymbol);
			client.sendToServer(move);
			isMyTurn = false;
			statusLabel.setText("Waiting for opponent...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@FXML
	void handleExit(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	void handleRestart(ActionEvent event) {
		try {
			client.sendToServer("restart"); // שלח לשרת בקשת אתחול
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetBoard() {
		Platform.runLater(() -> {
			for (Button button : buttonMap.values()) {
				button.setText("");
				button.setDisable(false);
			}
			statusLabel.setText("Game restarted. Waiting for turn...");
		});
	}

	public void applyMove(GameMove move) {
		Platform.runLater(() -> {
			String key = "button" + move.getRow() + move.getCol();
			Button target = buttonMap.get(key);
			target.setText(move.getPlayer());
			target.setDisable(true);
		});
	}

	public void setPlayerSymbol(String symbol) {
		this.playerSymbol = symbol;
	}

	public void setMyTurn(boolean isMyTurn) {
		this.isMyTurn = isMyTurn;
		Platform.runLater(() -> {
			statusLabel.setText(isMyTurn ? "It's your turn!" : "Waiting for opponent...");
		});
	}

	public void handleMessageFromServer(Object msg) {
		if (msg instanceof GameMove) {
			applyMove((GameMove) msg);
		} else if (msg instanceof String) {
			String message = (String) msg;
			if (message.startsWith("start:")) {
				playerSymbol = message.substring(6);
				isMyTurn = playerSymbol.equals("X");
				setMyTurn(isMyTurn);
			} else if (message.startsWith("turn:")) {
				boolean turn = Boolean.parseBoolean(message.substring(5));
				setMyTurn(turn);
			} else if (message.equals("You win!")) {
				Platform.runLater(() -> {
					statusLabel.setText("🎉 You win!");
					disableAllButtons();
				});
			} else if (message.equals("You lose!")) {
				Platform.runLater(() -> {
					statusLabel.setText("😢 You lose!");
					disableAllButtons();
				});
			} else if (message.equals("Draw!")) {
				Platform.runLater(() -> {
					statusLabel.setText("🤝 It's a draw!");
					disableAllButtons();
				});
			} else if (message.equals("restart")) {
				resetBoard();  // ← כאשר השרת שולח את "restart"
			} else {
				Platform.runLater(() -> statusLabel.setText(message));
			}
		}
	}

	private void disableAllButtons() {
		for (Button button : buttonMap.values()) {
			button.setDisable(true);
		}
	}

	public void setClient(SimpleClient client) {
		this.client = client;
	}
}
