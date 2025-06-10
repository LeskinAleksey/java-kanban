import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> viewHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        int maxNumTasks = 10;
        if (viewHistory.size() < maxNumTasks) {
            viewHistory.add(task);
            return;
        }

        for (int i = 0; i < maxNumTasks - 1; i++) {
            viewHistory.set(i, viewHistory.get(i + 1));
        }
        viewHistory.remove(maxNumTasks - 1);
        viewHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return viewHistory;
    }
}
