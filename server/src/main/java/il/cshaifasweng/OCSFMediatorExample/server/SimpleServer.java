// === SimpleServer.java ===
package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.GameMove;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> subscribers = new ArrayList<>();
	private String[][] board = new String[3][3];
	private int currentPlayerIndex = 0;

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (msg instanceof String) {
			String str = (String) msg;

			if (str.equals("add client")) {
				SubscribedClient sub = new SubscribedClient(client);
				subscribers.add(sub);
				try {
					client.sendToClient("client added successfully");
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (subscribers.size() == 2) {
					startGame();
				}
			}

			else if (str.equals("restart")) {
				resetGame();                   // אתחול מצב הלוח והאינדקס
				broadcastMessage("restart");  // הודעה ללקוחות לאתחל את הלוח
				startGame();                  // התחלה מחדש של המשחק עם X/O ותור
				return;
			}
		}

		else if (msg instanceof GameMove) {
			GameMove move = (GameMove) msg;
			int row = move.getRow();
			int col = move.getCol();
			String player = move.getPlayer();

			if (board[row][col] == null && client == subscribers.get(currentPlayerIndex).getClient()) {
				board[row][col] = player;
				broadcastMove(move);

				if (checkWin(player)) {
					broadcastWin(player, client);  // ניצחון
					return;
				} else if (checkDraw()) {
					broadcastMessage("Draw!");     // תיקו
					return;
				}

				currentPlayerIndex = 1 - currentPlayerIndex;
				updateTurns();
			}
		}
	}


	private void startGame() {
		board = new String[3][3];
		currentPlayerIndex = 0;
		try {
			subscribers.get(0).getClient().sendToClient("start:X");
			subscribers.get(1).getClient().sendToClient("start:O");
		} catch (IOException e) {
			e.printStackTrace();
		}
		updateTurns();
	}

	private void updateTurns() {
		for (int i = 0; i < subscribers.size(); i++) {
			try {
				subscribers.get(i).getClient().sendToClient("turn:" + (i == currentPlayerIndex));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void broadcastMove(GameMove move) {
		for (SubscribedClient sub : subscribers) {
			try {
				sub.getClient().sendToClient(move);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void broadcastMessage(String message) {
		for (SubscribedClient sub : subscribers) {
			try {
				sub.getClient().sendToClient(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void broadcastWin(String winnerSymbol, ConnectionToClient winnerClient) {
		for (SubscribedClient sub : subscribers) {
			try {
				String msg = sub.getClient().equals(winnerClient) ? "You win!" : "You lose!";
				sub.getClient().sendToClient(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getPlayerSymbol(SubscribedClient sub) {
		int index = subscribers.indexOf(sub);
		return index == 0 ? "X" : "O";
	}

	private void resetGame() {
		board = new String[3][3];
		startGame();
	}

	private boolean checkWin(String player) {
		for (int i = 0; i < 3; i++) {
			if ((player.equals(board[i][0]) && player.equals(board[i][1]) && player.equals(board[i][2])) ||
					(player.equals(board[0][i]) && player.equals(board[1][i]) && player.equals(board[2][i]))) {
				return true;
			}
		}
		return (player.equals(board[0][0]) && player.equals(board[1][1]) && player.equals(board[2][2])) ||
				(player.equals(board[0][2]) && player.equals(board[1][1]) && player.equals(board[2][0]));
	}

	private boolean checkDraw() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == null) return false;
			}
		}
		return true;
	}
}
