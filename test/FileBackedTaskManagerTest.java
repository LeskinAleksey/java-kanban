import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test_tasks", ".csv").toFile();
        manager = new FileBackedTaskManager();
        manager.file = tempFile;
    }

    @Test
    void shouldSaveAndLoadEmptyFile() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getTasksList().isEmpty());
        assertTrue(loadedManager.getSubtasksList().isEmpty());
        assertTrue(loadedManager.getEpicsList().isEmpty());
    }

    @Test
    void shouldSaveMultipleTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
        manager.createSubtask(subtask);

        List<String> lines = null;
        try {
            lines = Files.readAllLines(tempFile.toPath());
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
        assertEquals(6, lines.size());
    }

    @Test
    void shouldLoadMultipleTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        try {
            List<String> lines = Files.readAllLines(tempFile.toPath());
            lines.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(2, loadedManager.getTasksList().size());
        assertEquals(1, loadedManager.getEpicsList().size());
        assertEquals(1, loadedManager.getSubtasksList().size());

        Task loadedTask1 = loadedManager.getTask(1);
        Task loadedTask2 = loadedManager.getTask(2);
        Epic loadedEpic = loadedManager.getEpic(3);
        Subtask loadedSubtask = loadedManager.getSubtask(4);

        assertEquals("Task 1", loadedTask1.getName());
        assertEquals("Task 2", loadedTask2.getName());
        assertEquals("Epic 1", loadedEpic.getName());
        assertEquals("Subtask 1", loadedSubtask.getName());
        assertEquals(3, loadedSubtask.getEpicId());
    }
}
