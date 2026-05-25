package com.example.rickandmortyapp.util;

public class Event<T> {
    private final T value;
    private boolean handled;

    public Event(T value) {
        this.value = value;
    }

    public T getContentIfNotHandled() {
        if (handled) {
            return null;
        }
        handled = true;
        return value;
    }

    public T peek() {
        return value;
    }
}
