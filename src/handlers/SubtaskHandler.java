package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import interfaces.TaskManager;
import model.Subtask;
import util.GsonProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = GsonProvider.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASK:
                handleGetSubtask(exchange);
                break;
            case GET_SUBTASKS_BY_EPIC:
                handleGetSubtasksByEpic(exchange);
                break;
            case POST_SUBTASK:
                handlePostSubtask(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        Collection<Subtask> subtasks = manager.getSubtasksList();
        String response = gson.toJson(subtasks);
        sendText(exchange, response);
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange.getRequestURI().getPath());
        Subtask subtask = manager.getSubtask(id);

        if (subtask == null) {
            sendNotFound(exchange);
            return;
        }

        String response = gson.toJson(subtask);
        sendText(exchange, response);
    }

    private void handleGetSubtasksByEpic(HttpExchange exchange) throws IOException {
        int epicId = extractIdFromPath(exchange.getRequestURI().getPath());
        ArrayList<Subtask> subtasks = manager.getEpicSubtasks(epicId);

        if (subtasks == null) {
            sendNotFound(exchange);
            return;
        }

        String response = gson.toJson(subtasks);
        sendText(exchange, response);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            if (subtask.getId() == 0) {
                manager.createSubtask(subtask);
                sendText(exchange, gson.toJson(subtask));
            } else {
                manager.changeSubtask(subtask, subtask.getId());
                sendText(exchange, gson.toJson(subtask));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange.getRequestURI().getPath());
        boolean exists = manager.getSubtask(id) != null;

        if (!exists) {
            sendNotFound(exchange);
            return;
        }

        manager.deleteSubtask(id);
        sendText(exchange, "Подзадача удалена");
    }

    private int extractIdFromPath(String path) {
        String[] parts = path.split("/");
        try {
            return Integer.parseInt(parts[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET") && pathParts.length == 2) {
            return Endpoint.GET_SUBTASKS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3 && !pathParts[2].equals("epic")) {
            return Endpoint.GET_SUBTASK;
        } else if (requestMethod.equals("GET") && pathParts.length == 4 && pathParts[3].equals("epic")) {
            return Endpoint.GET_SUBTASKS_BY_EPIC;
        } else if (requestMethod.equals("POST") && pathParts.length == 2) {
            return Endpoint.POST_SUBTASK;
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3) {
            return Endpoint.DELETE_SUBTASK;
        }
        return Endpoint.UNKNOWN;
    }
}
