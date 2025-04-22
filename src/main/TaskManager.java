import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int taskId = 0;

    public Collection<Task> getTasksList() {
        return tasks.values();
    }

    public Collection<SubTask> getSubTasksList() {
        return subTasks.values();
    }

    public Collection<Epic> getEpicsList() {
        return epics.values();
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
        }
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubTasks();
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public SubTask getSubTask(int subTaskId) {
        return subTasks.get(subTaskId);
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    public void createTask(Task task) {
        taskId++;
        task.setId(taskId);
        task.setStatus(Status.NEW);
        tasks.put(taskId, task);
    }

    public void createSubTask(SubTask subTask) {
        taskId++;
        subTask.setId(taskId);
        subTask.setStatus(Status.NEW);
        subTasks.put(taskId, subTask);
        updateEpicStatus(subTask.getEpicId());
    }

    public void createEpic(Epic epic) {
        taskId++;
        epic.setId(taskId);
        epic.setStatus(Status.NEW);
        epics.put(taskId, epic);
    }

    public void changeTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    public void changeSubTask(SubTask subTask) {
        subTasks.replace(subTask.getId(), subTask);
        updateEpicStatus(subTask.getEpicId());
    }

    public void changeEpic(Epic epic) {
        epics.replace(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteSubTask(int taskId) {
        int epicId = subTasks.get(taskId).getEpicId();
        subTasks.remove(taskId);
        updateEpicStatus(epicId);
    }

    public void deleteEpic(int taskId) {
        epics.remove(taskId);
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == taskId) {
                subTasks.remove(subTask.getId());
            }
        }
    }

    public ArrayList<SubTask> getEpicSubTasks(int epicId) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();

        for (SubTask task : subTasks.values()) {
            if (task.getEpicId() == epicId) {
                epicSubTasks.add(task);
            }
        }

        return epicSubTasks;
    }

    private void updateEpicStatus(int epicId) {
        ArrayList<SubTask> epicSubTasks = getEpicSubTasks(epicId);

        Epic epic = epics.get(epicId);

        if (epicSubTasks.size() == 0) {
            epic.setStatus(Status.NEW);
            return;
        }

        int newCount = 0;
        int doneCount = 0;
        for (SubTask subTask : epicSubTasks) {
            Status subTaskStatus = subTask.getStatus();
            if (subTaskStatus.equals(Status.NEW)) {
                newCount++;
            } else if (subTaskStatus.equals(Status.DONE)) {
                doneCount++;
            }
        }

        if (newCount == epicSubTasks.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == epicSubTasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
