import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.Managers;

class SubTaskTest {
    static TaskManager manager;

    @BeforeAll
    static void init() {
        manager = Managers.getDefault();
    }

    @AfterEach
    void clear() {
        manager.deleteAllSubtasks();
    }

    @Test
    void subtaskWithSameIdIsEqual() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId());
        manager.createSubtask(subtask);
        Assertions.assertEquals(manager.getSubtask(subtask.getId()), manager.getSubtask(subtask.getId()));
    }

    @Test
    void changeSubtaskNameAndDescription() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId());
        manager.createSubtask(subtask);
        Subtask changedSubtask = new Subtask("New name", "New description", epic.getId());
        manager.changeSubtask(changedSubtask, subtask.getId());
        Assertions.assertEquals(changedSubtask.getName(), manager.getSubtask(subtask.getId()).getName());
        Assertions.assertEquals(changedSubtask.getDescription(), manager.getSubtask(subtask.getId()).getDescription());
    }

    @Test
    void addSubtasks() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId());
        manager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("subtask2", "Description2", epic.getId());
        manager.createSubtask(subtask2);
        Assertions.assertEquals(2, manager.getSubtasksList().size());
    }

    @Test
    void deleteSubtasks() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId());
        manager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("subtask2", "Description2", epic.getId());
        manager.createSubtask(subtask2);
        manager.deleteAllSubtasks();
        Assertions.assertEquals(0, manager.getSubtasksList().size());
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId());
        manager.createSubtask(subtask);
        manager.deleteSubtask(subtask.getId());
        Assertions.assertTrue(manager.getSubtasksList().isEmpty());
    }
}