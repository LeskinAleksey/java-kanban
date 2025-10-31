import interfaces.TaskManager;
import model.Epic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.Managers;

class EpicTest {
    static TaskManager manager;

    @BeforeAll
    static void init() {
        manager = Managers.getDefaultTaskManager();
    }

    @AfterEach
    void clear() {
        manager.deleteAllEpics();
    }

    @Test
    void epicsWithSameIdIsEqual() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Assertions.assertEquals(manager.getEpic(epic.getId()), manager.getEpic(epic.getId()));
    }

    @Test
    void changeEpicNameAndDescription() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Epic changedEpic = new Epic("New name", "New description");
        manager.changeEpic(changedEpic, epic.getId());
        Assertions.assertEquals(changedEpic.getName(), manager.getEpic(epic.getId()).getName());
        Assertions.assertEquals(changedEpic.getDescription(), manager.getEpic(epic.getId()).getDescription());
    }

    @Test
    void addEpics() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Epic epic2 = new Epic("Epic2", "Description2");
        manager.createEpic(epic2);
        Assertions.assertEquals(2, manager.getEpicsList().size());
    }

    @Test
    void deleteEpics() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Epic epic2 = new Epic("Epic2", "Description2");
        manager.createEpic(epic2);
        manager.deleteAllEpics();
        Assertions.assertEquals(0, manager.getEpicsList().size());
    }

    @Test
    void deleteEpic() {
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        manager.deleteEpic(epic.getId());
        Assertions.assertTrue(manager.getEpicsList().isEmpty());
    }
}