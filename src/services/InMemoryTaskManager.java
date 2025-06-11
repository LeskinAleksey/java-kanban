package services;

import enums.Status;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int taskId = 0;
    private HistoryManager viewHistory = Managers.getDefaultHistory();

    @Override
    public Collection<Task> getTasksList() {
        return tasks.values();
    }

    @Override
    public Collection<Subtask> getSubtasksList() {
        return subTasks.values();
    }

    @Override
    public Collection<Epic> getEpicsList() {
        return epics.values();
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        viewHistory.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        Subtask subTask = subTasks.get(subtaskId);
        viewHistory.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        viewHistory.add(epic);
        return epic;
    }

    @Override
    public void createTask(Task task) {
        taskId++;
        task.setId(taskId);
        task.setStatus(Status.NEW);
        tasks.put(taskId, task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        taskId++;
        subtask.setId(taskId);
        subtask.setStatus(Status.NEW);
        subTasks.put(taskId, subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void createEpic(Epic epic) {
        taskId++;
        epic.setId(taskId);
        epic.setStatus(Status.NEW);
        epics.put(taskId, epic);
    }

    @Override
    public void changeTask(Task task, int taskId) {
        tasks.replace(taskId, task);
    }

    @Override
    public void changeSubtask(Subtask subtask, int subtaskId) {
        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }
        subTasks.replace(subtaskId, subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void changeEpic(Epic epic, int epicId) {
        epics.replace(epicId, epic);
        updateEpicStatus(epicId);
    }

    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteSubtask(int taskId) {
        int epicId = subTasks.get(taskId).getEpicId();
        subTasks.remove(taskId);
        updateEpicStatus(epicId);
    }

    @Override
    public void deleteEpic(int taskId) {
        epics.remove(taskId);
        for (Subtask subTask : subTasks.values()) {
            if (subTask.getEpicId() == taskId) {
                subTasks.remove(subTask.getId());
            }
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubTasks = new ArrayList<>();

        for (Subtask task : subTasks.values()) {
            if (task.getEpicId() == epicId) {
                epicSubTasks.add(task);
            }
        }

        return epicSubTasks;
    }

    public List<Task> getHistory() {
        return viewHistory.getHistory();
    }

    private void updateEpicStatus(int epicId) {
        ArrayList<Subtask> epicSubTasks = getEpicSubtasks(epicId);

        Epic epic = epics.get(epicId);

        if (epicSubTasks.size() == 0) {
            epic.setStatus(Status.NEW);
            return;
        }

        int newCount = 0;
        int doneCount = 0;
        for (Subtask subTask : epicSubTasks) {
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
