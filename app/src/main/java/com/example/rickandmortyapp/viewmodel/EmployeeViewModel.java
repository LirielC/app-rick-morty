package com.example.rickandmortyapp.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.employee.Employee;
import com.example.rickandmortyapp.data.repository.EmployeeRepository;
import com.example.rickandmortyapp.domain.usecase.SaveEmployeeUseCase;
import com.example.rickandmortyapp.util.Event;
import com.example.rickandmortyapp.ui.state.EmployeeFormUiState;
import com.example.rickandmortyapp.ui.state.EmployeeListUiState;
import com.example.rickandmortyapp.util.NetworkCallback;
import com.example.rickandmortyapp.util.UiState;

import java.util.List;

public class EmployeeViewModel extends AndroidViewModel {
    private final MutableLiveData<EmployeeListUiState> employeeListState =
            new MutableLiveData<>(EmployeeListUiState.idle());
    private final MutableLiveData<EmployeeFormUiState> employeeFormState =
            new MutableLiveData<>(EmployeeFormUiState.idle());
    private final EmployeeRepository repository = new EmployeeRepository();
    private final SaveEmployeeUseCase saveEmployeeUseCase;

    public EmployeeViewModel(@NonNull Application application) {
        super(application);
        saveEmployeeUseCase = new SaveEmployeeUseCase(application, repository);
    }

    public LiveData<EmployeeListUiState> getEmployeeListState() {
        return employeeListState;
    }

    public LiveData<EmployeeFormUiState> getEmployeeFormState() {
        return employeeFormState;
    }

    public void loadEmployees() {
        employeeListState.setValue(new EmployeeListUiState(UiState.loading(), false, null, null));
        repository.list(new NetworkCallback<>() {
            @Override
            public void onSuccess(List<Employee> data) {
                employeeListState.postValue(new EmployeeListUiState(UiState.success(data), false, null, null));
            }

            @Override
            public void onEmpty(String message) {
                employeeListState.postValue(new EmployeeListUiState(UiState.empty(message), false, null, null));
            }

            @Override
            public void onError(String message) {
                employeeListState.postValue(new EmployeeListUiState(UiState.error(message), false, null, null));
            }
        });
    }

    public void saveEmployee(Long id, String nome, String email, String cargo, String salarioText, boolean ativo) {
        employeeFormState.setValue(new EmployeeFormUiState(true, null, null));
        saveEmployeeUseCase.execute(id, nome, email, cargo, salarioText, ativo, new NetworkCallback<>() {
            @Override
            public void onSuccess(Employee data) {
                int messageRes = id == null ? R.string.employee_create_success : R.string.employee_edit_success;
                employeeFormState.postValue(new EmployeeFormUiState(false, new Event<>(messageRes), null));
            }

            @Override
            public void onEmpty(String message) {
                employeeFormState.postValue(new EmployeeFormUiState(false, null, new Event<>(message)));
            }

            @Override
            public void onError(String message) {
                employeeFormState.postValue(new EmployeeFormUiState(false, null, new Event<>(message)));
            }
        });
    }

    public void deleteEmployee(long id) {
        employeeListState.setValue(new EmployeeListUiState(currentEmployeesState(), true, null, null));
        repository.delete(id, new NetworkCallback<>() {
            @Override
            public void onSuccess(String data) {
                employeeListState.postValue(new EmployeeListUiState(
                        currentEmployeesState(),
                        false,
                        new Event<>(Boolean.TRUE),
                        new Event<>(R.string.employee_delete_success)
                ));
            }

            @Override
            public void onEmpty(String message) {
                employeeListState.postValue(new EmployeeListUiState(
                        currentEmployeesState(),
                        false,
                        null,
                        new Event<>(R.string.employee_delete_error)
                ));
            }

            @Override
            public void onError(String message) {
                employeeListState.postValue(new EmployeeListUiState(
                        currentEmployeesState(),
                        false,
                        null,
                        new Event<>(R.string.employee_delete_error)
                ));
            }
        });
    }

    private UiState<List<Employee>> currentEmployeesState() {
        EmployeeListUiState currentState = employeeListState.getValue();
        return currentState != null ? currentState.getEmployeesState() : UiState.idle();
    }
}
