package com.example.rickandmortyapp.ui.characters;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.character.Character;
import com.example.rickandmortyapp.ui.employees.EmployeeListActivity;
import com.example.rickandmortyapp.ui.menu.MainMenuActivity;
import com.example.rickandmortyapp.ui.profile.CharacterProfileActivity;
import com.example.rickandmortyapp.util.UiState;
import com.example.rickandmortyapp.viewmodel.CharacterListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

public class CharacterListActivity extends AppCompatActivity {
    private CharacterListViewModel viewModel;
    private CharacterAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout loadingContainer;
    private TextView messageTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Button applyFiltersButton;
    private Button clearFiltersButton;
    private Button toggleFiltersButton;
    private Button stateActionButton;
    private AutoCompleteTextView statusAutoComplete;
    private AutoCompleteTextView genderAutoComplete;
    private AutoCompleteTextView speciesAutoComplete;
    private TextView filtersSummaryTextView;
    private View filtersPanelContainer;
    private View stateContainer;
    private boolean areFiltersExpanded;

    private static final String STATE_FILTERS_EXPANDED = "filters_expanded";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_list);

        viewModel = new ViewModelProvider(this).get(CharacterListViewModel.class);
        findViewById(R.id.characterListToolbar);
        progressBar = findViewById(R.id.charactersProgressBar);
        loadingContainer = findViewById(R.id.charactersLoadingContainer);
        messageTextView = findViewById(R.id.charactersMessageTextView);
        stateContainer = findViewById(R.id.charactersStateContainer);
        stateActionButton = findViewById(R.id.charactersStateActionButton);
        swipeRefreshLayout = findViewById(R.id.charactersSwipeRefresh);
        recyclerView = findViewById(R.id.charactersRecyclerView);
        statusAutoComplete = findViewById(R.id.statusAutoCompleteTextView);
        genderAutoComplete = findViewById(R.id.genderAutoCompleteTextView);
        speciesAutoComplete = findViewById(R.id.speciesAutoCompleteTextView);
        applyFiltersButton = findViewById(R.id.applyFiltersButton);
        clearFiltersButton = findViewById(R.id.clearFiltersButton);
        toggleFiltersButton = findViewById(R.id.toggleFiltersButton);
        filtersSummaryTextView = findViewById(R.id.filtersSummaryTextView);
        filtersPanelContainer = findViewById(R.id.filtersPanelContainer);
        BottomNavigationView bottomNavigationView = findViewById(R.id.charactersBottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.navigation_characters);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_characters) {
                return true;
            }
            if (item.getItemId() == R.id.navigation_overview) {
                startActivity(new Intent(this, MainMenuActivity.class));
                finish();
                return true;
            }
            if (item.getItemId() == R.id.navigation_employees) {
                startActivity(new Intent(this, EmployeeListActivity.class));
                finish();
                return true;
            }
            return false;
        });

        statusAutoComplete.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.status_options)));
        genderAutoComplete.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.gender_options)));
        speciesAutoComplete.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.species_options)));

        adapter = new CharacterAdapter(this::openCharacterProfile);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visible = layoutManager.getChildCount();
                int total = layoutManager.getItemCount();
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                if (dy > 0 && visible + firstVisible >= total - 4) {
                    viewModel.loadNextPage();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(viewModel::loadFirstPage);
        applyFiltersButton.setOnClickListener(v -> {
            viewModel.applyFilters(
                    statusAutoComplete.getText().toString(),
                    genderAutoComplete.getText().toString(),
                    speciesAutoComplete.getText().toString()
            );
            updateFiltersSummary();
            setFiltersExpanded(false);
        });
        clearFiltersButton.setOnClickListener(v -> clearFilters());
        toggleFiltersButton.setOnClickListener(v -> setFiltersExpanded(!areFiltersExpanded));
        stateActionButton.setOnClickListener(v -> {
            if (getString(R.string.clear_filters).contentEquals(stateActionButton.getText())) {
                clearFilters();
            } else {
                viewModel.loadFirstPage();
            }
        });

        viewModel.getCharactersState().observe(this, this::renderState);
        areFiltersExpanded = savedInstanceState != null && savedInstanceState.getBoolean(STATE_FILTERS_EXPANDED, false);
        setFiltersExpanded(areFiltersExpanded);
        updateFiltersSummary();

        if (savedInstanceState == null && !viewModel.hasLoadedCharacters()) {
            viewModel.loadFirstPage();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_FILTERS_EXPANDED, areFiltersExpanded);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void renderState(UiState<List<Character>> state) {
        boolean isLoading = state.getStatus() == UiState.Status.LOADING;
        boolean hasItems = adapter.getItemCount() > 0;
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loadingContainer.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.charactersLoadingTextView)).setText(hasItems ? R.string.characters_loading_more : R.string.characters_loading);
        swipeRefreshLayout.setRefreshing(false);
        setFiltersEnabled(!isLoading);

        if (state.getStatus() == UiState.Status.SUCCESS) {
            renderList(state.getData());
            return;
        }

        if (state.getStatus() == UiState.Status.EMPTY) {
            adapter.submitList(Collections.emptyList());
            recyclerView.setVisibility(View.GONE);
            stateContainer.setVisibility(View.VISIBLE);
            messageTextView.setText(R.string.characters_empty);
            stateActionButton.setText(R.string.clear_filters);
            return;
        }

        if (state.getStatus() == UiState.Status.ERROR) {
            if (!hasItems) {
                recyclerView.setVisibility(View.GONE);
                stateContainer.setVisibility(View.VISIBLE);
                messageTextView.setText(R.string.characters_error);
                stateActionButton.setText(R.string.retry);
            } else {
                Snackbar.make(recyclerView, R.string.characters_error, Snackbar.LENGTH_LONG).show();
            }
            return;
        }

        if (isLoading && !hasItems) {
            recyclerView.setVisibility(View.GONE);
            stateContainer.setVisibility(View.GONE);
        }
    }

    private void renderList(List<Character> characters) {
        recyclerView.setVisibility(View.VISIBLE);
        stateContainer.setVisibility(View.GONE);
        adapter.submitList(characters);
    }

    private void setFiltersEnabled(boolean enabled) {
        applyFiltersButton.setEnabled(enabled);
        toggleFiltersButton.setEnabled(enabled);
        statusAutoComplete.setEnabled(enabled);
        genderAutoComplete.setEnabled(enabled);
        speciesAutoComplete.setEnabled(enabled);
        clearFiltersButton.setEnabled(enabled);
    }

    private void openCharacterProfile(Character character) {
        Intent intent = new Intent(this, CharacterProfileActivity.class);
        intent.putExtra(CharacterProfileActivity.EXTRA_CHARACTER, character);
        startActivity(intent);
    }

    private void clearFilters() {
        statusAutoComplete.setText("", false);
        genderAutoComplete.setText("", false);
        speciesAutoComplete.setText("", false);
        updateFiltersSummary();
        setFiltersExpanded(false);
        viewModel.applyFilters("", "", "");
    }

    private void setFiltersExpanded(boolean expanded) {
        areFiltersExpanded = expanded;
        filtersPanelContainer.setVisibility(expanded ? View.VISIBLE : View.GONE);
        toggleFiltersButton.setText(expanded ? R.string.characters_filters_hide : R.string.characters_filters_show);
    }

    private void updateFiltersSummary() {
        String status = normalizeFilterValue(statusAutoComplete.getText().toString());
        String gender = normalizeFilterValue(genderAutoComplete.getText().toString());
        String species = normalizeFilterValue(speciesAutoComplete.getText().toString());

        boolean hasActiveFilters = !status.isEmpty() || !gender.isEmpty() || !species.isEmpty();
        if (!hasActiveFilters) {
            filtersSummaryTextView.setText(R.string.characters_filters_inactive_summary);
            return;
        }

        filtersSummaryTextView.setText(getString(
                R.string.characters_filters_active_summary,
                status.isEmpty() ? getString(R.string.characters_filters_any) : status,
                gender.isEmpty() ? getString(R.string.characters_filters_any) : gender,
                species.isEmpty() ? getString(R.string.characters_filters_any) : species
        ));
    }

    private String normalizeFilterValue(String value) {
        return value == null ? "" : value.trim();
    }
}
