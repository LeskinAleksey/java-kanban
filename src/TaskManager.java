import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Collection<Task> getTasksList();

    Collection<Subtask> getSubtasksList();

    Collection<Epic> getEpicsList();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTask(int taskId);

    Subtask getSubtask(int subtaskId);

    Epic getEpic(int epicId);

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    void changeTask(Task task, int id);

    void changeSubtask(Subtask subtask, int subtaskId);

    void changeEpic(Epic epic, int epicId);

    void deleteTask(int taskId);

    void deleteSubtask(int taskId);

    void deleteEpic(int taskId);

    ArrayList<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
}
