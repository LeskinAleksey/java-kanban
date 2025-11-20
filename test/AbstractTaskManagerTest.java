import enums.Status;
import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractTaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
    protected Duration oneHourDuration = Duration.ofMinutes(60L);
    protected LocalDateTime getStartTime(int taskIndex) {
        return currentDateTime.plusHours(taskIndex * 3L);
    }

    protected Duration getDuration() {
        return Duration.ofHours(2);
    }


    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    @AfterEach
    void tearDown() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
    }

    @Test
    void testCreateAndGetTask() {
        Task task = new Task("Test Task", "Test Description", oneHourDuration, currentDateTime);
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTask(1);
        assertNotNull(retrievedTask);
        assertEquals("Test Task", retrievedTask.getName());
        assertEquals(Status.NEW, retrievedTask.getStatus());
    }

    @Test
    void testCreateAndGetEpic() {
        Epic epic = new Epic("Test Epic", "Test Description", oneHourDuration, currentDateTime);
        taskManager.createEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(1);
        assertNotNull(retrievedEpic);
        assertEquals("Test Epic", retrievedEpic.getName());
        assertEquals(Status.NEW, retrievedEpic.getStatus());
    }

    @Test
    void testCreateAndGetSubtask() {
        Epic epic = new Epic("Test Epic", "Test Description", oneHourDuration, currentDateTime);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Description", 1, oneHourDuration, currentDateTime);
        taskManager.createSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtask(2);
        assertNotNull(retrievedSubtask);
        assertEquals("Test Subtask", retrievedSubtask.getName());
        assertEquals(1, retrievedSubtask.getEpicId());
        assertEquals(Status.NEW, retrievedSubtask.getStatus());
    }

    @Test
    void testEpicStatusCalculationAllNew() {
        Epic epic = new Epic("Test Epic", "Test Description", getDuration(), getStartTime(0));
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1, getDuration(), getStartTime(1));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 1, getDuration(), getStartTime(2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic retrievedEpic = taskManager.getEpic(1);
        assertEquals(Status.NEW, retrievedEpic.getStatus());
    }

    @Test
    void testEpicStatusCalculationAllDone() {
        Epic epic = new Epic("Test Epic", "Test Description", getDuration(), getStartTime(0));
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1, getDuration(), getStartTime(1));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 1, getDuration(), getStartTime(2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.changeSubtask(subtask1, 2);
        taskManager.changeSubtask(subtask2, 3);

        Epic retrievedEpic = taskManager.getEpic(1);
        assertEquals(Status.DONE, retrievedEpic.getStatus());
    }

    @Test
    void testEpicStatusCalculationMixedNewAndDone() {
        Epic epic = new Epic("Test Epic", "Test Description", getDuration(), getStartTime(0));
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1, getDuration(), getStartTime(1));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 1, getDuration(), getStartTime(2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        taskManager.changeSubtask(subtask1, 2);
        taskManager.changeSubtask(subtask2, 3);

        Epic retrievedEpic = taskManager.getEpic(1);
        assertEquals(Status.IN_PROGRESS, retrievedEpic.getStatus());
    }

    @Test
    void testEpicStatusCalculationInProgress() {
        Epic epic = new Epic("Test Epic", "Test Description", getDuration(), getStartTime(0));
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1, getDuration(), getStartTime(1));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 1, getDuration(), getStartTime(2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.NEW);
        taskManager.changeSubtask(subtask1, 2);
        taskManager.changeSubtask(subtask2, 3);

        Epic retrievedEpic = taskManager.getEpic(1);
        assertEquals(Status.IN_PROGRESS, retrievedEpic.getStatus());
    }

    @Test
    void testEpicStatusCalculationEmptySubtasks() {
        Epic epic = new Epic("Test Epic", "Test Description", oneHourDuration, currentDateTime);
        taskManager.createEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(1);
        assertEquals(Status.NEW, retrievedEpic.getStatus());
    }

    @Test
    void testTaskIntersection() {
        Task task1 = new Task("Task 1", "Description 1", oneHourDuration, currentDateTime);
        Task task2 = new Task("Task 2", "Description 2", oneHourDuration, currentDateTime.plusMinutes(30));

        taskManager.createTask(task1);

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createTask(task2);
        }, "Задача пересекается по времени с существующими задачами");
    }

    @Test
    void testTaskNoIntersection() {
        Task task1 = new Task("Task 1", "Description 1", oneHourDuration, currentDateTime);
        Task task2 = new Task("Task 2", "Description 2", oneHourDuration, currentDateTime.plusHours(2));

        assertDoesNotThrow(() -> {
            taskManager.createTask(task1);
            taskManager.createTask(task2);
        });
    }

    @Test
    void testGetEpicSubtasks() {
        Epic epic = new Epic("Test Epic", "Test Description", getDuration(), getStartTime(0));
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1, getDuration(), getStartTime(1));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 1, getDuration(), getStartTime(2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(2, taskManager.getEpicSubtasks(1).size());
    }
}
