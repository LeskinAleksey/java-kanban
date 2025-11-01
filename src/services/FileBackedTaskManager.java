package services;

import enums.Status;
import enums.TaskType;
import exceptions.ManagerLoadFromFileException;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static enums.TaskType.SUBTASK;
import static enums.TaskType.TASK;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public File file;

    public FileBackedTaskManager() {
        this.file = new File("resources/tasks.csv");
    }

    public String toString(Task task) {
        TaskType type = task.getType();
        String epicId = " ";
        if (type == SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + epicId;
    }

    public Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        String epicId = parts[5];
        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case SUBTASK:
                task = new Subtask(name, description, Integer.parseInt(epicId));
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getTasksList()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Task task : getSubtasksList()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Task task : getEpicsList()) {
                writer.write(toString(task));
                writer.newLine();
            }

            writer.newLine();
            List<Task> history = getHistory();
            if (!history.isEmpty()) {
                StringBuilder historyIds = new StringBuilder();
                for (Task task : history) {
                    if (!historyIds.isEmpty()) {
                        historyIds.append(",");
                    }
                    historyIds.append(task.getId());
                }
                writer.write(historyIds.toString());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл: " + file);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager();

        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8);

            if (lines.size() <= 1) {
                return manager;
            }

            int i = 1;

            while (i < lines.size() && !lines.get(i).trim().isEmpty()) {
                Task task = manager.fromString(lines.get(i));

                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case SUBTASK:
                        manager.subTasks.put(task.getId(), (Subtask) task);
                        break;
                    case EPIC:
                        manager.epics.put(task.getId(), (Epic) task);
                        break;
                }

                if (task.getId() > manager.taskId) {
                    manager.taskId = task.getId();
                }

                i++;
            }

            for (Epic epic : manager.epics.values()) {
                manager.updateEpicStatus(epic.getId());
            }

        } catch (IOException e) {
            throw new ManagerLoadFromFileException("Ошибка загрузки из файла: " + file.getPath());
        }

        return manager;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void changeTask(Task task, int taskId) {
        super.changeTask(task, taskId);
        save();
    }

    @Override
    public void changeSubtask(Subtask subtask, int subtaskId) {
        super.changeSubtask(subtask, subtaskId);
        save();
    }

    @Override
    public void changeEpic(Epic epic, int epicId) {
        super.changeEpic(epic, epicId);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteSubtask(int taskId) {
        super.deleteSubtask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int taskId) {
        super.deleteEpic(taskId);
    }

    @Override
    public Collection<Task> getTasksList() {
        return super.getTasksList();
    }

    @Override
    public Collection<Subtask> getSubtasksList() {
        return super.getSubtasksList();
    }

    @Override
    public Collection<Epic> getEpicsList() {
        return super.getEpicsList();
    }

    @Override
    public Task getTask(int taskId) {
        return super.getTask(taskId);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        return super.getSubtask(subtaskId);
    }

    @Override
    public Epic getEpic(int epicId) {
        return super.getEpic(epicId);
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        return super.getEpicSubtasks(epicId);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    void updateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
    }
}
