package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Output;
import uk.ac.imperial.state.ClassifiedState;

import java.util.Map;

/**
 * This class writes to a StateWriter the collected transitions
 */
public final class StateIOProcessor implements StateProcessor {
    private final StateWriter writer;

    private final Output transitionOutput;
    private Output stateOutput;


    public StateIOProcessor(StateWriter writer, Output transitionOutput, Output stateOutput) {
        this.writer = writer;
        this.transitionOutput = transitionOutput;
        this.stateOutput = stateOutput;
    }

    @Override
    public void processTransitions(int stateId, Map<Integer, Double> successorRates) {
        writer.writeTransitions(stateId, successorRates, transitionOutput);
    }

    @Override
    public void processState(ClassifiedState state, int stateId) {
        writer.writeState(state, stateId, stateOutput);
    }
}
