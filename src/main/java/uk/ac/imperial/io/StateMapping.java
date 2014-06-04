package uk.ac.imperial.io;

import uk.ac.imperial.state.ClassifiedState;

/**
 * Simple struct that contains the mapping of an id to state
 */
public class StateMapping {

    /**
     * A given state in the state space exploration graph
     */
    public final ClassifiedState state;

    /**
     * Id that the state has been assigned
     */
    public final int id;

    /**
     * Stores the state and id for retrieval later
     * @param state
     * @param id
     */
    public StateMapping(ClassifiedState state, int id) {
        this.state = state;
        this.id = id;
    }
}
