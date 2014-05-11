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
    public void writeTransitions(ClassifiedState state, Map<ClassifiedState, Double> successors, Output output);

}
