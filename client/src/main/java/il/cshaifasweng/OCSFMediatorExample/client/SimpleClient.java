package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

public class SimpleClient extends AbstractClient {

	public static String ip = "127.0.0.1";
	public static int port = 3000;

	private static SimpleClient client = null;

	private PrimaryController controller;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(ip, port);
		}
		return client;
	}

	public void setController(PrimaryController controller) {
		this.controller = controller;
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Platform.runLater(() -> {
			if (controller != null) {
				controller.handleMessageFromServer(msg);
			}
		});
	}
	public static SimpleClient createClient(String ip, int port) {
		client = new SimpleClient(ip, port);
		return client;
	}

}
