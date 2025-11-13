import interfaces.TaskManager;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.Managers;

class TaskTest {
    static TaskManager manager;

    @BeforeAll
    static void init() {
        manager = Managers.getDefaultTaskManager();
    }

    @AfterEach
    void clear() {
        manager.deleteAllTasks();
    }

    @Test
    void tasksWithSameIdIsEqual() {
        Task task = new Task("Task1", "Description1");
        manager.createTask(task);
        Assertions.assertEquals(manager.getTask(1), manager.getTask(1));
    }

    @Test
    void changeTaskNameAndDescription() {
        Task task = new Task("Task1", "Description1");
        manager.createTask(task);
        Task changedTask = new Task("New name", "New description");
        manager.changeTask(changedTask, task.getId());
        Assertions.assertEquals(changedTask.getName(), manager.getTask(task.getId()).getName());
        Assertions.assertEquals(changedTask.getDescription(), manager.getTask(task.getId()).getDescription());
    }

    @Test
    void addTasks() {
        Task task = new Task("Task1", "Description1");
        manager.createTask(task);
        Task task2 = new Task("Task2", "Description2");
        manager.createTask(task2);
        Assertions.assertEquals(2, manager.getTasksList().size());
    }

    @Test
    void deleteTasks() {
        Task task = new Task("Task1", "Description1");
        manager.createTask(task);
        Task task2 = new Task("Task2", "Description2");
        manager.createTask(task2);
        manager.deleteAllTasks();
        Assertions.assertEquals(0, manager.getTasksList().size());
    }

    @Test
    void deleteTask() {
        Task task = new Task("Task1", "Description1");
        manager.createTask(task);
        manager.deleteTask(task.getId());
        Assertions.assertTrue(manager.getTasksList().isEmpty());
    }
}