package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.ServiceException;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(userDAO, authDAO);

    private final Gson serializer = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.exception(Exception.class, this::exceptionHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String register(Request req, Response res) throws Exception {
        var newUser = serializer.fromJson(req.body(), UserData.class);
        var result = userService.register(newUser);
        return serializer.toJson(result);
    }

    private void exceptionHandler(Exception ex, Request req, Response res) {
        if (ex instanceof ServiceException serviceEx) {
            res.status(serviceEx.getStatusCode());
            res.body(serializer.toJson(Map.of("message", serviceEx.getMessage())));
        } else {
            res.status(500);
            res.body(serializer.toJson(Map.of("message", "Internal server error: " + ex.getMessage())));
        }
    }


}
