package model;

import enums.TaskType;

import static enums.TaskType.EPIC;

public class Epic extends Task {

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public TaskType getType() {
        return EPIC;
    }
}
