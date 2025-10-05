package services;

import interfaces.HistoryManager;
import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> viewHistory = new HashMap<>();

    private void linkLast(Task task) {
        int taskId = task.getId();
        Node newNode = new Node<>(task);
        if (viewHistory.isEmpty()) {
            head = newNode;
            tail = newNode;
            viewHistory.put(taskId, head);
            return;
        }

        if (viewHistory.containsKey(taskId)) {
            Node currNode = viewHistory.get(taskId);

            removeNode(currNode);
        }
        tail.next = newNode;
        newNode.prev = tail;
        tail = newNode;
        viewHistory.put(taskId, newNode);
    }

    private void removeNode(Node node) {
        if (node.next == null && node.prev != null) {
            node.prev.next = null;
            tail = node.prev;
        } else if (node.prev == null && node.next != null) {
            node.next.prev = null;
            head = node.next;
        } else if (node.prev != null && node.next != null) {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }
        int taskId = ((Task) node.data).getId();


        viewHistory.remove(taskId);
    }

    private List<Task> getTasks() {
        List<Task> viewHistoryList = new ArrayList<>();
        if (viewHistory.isEmpty()) {
            return viewHistoryList;
        }
        Node pointer = head;
        Task firstTask = (Task) head.data;
        viewHistoryList.add(firstTask);
        while (true) {
            if (pointer.next == null) {
                break;
            }
            pointer = pointer.next;
            Task task = (Task) pointer.data;
            viewHistoryList.add(task);
        }
        return viewHistoryList;
    }

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (viewHistory.containsKey(id)) {
            Node node = viewHistory.get(id);
            removeNode(node);
        }
    }
}
