package server;

import com.google.gson.Gson;
import exception.ServiceException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;  // Store the server URL
    private final Gson gson = new Gson();  // Gson for JSON serialization and deserialization

    // Constructor to initialize the server URL
    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
