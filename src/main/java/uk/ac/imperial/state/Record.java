package uk.ac.imperial.state;

import java.util.Map;

/**
 * Immutable record struct for storing state transitions
 * in integer representation
 */
public class Record {
    /**
     * State
     */
    public final int state;

    /**
     * States that the specified state field can transition to
     * and the rate in which it enters them
     */
    public final Map<Integer, Double> successors;

    /**
     * Constructor storing a state and successor for future retrial
     * @param state
     * @param successors
     */
    public Record(int state, Map<Integer, Double> successors) {
        this.state = state;
        this.successors = successors;
    }
}
