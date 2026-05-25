package com.example.rickandmortyapp.data.repository;

import com.example.rickandmortyapp.data.model.common.ApiResult;
import com.example.rickandmortyapp.data.model.employee.Employee;
import com.example.rickandmortyapp.data.model.employee.EmployeeRequest;
import com.example.rickandmortyapp.data.remote.EmployeeService;
import com.example.rickandmortyapp.util.Constants;
import com.example.rickandmortyapp.util.NetworkCallback;

import java.util.List;

public class EmployeeRepository {
    private final EmployeeService employeeService = new EmployeeService();

    public void list(NetworkCallback<List<Employee>> callback) {
        employeeService.list(new NetworkCallback<>() {
            @Override
            public void onSuccess(ApiResult<List<Employee>> result) {
                if (result.isSuccess()) {
                    List<Employee> employees = result.getData();
                    if (employees == null || employees.isEmpty()) {
                        callback.onEmpty("Nenhum funcionario cadastrado.");
                    } else {
                        callback.onSuccess(employees);
                    }
                    return;
                }
                callback.onError(mapListError(result));
            }

            @Override
            public void onEmpty(String message) {
                callback.onEmpty(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void create(EmployeeRequest employeeRequest, NetworkCallback<Employee> callback) {
        employeeService.create(employeeRequest, new NetworkCallback<>() {
            @Override
            public void onSuccess(ApiResult<Employee> result) {
                if (result.isSuccess() && result.getData() != null) {
                    callback.onSuccess(result.getData());
                    return;
                }
                callback.onError(mapSaveError(result));
            }

            @Override
            public void onEmpty(String message) {
                callback.onEmpty(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void update(long id, EmployeeRequest employeeRequest, NetworkCallback<Employee> callback) {
        employeeService.update(id, employeeRequest, new NetworkCallback<>() {
            @Override
            public void onSuccess(ApiResult<Employee> result) {
                if (result.isSuccess() && result.getData() != null) {
                    callback.onSuccess(result.getData());
                    return;
                }
                callback.onError(mapSaveError(result));
            }

            @Override
            public void onEmpty(String message) {
                callback.onEmpty(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void delete(long id, NetworkCallback<String> callback) {
        employeeService.delete(id, new NetworkCallback<>() {
            @Override
            public void onSuccess(ApiResult<String> result) {
                if (result.isSuccess() && result.getData() != null) {
                    callback.onSuccess(result.getData());
                    return;
                }
                callback.onError(mapDeleteError(result));
            }

            @Override
            public void onEmpty(String message) {
                callback.onEmpty(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    private String mapListError(ApiResult<List<Employee>> result) {
        if (result.getCode() == Constants.ERROR_CODE_NETWORK) {
            return "Erro ao listar funcionarios.";
        }
        return result.getMessage() != null && !result.getMessage().trim().isEmpty()
                ? result.getMessage()
                : "Falha ao listar funcionarios.";
    }

    private String mapSaveError(ApiResult<Employee> result) {
        if (result.getCode() == Constants.ERROR_CODE_NETWORK) {
            return "Erro ao salvar funcionario.";
        }
        if (result.getCode() == Constants.ERROR_CODE_PARSING) {
            return "Falha ao salvar funcionario.";
        }
        return result.getMessage() != null && !result.getMessage().trim().isEmpty()
                ? result.getMessage()
                : "Falha ao salvar funcionario.";
    }

    private String mapDeleteError(ApiResult<String> result) {
        if (result.getCode() == Constants.ERROR_CODE_NETWORK) {
            return "Erro ao excluir funcionario.";
        }
        return result.getMessage() != null && !result.getMessage().trim().isEmpty()
                ? result.getMessage()
                : "Falha ao excluir funcionario.";
    }
}
