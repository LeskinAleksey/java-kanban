package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import interfaces.TaskManager;
import util.GsonProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected final TaskManager manager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = GsonProvider.getGson();
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        h.close();
    }

    protected void sendHasOverlaps(HttpExchange h) throws IOException {
        h.sendResponseHeaders(409, 0);
        h.close();
    }

    protected void sendInternalServerError(HttpExchange h) throws IOException {
        h.sendResponseHeaders(500, 0);
        h.close();
    }
}
