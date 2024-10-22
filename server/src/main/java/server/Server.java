package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import service.ClearService;
import service.GameService;
import service.ServiceException;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {

    private final MemoryUserDAO USER_DAO = new MemoryUserDAO();
    private final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    private final MemoryGameDAO GAME_DAO = new MemoryGameDAO();
    private final UserService USER_SERVICE = new UserService(USER_DAO, AUTH_DAO);
    private final ClearService CLEAR_SERVICE = new ClearService(USER_DAO, AUTH_DAO, GAME_DAO);
    private final GameService GAME_SERVICE = new GameService(AUTH_DAO, GAME_DAO);

    private final Gson serializer = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.delete("/db", this::clear);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);
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
        var result = USER_SERVICE.register(newUser);
        return serializer.toJson(result);
    }

    private String login(Request req, Response res) throws Exception {
        var loginRequest = serializer.fromJson(req.body(), UserData.class);
        var result = USER_SERVICE.login(loginRequest);
        return serializer.toJson(result);
    }

    private String logout(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        USER_SERVICE.logout(authToken);
        return "";
    }

    private String createGame(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        var createGameRequest = serializer.fromJson(req.body(), CreateGameRequest.class);
        var result = GAME_SERVICE.createGame(createGameRequest.gameName(), authToken);
        return serializer.toJson(result);
    }

    private String listGames(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        var result = GAME_SERVICE.listGames(authToken);
        return serializer.toJson(result);
    }

    private String joinGame(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        var joinGameRequest = serializer.fromJson(req.body(), JoinGameRequest.class);
        GAME_SERVICE.joinGame(joinGameRequest.gameID(), joinGameRequest.playerColor(), authToken);
        return "";

    }

    private String clear(Request req, Response res) throws Exception {
        CLEAR_SERVICE.clear();
        return "";
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
