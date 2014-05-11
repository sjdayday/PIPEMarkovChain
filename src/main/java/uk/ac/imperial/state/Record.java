package uk.ac.imperial.state;

import uk.ac.imperial.state.ClassifiedState;

import java.util.Map;

/**
 * Record struct for storing state transitions
 */
public class Record {
    /**
     * State
     */
    public final ClassifiedState state;

    /**
     * States that the specified state field can transition to
     * and the rate in which it enters them
     */
    public final Map<ClassifiedState, Double> successors;

    public Record(ClassifiedState state, Map<ClassifiedState, Double> successors) {
        this.state = state;
        this.successors = successors;
    }
}
