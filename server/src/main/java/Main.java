import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var testServer = new Server();
        testServer.run(8080);
    }
}