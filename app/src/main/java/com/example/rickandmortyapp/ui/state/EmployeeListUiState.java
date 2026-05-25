package com.example.rickandmortyapp.ui.state;

import com.example.rickandmortyapp.data.model.employee.Employee;
import com.example.rickandmortyapp.util.Event;
import com.example.rickandmortyapp.util.UiState;

import java.util.List;

public class EmployeeListUiState {
    private final UiState<List<Employee>> employeesState;
    private final boolean deleting;
    private final Event<Boolean> reloadEvent;
    private final Event<Integer> messageEvent;

    public EmployeeListUiState(UiState<List<Employee>> employeesState, boolean deleting,
                               Event<Boolean> reloadEvent, Event<Integer> messageEvent) {
        this.employeesState = employeesState;
        this.deleting = deleting;
        this.reloadEvent = reloadEvent;
        this.messageEvent = messageEvent;
    }

    public static EmployeeListUiState idle() {
        return new EmployeeListUiState(UiState.idle(), false, null, null);
    }

    public UiState<List<Employee>> getEmployeesState() {
        return employeesState;
    }

    public boolean isDeleting() {
        return deleting;
    }

    public Event<Boolean> getReloadEvent() {
        return reloadEvent;
    }

    public Event<Integer> getMessageEvent() {
        return messageEvent;
    }
}
