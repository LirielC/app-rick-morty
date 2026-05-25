package com.example.rickandmortyapp.domain.usecase;

import com.example.rickandmortyapp.data.model.character.CharacterResponse;
import com.example.rickandmortyapp.data.repository.CharacterRepository;
import com.example.rickandmortyapp.util.NetworkCallback;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LoadCharactersPageUseCaseTest {

    @Test
    public void canLoadNextPage_returnsTrueOnlyWhenWithinLimitAndIdle() {
        LoadCharactersPageUseCase useCase = new LoadCharactersPageUseCase(new FakeCharacterRepository());

        assertTrue(useCase.canLoadNextPage(1, false, false));
        assertFalse(useCase.canLoadNextPage(3, false, false));
        assertFalse(useCase.canLoadNextPage(1, true, false));
        assertFalse(useCase.canLoadNextPage(1, false, true));
    }

    @Test
    public void createFilters_normalizesSelectableFiltersAndTrimsSpecies() {
        LoadCharactersPageUseCase useCase = new LoadCharactersPageUseCase(new FakeCharacterRepository());

        LoadCharactersPageUseCase.CharacterFilters filters =
                useCase.createFilters("Todos", "Feminino", " Human ");

        assertEquals("", filters.getStatus());
        assertEquals("female", filters.getGender());
        assertEquals("Human", filters.getSpecies());
    }

    @Test
    public void execute_delegatesPageAndFiltersToRepository() {
        FakeCharacterRepository repository = new FakeCharacterRepository();
        LoadCharactersPageUseCase useCase = new LoadCharactersPageUseCase(repository);

        LoadCharactersPageUseCase.CharacterFilters filters =
                new LoadCharactersPageUseCase.CharacterFilters("alive", "male", "Human");

        RecordingCallback callback = new RecordingCallback();

        useCase.execute(2, filters, callback);

        assertEquals(2, repository.page);
        assertEquals("alive", repository.status);
        assertEquals("male", repository.gender);
        assertEquals("Human", repository.species);
        assertTrue(repository.called);
        assertNotNull(callback.successData);
    }

    private static class FakeCharacterRepository extends CharacterRepository {
        int page;
        String status;
        String gender;
        String species;
        boolean called;

        @Override
        public void fetchCharacters(int page, String status, String gender, String species,
                                    NetworkCallback<CharacterResponse> callback) {
            this.page = page;
            this.status = status;
            this.gender = gender;
            this.species = species;
            this.called = true;
            callback.onSuccess(new CharacterResponse());
        }
    }

    private static class RecordingCallback implements NetworkCallback<CharacterResponse> {
        CharacterResponse successData;

        @Override
        public void onSuccess(CharacterResponse data) {
            successData = data;
        }

        @Override
        public void onEmpty(String message) {
        }

        @Override
        public void onError(String message) {
        }
    }
}
