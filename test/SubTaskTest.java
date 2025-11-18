import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

class SubTaskTest extends BaseTest {
    static TaskManager manager;
    private LocalDateTime currentDateTime = LocalDateTime.now();
    private Duration threeHoursDuration = Duration.ofMinutes(180L);

    @BeforeAll
    static void init() {
        manager = Managers.getDefaultTaskManager();
    }

    @AfterEach
    void clear() {
        manager.deleteAllSubtasks();
    }

    @Test
    void subtaskWithSameIdIsEqual() {
        Epic epic = new Epic("Epic1", "Description1", getDuration(), getStartTime(0));
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId(), getDuration(), getStartTime(1));
        manager.createSubtask(subtask);
        Assertions.assertEquals(manager.getSubtask(subtask.getId()), manager.getSubtask(subtask.getId()));
    }

    @Test
    void changeSubtaskNameAndDescription() {
        Epic epic = new Epic("Epic1", "Description1", getDuration(), getStartTime(2));
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId(), getDuration(), getStartTime(3));
        manager.createSubtask(subtask);
        Subtask changedSubtask = new Subtask("New name", "New description", epic.getId(), getDuration(), getStartTime(4));
        manager.changeSubtask(changedSubtask, subtask.getId());
        Assertions.assertEquals(changedSubtask.getName(), manager.getSubtask(subtask.getId()).getName());
        Assertions.assertEquals(changedSubtask.getDescription(), manager.getSubtask(subtask.getId()).getDescription());
    }

    @Test
    void addSubtasks() {
        Epic epic = new Epic("Epic1", "Description1", getDuration(), getStartTime(5));
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId(), getDuration(), getStartTime(6));
        manager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("subtask2", "Description2", epic.getId(), getDuration(), getStartTime(7));
        manager.createSubtask(subtask2);
        Assertions.assertEquals(2, manager.getSubtasksList().size());
    }

    @Test
    void deleteSubtasks() {
        Epic epic = new Epic("Epic1", "Description1", getDuration(), getStartTime(8));
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId(), getDuration(), getStartTime(9));
        manager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("subtask2", "Description2", epic.getId(), getDuration(), getStartTime(10));
        manager.createSubtask(subtask2);
        manager.deleteAllSubtasks();
        Assertions.assertEquals(0, manager.getSubtasksList().size());
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic("Epic1", "Description1", getDuration(), getStartTime(11));
        manager.createEpic(epic);
        Subtask subtask = new Subtask("subtask1", "Description1", epic.getId(), getDuration(), getStartTime(12));
        manager.createSubtask(subtask);
        manager.deleteSubtask(subtask.getId());
        Assertions.assertTrue(manager.getSubtasksList().isEmpty());
    }
}