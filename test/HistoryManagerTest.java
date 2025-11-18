import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest extends BaseTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;
    private LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
    private Duration oneHourDuration = Duration.ofMinutes(60L);

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefaultTaskManager();
    }

    @Test
    void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void testAddToHistory() {
        Task task = new Task("Test Task", "Test Description", oneHourDuration, currentDateTime);
        taskManager.createTask(task);
        taskManager.getTask(1);

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(1, history.get(0).getId());
    }

    @Test
    void testDuplicateInHistory() {
        Task task = new Task("Test Task", "Test Description", oneHourDuration, currentDateTime);
        taskManager.createTask(task);

        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getTask(1);

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void testRemoveFromHistoryBeginning() {
        Task task1 = new Task("Task 1", "Description 1", getDuration(), getStartTime(0));
        Task task2 = new Task("Task 2", "Description 2", getDuration(), getStartTime(1));
        Task task3 = new Task("Task 3", "Description 3", getDuration(), getStartTime(2));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(3);

        taskManager.deleteTask(1);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(2, history.get(0).getId());
    }

    @Test
    void testRemoveFromHistoryMiddle() {
        Task task1 = new Task("Task 1", "Description 1", getDuration(), getStartTime(0));
        Task task2 = new Task("Task 2", "Description 2", getDuration(), getStartTime(1));
        Task task3 = new Task("Task 3", "Description 3", getDuration(), getStartTime(2));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(3);

        taskManager.deleteTask(2);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(3, history.get(1).getId());
    }

    @Test
    void testRemoveFromHistoryEnd() {
        Task task1 = new Task("Task 1", "Description 1", getDuration(), getStartTime(0));
        Task task2 = new Task("Task 2", "Description 2", getDuration(), getStartTime(1));
        Task task3 = new Task("Task 3", "Description 3", getDuration(), getStartTime(2));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(3);

        taskManager.deleteTask(3);

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(1, history.get(0).getId());
        assertEquals(2, history.get(1).getId());
    }
}
