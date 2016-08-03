package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Output;
import uk.ac.imperial.state.ClassifiedState;

import java.util.Map;

/**
 * Contains methods for writing transitions and state mappings to output streams
 */
public interface StateWriter {

    /**
     * Writes classified state transitions
     * @param state to be written
     * @param successors transitions to be written
     * @param output output to be written to
     */
    void writeTransitions(int state, Map<Integer, Double> successors, Output output);

    /**
     * Logs state to state id in output
     * @param state to be written
     * @param stateId id of the state
     * @param stateOutput output to be written to
     */
    void writeState(ClassifiedState state, int stateId, Output stateOutput);
}
