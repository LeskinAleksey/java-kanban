package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import util.GsonProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = GsonProvider.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case GET_EPIC:
                handleGetEpic(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
                break;
            case POST_EPIC:
                handlePostEpic(exchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        Collection<Epic> epics = manager.getEpicsList();
        String response = gson.toJson(epics);
        sendText(exchange, response);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange.getRequestURI().getPath());
        Epic epic = manager.getEpic(id);

        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        String response = gson.toJson(epic);
        sendText(exchange, response);
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange.getRequestURI().getPath());
        Epic epic = manager.getEpic(id);

        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        ArrayList<Subtask> subtasks = manager.getEpicSubtasks(id);
        String response = gson.toJson(subtasks);
        sendText(exchange, response);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);

            if (epic.getId() == 0) {
                manager.createEpic(epic);
                sendText(exchange, gson.toJson(epic));
            } else {
                manager.changeEpic(epic, epic.getId());
                sendText(exchange, gson.toJson(epic));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendNotFound(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange.getRequestURI().getPath());
        boolean exists = manager.getEpic(id) != null;

        if (!exists) {
            sendNotFound(exchange);
            return;
        }

        manager.deleteEpic(id);
        sendText(exchange, "Эпик удален");
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
            return Endpoint.GET_EPICS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3 && !pathParts[2].equals("subtasks")) {
            return Endpoint.GET_EPIC;
        } else if (requestMethod.equals("GET") && pathParts.length == 4 && pathParts[3].equals("subtasks")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        } else if (requestMethod.equals("POST") && pathParts.length == 2) {
            return Endpoint.POST_EPIC;
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3) {
            return Endpoint.DELETE_EPIC;
        }
        return Endpoint.UNKNOWN;
    }
}
