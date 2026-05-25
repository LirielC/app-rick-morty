package com.example.rickandmortyapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rickandmortyapp.data.model.character.Character;
import com.example.rickandmortyapp.data.model.character.CharacterResponse;
import com.example.rickandmortyapp.data.repository.CharacterRepository;
import com.example.rickandmortyapp.domain.usecase.LoadCharactersPageUseCase;
import com.example.rickandmortyapp.util.Constants;
import com.example.rickandmortyapp.util.NetworkCallback;
import com.example.rickandmortyapp.util.UiState;

import java.util.ArrayList;
import java.util.List;

public class CharacterListViewModel extends ViewModel {
    private final MutableLiveData<UiState<List<Character>>> charactersState = new MutableLiveData<>(UiState.idle());
    private final CharacterRepository repository = new CharacterRepository();
    private final LoadCharactersPageUseCase loadCharactersPageUseCase = new LoadCharactersPageUseCase(repository);
    private final List<Character> loadedCharacters = new ArrayList<>();
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasReachedPageLimit = false;
    private LoadCharactersPageUseCase.CharacterFilters currentFilters =
            new LoadCharactersPageUseCase.CharacterFilters("", "", "");

    public LiveData<UiState<List<Character>>> getCharactersState() {
        return charactersState;
    }

    public void loadFirstPage() {
        currentPage = 0;
        hasReachedPageLimit = false;
        loadedCharacters.clear();
        fetchPage(1, true);
    }

    public void loadNextPage() {
        if (!loadCharactersPageUseCase.canLoadNextPage(currentPage, isLoading, hasReachedPageLimit)) {
            return;
        }
        fetchPage(currentPage + 1, false);
    }

    public void applyFilters(String status, String gender, String species) {
        currentFilters = loadCharactersPageUseCase.createFilters(status, gender, species);
        loadFirstPage();
    }

    public boolean hasLoadedCharacters() {
        return !loadedCharacters.isEmpty();
    }

    private void fetchPage(int page, boolean reset) {
        isLoading = true;
        if (reset || loadedCharacters.isEmpty()) {
            charactersState.setValue(UiState.loading());
        } else {
            charactersState.setValue(UiState.success(new ArrayList<>(loadedCharacters)));
        }
        loadCharactersPageUseCase.execute(page, currentFilters, new NetworkCallback<>() {
            @Override
            public void onSuccess(CharacterResponse data) {
                if (reset) {
                    loadedCharacters.clear();
                }
                if (data.getResults() != null) {
                    loadedCharacters.addAll(data.getResults());
                }
                currentPage = page;
                hasReachedPageLimit = currentPage >= Constants.MAX_CHARACTER_PAGES
                        || data.getInfo() == null
                        || data.getInfo().getNext() == null
                        || data.getInfo().getNext().isEmpty();
                isLoading = false;
                charactersState.postValue(UiState.success(new ArrayList<>(loadedCharacters)));
            }

            @Override
            public void onEmpty(String message) {
                isLoading = false;
                if (reset) {
                    loadedCharacters.clear();
                    hasReachedPageLimit = false;
                    currentPage = 0;
                    charactersState.postValue(UiState.empty(message));
                    return;
                }
                hasReachedPageLimit = true;
                charactersState.postValue(UiState.success(new ArrayList<>(loadedCharacters)));
            }

            @Override
            public void onError(String message) {
                isLoading = false;
                if (loadedCharacters.isEmpty()) {
                    charactersState.postValue(UiState.error(message));
                } else {
                    charactersState.postValue(UiState.success(new ArrayList<>(loadedCharacters)));
                }
            }
        });
    }
}
