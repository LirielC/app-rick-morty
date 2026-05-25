package com.example.rickandmortyapp.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EventTest {

    @Test
    public void getContentIfNotHandled_returnsValueOnlyOnce() {
        Event<String> event = new Event<>("mensagem");

        assertEquals("mensagem", event.getContentIfNotHandled());
        assertNull(event.getContentIfNotHandled());
    }

    @Test
    public void peek_doesNotConsumeEvent() {
        Event<Integer> event = new Event<>(42);

        assertEquals(Integer.valueOf(42), event.peek());
        assertEquals(Integer.valueOf(42), event.getContentIfNotHandled());
        assertNull(event.getContentIfNotHandled());
    }
}
