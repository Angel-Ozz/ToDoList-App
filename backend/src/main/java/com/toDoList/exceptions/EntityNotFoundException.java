package com.toDoList.exceptions;

public class EntityNotFoundException extends RuntimeException {
    private final Integer entityId;

    public EntityNotFoundException(String message, Integer entityId) {
        super(message);
        this.entityId = entityId;
    }

    public Integer getEntityId() {
        return entityId;
    }
}
