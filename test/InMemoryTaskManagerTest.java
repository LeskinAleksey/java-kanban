import interfaces.TaskManager;
import services.Managers;

public class InMemoryTaskManagerTest extends AbstractTaskManagerTest<TaskManager> {
    @Override
    protected TaskManager createTaskManager() {
        return Managers.getDefaultTaskManager();
    }
}