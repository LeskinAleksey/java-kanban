package services;

import interfaces.HistoryManager;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> viewHistory = new ArrayList<>();
    private static final int MAX_NUM_TASKS = 10;

    @Override
    public void add(Task task) {
        if (viewHistory.size() < MAX_NUM_TASKS) {
            viewHistory.add(task);
            return;
        }

        viewHistory.removeFirst();
        viewHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(viewHistory);
    }
}
