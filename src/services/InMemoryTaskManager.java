package services;

import enums.Status;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    final HashMap<Integer, Task> tasks = new HashMap<>();
    final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    final HashMap<Integer, Epic> epics = new HashMap<>();
    int taskId = 0;
    private HistoryManager viewHistory = Managers.getDefaultHistory();

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null)
                .collect(Collectors.toList());
    }

    private boolean isTasksIntersect(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null ||
                task1.getDuration() == null || task2.getDuration() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !start1.isAfter(end2) && !start2.isAfter(end1) && !start1.equals(end2) && !start2.equals(end1);
    }

    private boolean isTaskIntersectWithAny(Task newTask) {
        List<Task> sortedTasks = getPrioritizedTasks();

        int position = 0;
        while (position < sortedTasks.size() &&
                sortedTasks.get(position).getStartTime() != null &&
                sortedTasks.get(position).getStartTime().isBefore(newTask.getStartTime())) {
            position++;
        }

        if (position > 0) {
            Task previousTask = sortedTasks.get(position - 1);
            if (!(previousTask.getId() == (newTask.getId())) && isTasksIntersect(previousTask, newTask)) {
                return true;
            }
        }

        if (position < sortedTasks.size()) {
            Task nextTask = sortedTasks.get(position);
            if (!(nextTask.getId() == (newTask.getId())) && isTasksIntersect(nextTask, newTask)) {
                return true;
            }
        }

        return false;
    }

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
        tasks.keySet().forEach(viewHistory::remove);
        taskId -= tasks.size();
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subTasks.keySet().forEach(viewHistory::remove);
        taskId -= subTasks.size();
        subTasks.clear();
        epics.values().forEach(epic -> epic.setStatus(Status.NEW));
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(viewHistory::remove);
        taskId -= epics.size();
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
        if (isTaskIntersectWithAny(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующими задачами");
        }

        taskId++;
        task.setId(taskId);
        task.setStatus(Status.NEW);
        tasks.put(taskId, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (isTaskIntersectWithAny(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с существующими задачами");
        }

        taskId++;
        subtask.setId(taskId);
        subtask.setStatus(Status.NEW);
        subTasks.put(taskId, subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
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
        task.setId(taskId);
        if (isTaskIntersectWithAny(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с существующими задачами");
        }

        Task oldTask = tasks.get(taskId);
        if (oldTask != null && oldTask.getStartTime() != null) {
            prioritizedTasks.remove(oldTask);
        }
        tasks.replace(taskId, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void changeSubtask(Subtask subtask, int subtaskId) {
        subtask.setId(subtaskId);
        if (isTaskIntersectWithAny(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с существующими задачами");
        }

        Subtask oldSubtask = subTasks.get(subtaskId);
        if (oldSubtask != null && oldSubtask.getStartTime() != null) {
            prioritizedTasks.remove(oldSubtask);
        }

        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }
        subTasks.replace(subtaskId, subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
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
        viewHistory.remove(taskId);
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
        subTasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == taskId)
                .map(Subtask::getId)
                .forEach(subTasks::remove);
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        return subTasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Task> getHistory() {
        return viewHistory.getHistory();
    }

    void updateEpicStatus(int epicId) {
        ArrayList<Subtask> epicSubTasks = getEpicSubtasks(epicId);
        Epic epic = epics.get(epicId);

        if (epicSubTasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        long newCount = epicSubTasks.stream()
                .filter(subTask -> subTask.getStatus() == Status.NEW)
                .count();

        long doneCount = epicSubTasks.stream()
                .filter(subTask -> subTask.getStatus() == Status.DONE)
                .count();

        if (newCount == epicSubTasks.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == epicSubTasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
