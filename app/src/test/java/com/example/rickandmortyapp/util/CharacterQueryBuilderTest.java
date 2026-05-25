package com.example.rickandmortyapp.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CharacterQueryBuilderTest {

    @Test
    public void build_includesPage() {
        String url = CharacterQueryBuilder.build(2, "", null, "   ");

        assertEquals("https://rickandmortyapi.com/api/character?page=2", url);
    }

    @Test
    public void build_includesStatusWhenFilled() {
        String url = CharacterQueryBuilder.build(1, "alive", null, null);

        assertEquals("https://rickandmortyapi.com/api/character?page=1&status=alive", url);
    }

    @Test
    public void build_includesGenderWhenFilled() {
        String url = CharacterQueryBuilder.build(1, null, "female", null);

        assertEquals("https://rickandmortyapi.com/api/character?page=1&gender=female", url);
    }

    @Test
    public void build_includesSpeciesWhenFilled() {
        String url = CharacterQueryBuilder.build(1, null, null, " Human ");

        assertEquals("https://rickandmortyapi.com/api/character?page=1&species=Human", url);
    }

    @Test
    public void build_ignoresEmptyOrNullFilters() {
        String url = CharacterQueryBuilder.build(1, " ", null, "");

        assertEquals("https://rickandmortyapi.com/api/character?page=1", url);
    }

    @Test
    public void build_combinesAllFilledFilters() {
        String url = CharacterQueryBuilder.build(1, "alive", "female", " Human ");

        assertEquals(
                "https://rickandmortyapi.com/api/character?page=1&status=alive&gender=female&species=Human",
                url
        );
    }
}
