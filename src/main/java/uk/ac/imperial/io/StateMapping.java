package uk.ac.imperial.io;

import uk.ac.imperial.state.ClassifiedState;

/**
 * Contains the mapping of an id to state
 */
public class StateMapping {
    public final ClassifiedState state;
    public final int id;

    public StateMapping(ClassifiedState state, int id) {
        this.state = state;
        this.id = id;
    }
}
