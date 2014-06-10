package uk.ac.imperial.utils;

import org.junit.Test;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.HashedClassifiedState;
import uk.ac.imperial.state.HashedStateBuilder;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ExploredSetTest {
    ClassifiedState explorerState;

    ExploredSet set;

    @org.junit.Before
    public void setUp() {

        explorerState = createState(1, 2);

    }

    /**
     * @param p1Tokens
     * @param p2Tokens
     * @return State representation of a state with two places P1, P2 with the specified number of Default tokens
     */
    public ClassifiedState createState(int p1Tokens, int p2Tokens) {
        HashedStateBuilder builder = new HashedStateBuilder();
        builder.placeWithToken("P1", "Default", p1Tokens);
        builder.placeWithToken("P2", "Default", p2Tokens);
        return HashedClassifiedState.tangibleState(builder.build());
    }

    @Test
    public void containsEmpty() {
        set = new ExploredSet(10);
        assertFalse(set.contains(explorerState));
    }

    @Test
    public void containsExactItem() {
        set = new ExploredSet(10);
        set.add(explorerState, 1);
        assertTrue(set.contains(explorerState));
    }

    @Test
    public void containsDuplicateItem() {
        set = new ExploredSet(10);
        set.add(explorerState, 1);
        ClassifiedState sameState = createState(1, 2);
        assertTrue(set.contains(sameState));
    }

    @Test
    public void containsDuplicateItemOppositeOrderHashMap() {
        set = new ExploredSet(10);
        set.add(explorerState, 1);
        ClassifiedState sameState = createStateOppositeOrder(1, 2);
        assertTrue(set.contains(sameState));
    }

    /**
     * @param p1Tokens
     * @param p2Tokens
     * @return State representation of a state with two places P1, P2 with the specified number of Default tokens
     */
    public ClassifiedState createStateOppositeOrder(int p1Tokens, int p2Tokens) {
        HashedStateBuilder builder = new HashedStateBuilder();
        builder.placeWithToken("P2", "Default", p2Tokens);
        builder.placeWithToken("P1", "Default", p1Tokens);
        return HashedClassifiedState.tangibleState(builder.build());

    }

    @Test
    public void doesNotContainDifferentItem() {
        set = new ExploredSet(10);
        set.add(explorerState, 1);
        ClassifiedState differentState = createState(22, 1);
        assertFalse(set.contains(differentState));
    }

    @Test
    public void addAll() {
        set = new ExploredSet(10);
        ClassifiedState other = createState(2, 10);
        ClassifiedState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another), Arrays.asList(1, 2, 3));
        assertTrue(set.contains(explorerState));
        assertTrue(set.contains(other));
        assertTrue(set.contains(another));
    }

    @Test
    public void clear() {
        set = new ExploredSet(10);
        ClassifiedState other = createState(2, 10);
        ClassifiedState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another), Arrays.asList(1, 2, 3));
        set.clear();
        assertFalse(set.contains(explorerState));
        assertFalse(set.contains(another));
        assertFalse(set.contains(explorerState));
    }

    /**
     * This test was found when exploring medium_complex_5832,
     * ExploredSet was returning false for containing the item
     * when it had been added to the state
     */
    @Test
    public void containsLargeState() throws IOException {
        String jsonValue =
                "{\"P12\": {\"Default\": 0}, \"P11\": {\"Default\": 1}, \"P14\": {\"Default\": 1}, \"P13\": {\"Default\": 0}, \"P9\": {\"Default\": 0}, \"P16\": {\"Default\": 1}, \"P15\": {\"Default\": 0}, \"P8\": {\"Default\": 1}, \"P17\": {\"Default\": 0}, \"P5\": {\"Default\": 0}, \"P4\": {\"Default\": 0}, \"P7\": {\"Default\": 0}, \"P6\": {\"Default\": 0}, \"P1\": {\"Default\": 0}, \"P0\": {\"Default\": 1}, \"P3\": {\"Default\": 0}, \"P2\": {\"Default\": 1}, \"P10\": {\"Default\": 0}}";
        ClassifiedState state = StateUtils.tangibleStateFromJson(jsonValue);

        set = new ExploredSet(10);
        set.add(state, 1);
        ClassifiedState sameState = StateUtils.tangibleStateFromJson(jsonValue);
        assertTrue(set.contains(sameState));
    }

    /**
     * This test was added because during debugging these two states cause problems
     * for the contains method
     * @throws IOException
     */
    @Test
    public void similarStates() throws IOException {
        ClassifiedState state = StateUtils.tangibleStateFromJson("{\"P1\": {\"Default\": 1}, \"P0\": {\"Default\": 0}, \"P3\": {\"Default\": 0}, \"P2\": {\"Default\": 1}}");
        ClassifiedState state2 = StateUtils.tangibleStateFromJson("{\"P1\": {\"Default\": 0}, \"P0\": {\"Default\": 1}, \"P3\": {\"Default\": 1}, \"P2\": {\"Default\": 0}}");

        set = new ExploredSet(10);
        set.add(state, 1);
        assertFalse(set.contains(state2));
    }

    @Test
    public void idProblems() throws IOException {
       ClassifiedState state = StateUtils.tangibleStateFromJson( "{\"P0\" : { \"Default\" : 0, \"Red\" : 1 }, \"P1\" : { \"Default\" : 1, \"Red\" : 0 } }");
       ClassifiedState state2 = StateUtils.tangibleStateFromJson( "{\"P0\" : { \"Default\" : 1, \"Red\" : 0 }, \"P1\" : { \"Default\" : 0, \"Red\" : 1 } }");

       set = new ExploredSet(10);
       set.add(state, 1);
       set.add(state2, 2);

       assertEquals(1, set.getId(state));
       assertEquals(2, set.getId(state2));

    }

    @Test
    public void getsId() {
        set = new ExploredSet(10);
        ClassifiedState other = createState(2, 10);
        ClassifiedState another = createState(2, 7);
        set.addAll(Arrays.asList(explorerState, other, another), Arrays.asList(1, 2, 3));
        assertEquals(1, set.getId(explorerState));
        assertEquals(2, set.getId(other));
        assertEquals(3, set.getId(another));
    }

    @Test
    public void empty() {
        set = new ExploredSet(10);
        assertEquals(0, set.size());
    }

    @Test
    public void singleItem() {
        set = new ExploredSet(10);
        set.add(createState(2, 10), 1);
        assertEquals(1, set.size());
    }


    @Test
    public void manyItems() {
        set = new ExploredSet(10);
        for (int i = 0; i < 40; i++) {
            set.add(createState(0, i), i);
        }
        assertEquals(40, set.size());
    }

    @Test
    public void duplicateItemSize() {
        set = new ExploredSet(10);
        set.add(createState(2, 10), 1);
        set.add(createState(2, 10), 1);
        assertEquals(1, set.size());
    }

    @Test
    public void duplicateItemDifferentIdSize() {
        set = new ExploredSet(10);
        set.add(createState(2, 10), 1);
        set.add(createState(2, 10), 2);
        assertEquals(1, set.size());
    }


    @Test
    public void duplicateItemDifferentId() {
        set = new ExploredSet(10);
        ClassifiedState state = createState(2, 10);
        set.add(state, 1);
        set.add(state, 2);
        assertEquals(2, set.getId(state));
    }

    @Test
    public void containsComplex() throws IOException {
        ClassifiedState state = StateUtils.tangibleStateFromJson("{\"P28\": {\"Default\": 0}, \"P29\": {\"Default\": 0}, \"P26\": {\"Default\": 0}, \"P27\": {\"Default\": 0}, \"P24\": {\"Default\": 1}, \"P25\": {\"Default\": 0}, \"P22\": {\"Default\": 0}, \"P23\": {\"Default\": 1}, \"P21\": {\"Default\": 0}, \"P20\": {\"Default\": 0}, \"P12\": {\"Default\": 0}, \"P11\": {\"Default\": 0}, \"P14\": {\"Default\": 0}, \"P13\": {\"Default\": 1}, \"P16\": {\"Default\": 0}, \"P9\": {\"Default\": 0}, \"P8\": {\"Default\": 0}, \"P15\": {\"Default\": 0}, \"P18\": {\"Default\": 0}, \"P17\": {\"Default\": 0}, \"P5\": {\"Default\": 0}, \"P4\": {\"Default\": 0}, \"P19\": {\"Default\": 0}, \"P7\": {\"Default\": 0}, \"P6\": {\"Default\": 0}, \"P1\": {\"Default\": 0}, \"P33\": {\"Default\": 0}, \"P0\": {\"Default\": 1}, \"P34\": {\"Default\": 0}, \"P3\": {\"Default\": 0}, \"P2\": {\"Default\": 0}, \"P30\": {\"Default\": 0}, \"P32\": {\"Default\": 1}, \"P31\": {\"Default\": 0}, \"P10\": {\"Default\": 0}}");
        ClassifiedState state2 =  StateUtils.tangibleStateFromJson("{\"P28\": {\"Default\": 0}, \"P29\": {\"Default\": 0}, \"P26\": {\"Default\": 1}, \"P27\": {\"Default\": 0}, \"P24\": {\"Default\": 0}, \"P25\": {\"Default\": 0}, \"P22\": {\"Default\": 0}, \"P23\": {\"Default\": 0}, \"P21\": {\"Default\": 0}, \"P20\": {\"Default\": 0}, \"P12\": {\"Default\": 0}, \"P11\": {\"Default\": 0}, \"P14\": {\"Default\": 1}, \"P13\": {\"Default\": 0}, \"P9\": {\"Default\": 0}, \"P16\": {\"Default\": 0}, \"P15\": {\"Default\": 0}, \"P8\": {\"Default\": 0}, \"P18\": {\"Default\": 1}, \"P17\": {\"Default\": 0}, \"P5\": {\"Default\": 0}, \"P19\": {\"Default\": 0}, \"P4\": {\"Default\": 0}, \"P7\": {\"Default\": 0}, \"P6\": {\"Default\": 0}, \"P1\": {\"Default\": 1}, \"P33\": {\"Default\": 0}, \"P0\": {\"Default\": 0}, \"P34\": {\"Default\": 1}, \"P3\": {\"Default\": 0}, \"P2\": {\"Default\": 0}, \"P30\": {\"Default\": 0}, \"P32\": {\"Default\": 0}, \"P31\": {\"Default\": 0}, \"P10\": {\"Default\": 0}}");
        set = new ExploredSet(300007);
        set.add(state, 1);
        assertFalse(set.contains(state2));
    }

}