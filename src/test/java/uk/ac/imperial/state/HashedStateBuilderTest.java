package uk.ac.imperial.state;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class HashedStateBuilderTest {

    HashedStateBuilder builder;

    @Before
    public void setUp() {
        builder = new HashedStateBuilder();
    }

    @Test
    public void simple() {
        builder.placeWithToken("P1", "Default", 1);
        HashedState state = builder.build();
        Map<String, Integer> map = state.getTokens("P1");
        int actual = map.get("Default");
        assertEquals(1, actual);
    }

    @Test
    public void containsToken() {
        builder.placeWithToken("P1", "Default", 1);
        HashedState state = builder.build();
        assertTrue(state.containsTokens("P1"));
    }


    @Test
    public void doesNotContainToken() {
        builder.placeWithToken("P1", "Default", 1);
        HashedState state = builder.build();
        assertFalse(state.containsTokens("P0"));
    }

    @Test
    public void simpleOverwrites() {
        builder.placeWithToken("P1", "Default", 1);
        builder.placeWithToken("P1", "Default", 6);
        HashedState state = builder.build();
        Map<String, Integer> map = state.getTokens("P1");
        int actual = map.get("Default");
        assertEquals(6, actual);
    }

    @Test
    public void multipleColoredTokens() {
        builder.placeWithToken("P1", "Default", 1);
        builder.placeWithToken("P1", "Red", 6);
        HashedState state = builder.build();
        Map<String, Integer> map = state.getTokens("P1");
        assertEquals(2, map.size());

        int actualDefault = map.get("Default");
        assertEquals(1, actualDefault);
        int actualRed = map.get("Red");
        assertEquals(6, actualRed);
    }

    @Test
    public void multiplePlaces() {
        builder.placeWithToken("P1", "Default", 1);
        builder.placeWithToken("P2", "Default", 6);
        HashedState state = builder.build();
        Map<String, Integer> mapP1 = state.getTokens("P1");
        int actualP1 = mapP1.get("Default");
        assertEquals(1, actualP1);
        Map<String, Integer> mapP2 = state.getTokens("P2");
        int actualP2 = mapP2.get("Default");
        assertEquals(6, actualP2);

    }

    @Test
    public void addMultipleTokens() {
        Map<String, Integer> tokens = new HashMap<>();
        tokens.put("Default", 1);
        tokens.put("Red", 6);
        builder.placeWithTokens("P1", tokens);
        HashedState state = builder.build();
        Map<String, Integer> map = state.getTokens("P1");
        assertEquals(2, map.size());

        int actualDefault = map.get("Default");
        assertEquals(1, actualDefault);
        int actualRed = map.get("Red");
        assertEquals(6, actualRed);

    }



}