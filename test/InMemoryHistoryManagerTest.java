import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.Managers;

public class InMemoryHistoryManagerTest {
    static TaskManager manager;

    @BeforeAll
    static void init() {
        manager = Managers.getDefaultTaskManager();
    }

    @AfterEach
    void clear() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();
    }

    @Test
    void createViewHistoryEntryAfterTaskViewTest() {
        Task task = new Task("Task1", "Description1");
        manager.createTask(task);
        manager.getTask(1);
        Assertions.assertEquals(1, manager.getHistory().size());
        Assertions.assertEquals(1, manager.getHistory().getLast().getId());
    }

    @Test
    void createViewHistoryEntryAfterEpicViewTest() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        manager.getEpic(1);
        Assertions.assertEquals(1, manager.getHistory().size());
        Assertions.assertEquals(1, manager.getHistory().getLast().getId());
    }

    @Test
    void createViewHistoryEntryAfterSubtaskViewTest() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId());
        manager.createSubtask(subtask);
        manager.getSubtask(2);
        Assertions.assertEquals(2, manager.getHistory().getLast().getId());
    }
}
