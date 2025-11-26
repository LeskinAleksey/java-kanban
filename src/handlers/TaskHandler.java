package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import interfaces.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        Endpoint endpoint;

        try {
            endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        } catch (Exception e) {
            sendInternalServerError(exchange);
            return;
        }

        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK:
                handleGetTask(exchange);
                break;
            case POST_TASK:
                handlePostTask(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        Collection<Task> tasks = manager.getTasksList();
        String response = gson.toJson(tasks);
        sendText(exchange, response);
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange.getRequestURI().getPath());
        Task task = manager.getTask(id);

        if (task == null) {
            sendNotFound(exchange);
            return;
        }

        String response = gson.toJson(task);
        sendText(exchange, response);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);

            if (task.getId() == 0) {
                Task created = manager.createTask(task);
                if (created == null) {
                    sendHasOverlaps(exchange);
                } else {
                    sendText(exchange, gson.toJson(created));
                }
            } else {
                boolean updated = manager.changeTask(task, task.getId());
                if (updated) {
                    sendText(exchange, gson.toJson(task));
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange);
        }
    }


    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        int id = extractIdFromPath(exchange.getRequestURI().getPath());
        boolean deleted = manager.deleteTask(id);

        if (deleted) {
            sendText(exchange, "Задача удалена");
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        manager.deleteAllTasks();
        sendText(exchange, "Все задачи удалены");
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
            return Endpoint.GET_TASKS;
        } else if (requestMethod.equals("GET") && pathParts.length == 3) {
            return Endpoint.GET_TASK;
        } else if (requestMethod.equals("POST") && pathParts.length == 2) {
            return Endpoint.POST_TASK;
        } else if (requestMethod.equals("DELETE") && pathParts.length == 3) {
            return Endpoint.DELETE_TASK;
        }
        return Endpoint.UNKNOWN;
    }
}
