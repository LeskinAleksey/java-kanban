public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        for (int i = 1; i <= 10; i++) {
            Task currTask = new Task("Task" + i, "TaskDesc" + i);
            manager.createTask(currTask);
        }

        for (int i = 11; i <= 16; i++) {
            Epic currEpic = new Epic("Epic" + i, "EpicDesc" + i);
            manager.createEpic(currEpic);
        }

        for (int i = 11; i <= 16; i++) {
            Subtask currSubTask = new Subtask("SubTask" + i, "SubTaskDesc" + i, i);
            manager.createSubtask(currSubTask);
        }

        for (int i = 1; i < 11; i++) {
            manager.getTask(i);
        }

        for (int i = 11; i < 16; i++) {
            manager.getEpic(i);
        }

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
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
