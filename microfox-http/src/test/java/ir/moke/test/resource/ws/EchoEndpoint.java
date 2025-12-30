package ir.moke.test.resource.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint("/echo")
public class EchoEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Socket Opened");
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        System.out.print(msg);
        try {
            session.getBasicRemote().sendText("Response : Hello");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose() {
        System.out.println("Socket closed");
    }

    @OnError
    public void onError(Throwable t) {
        if (t != null) System.out.println("Error : " + t.getMessage());
    }
}
