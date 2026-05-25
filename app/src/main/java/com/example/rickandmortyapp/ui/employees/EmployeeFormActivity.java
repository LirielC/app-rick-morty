package com.example.rickandmortyapp.ui.employees;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.employee.Employee;
import com.example.rickandmortyapp.util.CurrencyFormatter;
import com.example.rickandmortyapp.util.DateUtils;
import com.example.rickandmortyapp.viewmodel.EmployeeViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

public class EmployeeFormActivity extends AppCompatActivity {
    public static final String EXTRA_EMPLOYEE = "extra_employee";

    private EmployeeViewModel viewModel;
    private Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_form);

        viewModel = new ViewModelProvider(this).get(EmployeeViewModel.class);
        employee = (Employee) getIntent().getSerializableExtra(EXTRA_EMPLOYEE);
        MaterialToolbar toolbar = findViewById(R.id.employeeFormToolbar);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        EditText nameEditText = findViewById(R.id.employeeNameEditText);
        EditText emailEditText = findViewById(R.id.employeeEmailEditText);
        EditText roleEditText = findViewById(R.id.employeeRoleEditText);
        EditText salaryEditText = findViewById(R.id.employeeSalaryEditText);
        CheckBox activeCheckBox = findViewById(R.id.employeeActiveCheckBox);
        Button saveButton = findViewById(R.id.saveEmployeeButton);
        Button cancelButton = findViewById(R.id.cancelEmployeeButton);
        TextView titleTextView = findViewById(R.id.employeeFormTitleTextView);
        TextView subtitleTextView = findViewById(R.id.employeeFormSubtitleTextView);
        TextView employeeMetaTextView = findViewById(R.id.employeeFormMetaTextView);
        TextView contractStatusTextView = findViewById(R.id.employeeContractStatusTextView);
        View employeeMetaCard = findViewById(R.id.employeeMetaCard);
        LinearLayout loadingContainer = findViewById(R.id.employeeSaveLoadingContainer);

        if (employee != null) {
            toolbar.setTitle(R.string.employee_edit_title);
            titleTextView.setText(R.string.employee_edit_title);
            subtitleTextView.setText(R.string.employee_edit_subtitle);
            saveButton.setText(R.string.save_employee_edit);
            nameEditText.setText(employee.getNome());
            emailEditText.setText(employee.getEmail());
            roleEditText.setText(employee.getCargo());
            salaryEditText.setText(CurrencyFormatter.formatInput(employee.getSalario()));
            activeCheckBox.setChecked(employee.isAtivo());
            employeeMetaCard.setVisibility(View.VISIBLE);
            employeeMetaTextView.setText(getString(
                    R.string.employee_form_metadata_value,
                    employee.getId(),
                    DateUtils.formatDisplayDate(employee.getDataCriacao())
            ));
        } else {
            toolbar.setTitle(R.string.employee_create_title);
            titleTextView.setText(R.string.employee_create_title);
            subtitleTextView.setText(R.string.employee_create_subtitle);
            saveButton.setText(R.string.save_employee_create);
            employeeMetaCard.setVisibility(View.GONE);
        }

        contractStatusTextView.setText(activeCheckBox.isChecked()
                ? R.string.employee_contract_status_active
                : R.string.employee_contract_status_inactive);
        activeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> contractStatusTextView.setText(
                isChecked ? R.string.employee_contract_status_active : R.string.employee_contract_status_inactive
        ));
        salaryEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Double parsedValue = CurrencyFormatter.parseFlexible(salaryEditText.getText().toString());
                if (parsedValue != null) {
                    salaryEditText.setText(CurrencyFormatter.formatInput(parsedValue));
                }
            }
        });

        saveButton.setOnClickListener(v -> viewModel.saveEmployee(
                employee != null ? employee.getId() : null,
                nameEditText.getText().toString(),
                emailEditText.getText().toString(),
                roleEditText.getText().toString(),
                salaryEditText.getText().toString(),
                activeCheckBox.isChecked()
        ));
        cancelButton.setOnClickListener(v -> finish());

        viewModel.getEmployeeFormState().observe(this, screenState -> {
            boolean isLoading = screenState.isSaving();
            loadingContainer.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            saveButton.setEnabled(!isLoading);
            cancelButton.setEnabled(!isLoading);
            nameEditText.setEnabled(!isLoading);
            emailEditText.setEnabled(!isLoading);
            roleEditText.setEnabled(!isLoading);
            salaryEditText.setEnabled(!isLoading);
            activeCheckBox.setEnabled(!isLoading);
            Integer finishMessageRes = screenState.getFinishMessageEvent() != null
                    ? screenState.getFinishMessageEvent().getContentIfNotHandled()
                    : null;
            if (finishMessageRes != null) {
                Intent data = new Intent();
                data.putExtra(EmployeeListActivity.EXTRA_RESULT_MESSAGE, getString(finishMessageRes));
                setResult(RESULT_OK, data);
                finish();
            }
            String errorMessage = screenState.getErrorMessageEvent() != null
                    ? screenState.getErrorMessageEvent().getContentIfNotHandled()
                    : null;
            if (errorMessage != null) {
                Snackbar.make(saveButton, mapSaveMessage(errorMessage), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private String mapSaveMessage(String rawMessage) {
        if (rawMessage == null) {
            return getString(R.string.employee_save_error);
        }
        if (rawMessage.equals(getString(R.string.employee_name_error))
                || rawMessage.equals(getString(R.string.employee_email_error))
                || rawMessage.equals(getString(R.string.employee_role_error))
                || rawMessage.equals(getString(R.string.employee_salary_error))) {
            return rawMessage;
        }
        return getString(R.string.employee_save_error);
    }
}
