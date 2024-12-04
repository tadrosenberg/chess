package ui;

import com.google.gson.Gson;
import exception.ServiceException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final ServerMessageObserver observer;
    private final Gson gson = new Gson();

    public WebSocketFacade(String serverUrl, ServerMessageObserver observer) throws ServiceException {
        try {
            serverUrl = serverUrl.replace("http", "ws");
            URI uri = new URI(serverUrl + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                            observer.notify(loadGameMessage);
                        }
                        case NOTIFICATION -> {
                            NotificationMessage notification = gson.fromJson(message, NotificationMessage.class);
                            observer.notify(notification);
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                            observer.notify(errorMessage);
                        }
                    }
                }
            });
        } catch (IOException | URISyntaxException | DeploymentException e) {
            throw new ServiceException(500, "Failed to connect WebSocket: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("WebSocket connection established.");
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public void sendCommand(UserGameCommand command) throws ServiceException {
        try {
            String json = gson.toJson(command); // Serialize command to JSON
            this.session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            throw new ServiceException(500, "Failed to send WebSocket command: " + e.getMessage());
        }
    }

    public void close() throws ServiceException {
        try {
            this.session.close();
        } catch (IOException e) {
            throw new ServiceException(500, "Failed to close WebSocket: " + e.getMessage());
        }
    }

}