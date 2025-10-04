package services;

import interfaces.HistoryManager;
import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Node> viewHistory = new ArrayList<>();
    private final Map<Integer, Integer> taskIdAndHistoryIdxMap = new HashMap<>();

    private void linkLast(Task task) {
        int taskId = task.getId();
        boolean keyExists = taskIdAndHistoryIdxMap.containsKey(taskId);
        if (keyExists) {
            int taskIdx = taskIdAndHistoryIdxMap.get(taskId);
            Node currNode = viewHistory.get(taskIdx);

            removeNode(currNode);
        }
        Node<Task> node = new Node<>(task);
        if (viewHistory.size() > 0) {
            Node prevNode = viewHistory.getLast();
            node.prev = prevNode;
            prevNode.next = node;
        }
        viewHistory.add(node);
        taskIdAndHistoryIdxMap.put(taskId, viewHistory.size() - 1);
    }

    private void removeNode(Node node) {
        Task task = (Task) node.data;
        int taskId = task.getId();
        int taskHistoryIdx = taskIdAndHistoryIdxMap.get(taskId);
        if (node.next != null) {
            node.next.prev = node.prev;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        }

        viewHistory.remove(taskHistoryIdx);
        taskIdAndHistoryIdxMap.remove(taskId);
        for (Map.Entry<Integer, Integer> entry : taskIdAndHistoryIdxMap.entrySet()) {
            int value = entry.getValue();
            if (value > taskHistoryIdx) {
                entry.setValue(value - 1);
            }
        }
    }

    private List<Task> getTasks() {
        List viewHistoryList = new ArrayList<>();

        for (Node node : viewHistory) {
            viewHistoryList.add(node.data);
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
        if (taskIdAndHistoryIdxMap.containsKey(id)) {
            int nodeIdx = taskIdAndHistoryIdxMap.get(id);
            Node node = viewHistory.get(nodeIdx);
            removeNode(node);
        }
    }
}
