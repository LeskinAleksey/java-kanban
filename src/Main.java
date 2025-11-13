import interfaces.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import services.FileBackedTaskManager;
import services.Managers;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultTaskManager();
        FileBackedTaskManager fileManager = Managers.getDefaultFileManager();
        for (int i = 1; i <= 10; i++) {
            Task currTask = new Task("model.Task" + i, "TaskDesc" + i);
            fileManager.createTask(currTask);
        }

        for (int i = 11; i <= 16; i++) {
            Epic currEpic = new Epic("model.Epic" + i, "EpicDesc" + i);
            fileManager.createEpic(currEpic);
        }

        for (int i = 11; i <= 16; i++) {
            Subtask currSubTask = new Subtask("SubTask" + i, "SubTaskDesc" + i, i);
            fileManager.createSubtask(currSubTask);
        }

        for (int i = 1; i < 11; i++) {
            fileManager.getTask(i);
        }

        for (int i = 11; i < 17; i++) {
            fileManager.getEpic(i);
        }

        for (int i = 17; i <= 22; i++) {
            fileManager.getSubtask(i);
        }

        printAllTasks(fileManager);
    }

    private static void printAllTasks(FileBackedTaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpicsList()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasksList()) {
            System.out.println(subtask);
        }
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
