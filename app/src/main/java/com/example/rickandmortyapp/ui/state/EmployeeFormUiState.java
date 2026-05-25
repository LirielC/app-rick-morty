package com.example.rickandmortyapp.ui.state;

import com.example.rickandmortyapp.util.Event;

public class EmployeeFormUiState {
    private final boolean saving;
    private final Event<Integer> finishMessageEvent;
    private final Event<String> errorMessageEvent;

    public EmployeeFormUiState(boolean saving, Event<Integer> finishMessageEvent, Event<String> errorMessageEvent) {
        this.saving = saving;
        this.finishMessageEvent = finishMessageEvent;
        this.errorMessageEvent = errorMessageEvent;
    }

    public static EmployeeFormUiState idle() {
        return new EmployeeFormUiState(false, null, null);
    }

    public boolean isSaving() {
        return saving;
    }

    public Event<Integer> getFinishMessageEvent() {
        return finishMessageEvent;
    }

    public Event<String> getErrorMessageEvent() {
        return errorMessageEvent;
    }
}
