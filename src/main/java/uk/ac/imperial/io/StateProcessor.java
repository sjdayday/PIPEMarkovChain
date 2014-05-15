package uk.ac.imperial.io;

import uk.ac.imperial.state.ClassifiedState;

import java.util.Map;

/**
 * This interface will process state transitions in whatever way
 * the underlying implementation likes
 */
public interface StateProcessor {

    /**
     *
     * Process transitions from state to successor rates. Processing happens in
     * integer form since the name of the state is not needed
     *
     * @param stateId unique id for state
     * @param successorRates unique id for successors to the rate at which they are enterd
     */
    public void processTransitions(int stateId, Map<Integer, Double> successorRates);

    /**
     *
     * Process the assigning of an id to a state
     *
     * @param state
     * @param stateId
     */
    public void processState(ClassifiedState state, int stateId);

}
