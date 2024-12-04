package ui;

import websocket.messages.ServerMessage;
import exception.ServiceException;

public interface ServerMessageObserver {
    
    void notify(ServerMessage message) throws ServiceException;
}