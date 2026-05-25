package com.example.rickandmortyapp.ui.employees;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.employee.Employee;
import com.example.rickandmortyapp.util.CurrencyFormatter;
import com.example.rickandmortyapp.util.DateUtils;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmployeeAdapter extends ListAdapter<Employee, EmployeeAdapter.EmployeeViewHolder> {
    public interface OnEmployeeActionListener {
        void onEdit(Employee employee);
        void onDelete(Employee employee);
    }

    private static final DiffUtil.ItemCallback<Employee> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Employee>() {
                @Override
                public boolean areItemsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
                    return Objects.equals(oldItem.getId(), newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Employee oldItem, @NonNull Employee newItem) {
                    return Objects.equals(oldItem.getNome(), newItem.getNome())
                            && Objects.equals(oldItem.getEmail(), newItem.getEmail())
                            && Objects.equals(oldItem.getCargo(), newItem.getCargo())
                            && Double.compare(oldItem.getSalario(), newItem.getSalario()) == 0
                            && oldItem.isAtivo() == newItem.isAtivo()
                            && Objects.equals(oldItem.getDataCriacao(), newItem.getDataCriacao());
                }
            };

    private final OnEmployeeActionListener listener;

    public EmployeeAdapter(OnEmployeeActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @Override
    public void submitList(List<Employee> employees) {
        super.submitList(employees == null ? null : new ArrayList<>(employees));
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EmployeeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView emailText;
        private final TextView roleText;
        private final TextView salaryText;
        private final TextView metadataText;
        private final Chip statusChip;
        private final Button editButton;
        private final Button deleteButton;

        EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.employeeNameTextView);
            emailText = itemView.findViewById(R.id.employeeEmailTextView);
            roleText = itemView.findViewById(R.id.employeeRoleTextView);
            salaryText = itemView.findViewById(R.id.employeeSalaryTextView);
            metadataText = itemView.findViewById(R.id.employeeMetadataTextView);
            statusChip = itemView.findViewById(R.id.employeeStatusChip);
            editButton = itemView.findViewById(R.id.editEmployeeButton);
            deleteButton = itemView.findViewById(R.id.deleteEmployeeButton);
        }

        void bind(Employee employee) {
            nameText.setText(employee.getNome());
            emailText.setText(employee.getEmail());
            roleText.setText(employee.getCargo());
            salaryText.setText(itemView.getContext().getString(
                    R.string.salary_label_value,
                    CurrencyFormatter.formatCurrency(employee.getSalario())
            ));
            metadataText.setText(itemView.getContext().getString(
                    R.string.employee_metadata_value,
                    employee.getId(),
                    DateUtils.formatDisplayDate(employee.getDataCriacao())
            ));
            boolean isActive = employee.isAtivo();
            statusChip.setText(isActive ? R.string.active_status : R.string.inactive_status);
            statusChip.setChipBackgroundColorResource(isActive ? R.color.app_chip_alive : R.color.app_chip_unknown);
            itemView.setOnClickListener(v -> listener.onEdit(employee));
            editButton.setOnClickListener(v -> listener.onEdit(employee));
            deleteButton.setOnClickListener(v -> listener.onDelete(employee));
        }
    }
}
