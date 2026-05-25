package com.example.rickandmortyapp.ui.employees;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.employee.Employee;
import com.example.rickandmortyapp.ui.state.EmployeeListUiState;
import com.example.rickandmortyapp.ui.characters.CharacterListActivity;
import com.example.rickandmortyapp.ui.menu.MainMenuActivity;
import com.example.rickandmortyapp.util.UiState;
import com.example.rickandmortyapp.viewmodel.EmployeeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class
EmployeeListActivity extends AppCompatActivity implements EmployeeAdapter.OnEmployeeActionListener {
    public static final String EXTRA_RESULT_MESSAGE = "extra_result_message";

    private EmployeeViewModel viewModel;
    private EmployeeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView messageTextView;
    private View stateContainer;
    private LinearLayout loadingContainer;
    private TextView loadingTextView;
    private RecyclerView recyclerView;
    private ExtendedFloatingActionButton fab;
    private TextView stateActionButton;

    private final ActivityResultLauncher<Intent> formLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null && result.getData().hasExtra(EXTRA_RESULT_MESSAGE)) {
                    Snackbar.make(fab, result.getData().getStringExtra(EXTRA_RESULT_MESSAGE), Snackbar.LENGTH_SHORT).show();
                }
                viewModel.loadEmployees();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        viewModel = new ViewModelProvider(this).get(EmployeeViewModel.class);
        findViewById(R.id.employeeListToolbar);
        swipeRefreshLayout = findViewById(R.id.employeesSwipeRefresh);
        messageTextView = findViewById(R.id.employeesMessageTextView);
        stateContainer = findViewById(R.id.employeesStateContainer);
        loadingContainer = findViewById(R.id.employeesLoadingContainer);
        loadingTextView = findViewById(R.id.employeesLoadingTextView);
        recyclerView = findViewById(R.id.employeesRecyclerView);
        fab = findViewById(R.id.addEmployeeFab);
        stateActionButton = findViewById(R.id.employeesStateActionButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.employeesBottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.navigation_employees);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_employees) {
                return true;
            }
            if (item.getItemId() == R.id.navigation_overview) {
                startActivity(new Intent(this, MainMenuActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.navigation_characters) {
                startActivity(new Intent(this, CharacterListActivity.class));
                finish();
                return true;
            }
            return false;
        });

        stateActionButton.setOnClickListener(v -> {
            if (getString(R.string.retry).contentEquals(stateActionButton.getText())) {
                viewModel.loadEmployees();
            } else {
                formLauncher.launch(new Intent(this, EmployeeFormActivity.class));
            }
        });

        adapter = new EmployeeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(viewModel::loadEmployees);
        fab.setOnClickListener(v -> formLauncher.launch(new Intent(this, EmployeeFormActivity.class)));

        viewModel.getEmployeeListState().observe(this, this::renderState);

        viewModel.loadEmployees();
    }

    private void renderState(EmployeeListUiState screenState) {
        UiState<java.util.List<Employee>> state = screenState.getEmployeesState();
        boolean isLoading = state.getStatus() == UiState.Status.LOADING;
            boolean hasItems = adapter.getItemCount() > 0;
            swipeRefreshLayout.setRefreshing(false);
            loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            loadingTextView.setText(R.string.employees_loading);
            if (isLoading && !hasItems) {
                recyclerView.setVisibility(View.GONE);
                stateContainer.setVisibility(View.GONE);
            }
            if (state.getStatus() == UiState.Status.SUCCESS) {
                recyclerView.setVisibility(View.VISIBLE);
                stateContainer.setVisibility(View.GONE);
                adapter.submitList(state.getData());
            } else if (state.getStatus() == UiState.Status.EMPTY || state.getStatus() == UiState.Status.ERROR) {
                if (!hasItems || state.getStatus() == UiState.Status.EMPTY) {
                    recyclerView.setVisibility(View.GONE);
                    stateContainer.setVisibility(View.VISIBLE);
                }
                if (state.getStatus() == UiState.Status.EMPTY) {
                    messageTextView.setText(R.string.employees_empty);
                    stateActionButton.setText(R.string.employees_empty_action);
                    adapter.submitList(java.util.Collections.emptyList());
                } else {
                    messageTextView.setText(R.string.employees_error);
                    stateActionButton.setText(R.string.retry);
                    if (hasItems) {
                        Snackbar.make(recyclerView, R.string.employees_error, Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        Integer messageRes = screenState.getMessageEvent() != null
                ? screenState.getMessageEvent().getContentIfNotHandled()
                : null;
        if (messageRes != null) {
            Snackbar.make(fab, messageRes, Snackbar.LENGTH_SHORT).show();
        }
        Boolean shouldReload = screenState.getReloadEvent() != null
                ? screenState.getReloadEvent().getContentIfNotHandled()
                : null;
        if (Boolean.TRUE.equals(shouldReload)) {
            viewModel.loadEmployees();
        }
    }

    @Override
    public void onEdit(Employee employee) {
        Intent intent = new Intent(this, EmployeeFormActivity.class);
        intent.putExtra(EmployeeFormActivity.EXTRA_EMPLOYEE, employee);
        formLauncher.launch(intent);
    }

    @Override
    public void onDelete(Employee employee) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteEmployee(employee.getId()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
