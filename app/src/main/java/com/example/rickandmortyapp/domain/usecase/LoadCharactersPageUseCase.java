package com.example.rickandmortyapp.domain.usecase;

import com.example.rickandmortyapp.data.model.character.CharacterResponse;
import com.example.rickandmortyapp.data.repository.CharacterRepository;
import com.example.rickandmortyapp.util.CharacterValueMapper;
import com.example.rickandmortyapp.util.Constants;
import com.example.rickandmortyapp.util.NetworkCallback;

public class LoadCharactersPageUseCase {
    private final CharacterRepository repository;

    public LoadCharactersPageUseCase(CharacterRepository repository) {
        this.repository = repository;
    }

    public boolean canLoadNextPage(int currentPage, boolean isLoading, boolean hasReachedPageLimit) {
        return !isLoading && !hasReachedPageLimit && currentPage < Constants.MAX_CHARACTER_PAGES;
    }

    public CharacterFilters createFilters(String status, String gender, String species) {
        return new CharacterFilters(
                CharacterValueMapper.toApiStatus(status),
                CharacterValueMapper.toApiGender(gender),
                CharacterValueMapper.toApiSpecies(species)
        );
    }

    public void execute(int page, CharacterFilters filters, NetworkCallback<CharacterResponse> callback) {
        repository.fetchCharacters(page, filters.getStatus(), filters.getGender(), filters.getSpecies(), callback);
    }
    public static class CharacterFilters {
        private final String status;
        private final String gender;
        private final String species;

        public CharacterFilters(String status, String gender, String species) {
            this.status = status;
            this.gender = gender;
            this.species = species;
        }

        public String getStatus() {
            return status;
        }

        public String getGender() {
            return gender;
        }

        public String getSpecies() {
            return species;
        }
    }
}
