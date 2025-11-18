import interfaces.TaskManager;
import model.Task;
import services.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @BeforeEach
    @Override
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test_tasks", ".csv").toFile();
        taskManager = createTaskManager();
        taskManager.file = tempFile;
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager();
    }

    @Test
    void testSaveAndLoadFromFile() {
        var task = new Task("Test Task", "Test Description", oneHourDuration, currentDateTime);
        taskManager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getTasksList().size());
        assertEquals("Test Task", loadedManager.getTask(1).getName());
    }

    @Test
    void testFileOperations() {
        assertDoesNotThrow(() -> {
            var task = new Task("Test Task", "Test Description", oneHourDuration, currentDateTime);
            taskManager.createTask(task);
        });
    }
}
