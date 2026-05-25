package com.example.rickandmortyapp.domain.usecase;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.employee.Employee;
import com.example.rickandmortyapp.data.model.employee.EmployeeRequest;
import com.example.rickandmortyapp.data.repository.EmployeeRepository;
import com.example.rickandmortyapp.util.CurrencyFormatter;
import com.example.rickandmortyapp.util.NetworkCallback;

public class SaveEmployeeUseCase {
    private final Application application;
    private final EmployeeRepository repository;

    public SaveEmployeeUseCase(@NonNull Application application, EmployeeRepository repository) {
        this.application = application;
        this.repository = repository;
    }

    public void execute(Long id, String nome, String email, String cargo, String salarioText, boolean ativo,
                        NetworkCallback<Employee> callback) {
        if (TextUtils.isEmpty(nome == null ? null : nome.trim())) {
            callback.onError(application.getString(R.string.employee_name_error));
            return;
        }

        String trimmedEmail = email == null ? "" : email.trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            callback.onError(application.getString(R.string.employee_email_error));
            return;
        }

        if (TextUtils.isEmpty(cargo == null ? null : cargo.trim())) {
            callback.onError(application.getString(R.string.employee_role_error));
            return;
        }

        if (TextUtils.isEmpty(salarioText == null ? null : salarioText.trim())) {
            callback.onError(application.getString(R.string.employee_salary_error));
            return;
        }

        final double salario;
        Double parsedSalary = CurrencyFormatter.parseFlexible(salarioText);
        if (parsedSalary == null) {
            callback.onError(application.getString(R.string.employee_salary_error));
            return;
        }
        salario = parsedSalary;

        EmployeeRequest request = new EmployeeRequest(nome.trim(), trimmedEmail, cargo.trim(), salario, ativo);
        if (id == null) {
            repository.create(request, callback);
        } else {
            repository.update(id, request, callback);
        }
    }
}
