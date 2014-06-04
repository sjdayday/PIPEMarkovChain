package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Output;
import uk.ac.imperial.state.ClassifiedState;

import java.util.Map;

/**
 * This class writes to a StateWriter the collected transitions
 */
public final class StateIOProcessor implements StateProcessor {
    /**
     * For writing single states and transitions out to a stream
     */
    private final StateWriter writer;

    /**
     * Transition output stream, used to represent state transition records
     * I.e. state 1 has 2 successors: (state 2 with rate 1.0) and (state 3 with rate 2.5)
     */
    private final Output transitionOutput;

    /**
     * State mapping output stream
     */
    private Output stateOutput;


    /**
     *
     * @param writer state writer that will write to the given output streams
     * @param transitionOutput for writing transition records to
     * @param stateOutput for writing the state mappings to
     */
    public StateIOProcessor(StateWriter writer, Output transitionOutput, Output stateOutput) {
        this.writer = writer;
        this.transitionOutput = transitionOutput;
        this.stateOutput = stateOutput;
    }

    /**
     *
     * Writes the record to the transition output stream
     *
     * @param stateId unique id for state
     * @param successorRates unique id for successors to the rate at which they are entered
     */
    @Override
    public void processTransitions(int stateId, Map<Integer, Double> successorRates) {
        writer.writeTransitions(stateId, successorRates, transitionOutput);
    }

    /**
     * Writes the state to the state mapping stream
     *
     * @param state state object
     * @param stateId id that has been assigned to this state and is used in the transition records
     */
    @Override
    public void processState(ClassifiedState state, int stateId) {
        writer.writeState(state, stateId, stateOutput);
    }
}
