import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int taskId = 0;

    public HashMap<Integer, Task> getTasksList() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasksList() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpicsList() {
        return epics;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
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

    public void createTask(String name, String description) {
        taskId++;
        Task task = new Task(name, description, taskId, Status.NEW);
        tasks.put(taskId, task);
    }

    public void createSubTask(String name, String description, int epicId) {
        taskId++;
        SubTask subTask = new SubTask(name, description, taskId, Status.NEW, epicId);
        subTasks.put(taskId, subTask);
    }

    public void createEpic(String name, String description) {
        taskId++;
        Epic epic = new Epic(name, description, taskId, Status.NEW);
        epics.put(taskId, epic);
    }

    public void changeTask(int taskId, String name, String description, Status status) {
        Task task = new Task(name, description, taskId, status);
        tasks.replace(taskId, task);
    }

    public void changeSubTask(int taskId, String name, String description, Status status, int epicId) {
        SubTask subTask = new SubTask(name, description, taskId, status, epicId);
        subTasks.replace(taskId, subTask);
        updateEpicStatus(epicId);
    }

    public void changeEpic(int taskId, String name, String description, Status status) {
        Epic epic = new Epic(name, description, taskId, status);
        epics.replace(taskId, epic);
        updateEpicStatus(taskId);
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

    public void changeEpic(int taskId, String name, String description) {
        Status currentEpicStatus = epics.get(taskId).getStatus();
        Epic epic = new Epic(name, description, taskId, currentEpicStatus);
        epics.replace(taskId, epic);
    }
}
