package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Output;
import uk.ac.imperial.state.ClassifiedState;

import java.util.Map;

/**
 * Contains
 */
public interface StateWriter {

    /**
     * Writes classified state transitions
     * @param state
     * @param successors
     */
    public void writeTransitions(int state, Map<Integer, Double> successors, Output output);

    /**
     * Logs state to state id in output
     * @param state
     * @param stateId
     * @param stateOutput
     */
    void writeState(ClassifiedState state, int stateId, Output stateOutput);
}
