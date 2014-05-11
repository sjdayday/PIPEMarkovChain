package uk.ac.imperial.io;

import uk.ac.imperial.state.ClassifiedState;

import java.util.Map;

/**
 * This interface will process state transitions in whatever way
 * the underlying implementation likes
 */
public interface StateProcessor {
    public void processTransitions(ClassifiedState state, Map<ClassifiedState, Double> successorRates);
}
