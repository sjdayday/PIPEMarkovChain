package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Output;
import uk.ac.imperial.state.ClassifiedState;

import java.util.Map;

/**
 * This class writes to a StateWriter the collected transitions
 */
public class StateIOProcessor implements StateProcessor {
    private final StateWriter writer;

    private final Output output;


    public StateIOProcessor(StateWriter writer, Output output) {
        this.writer = writer;
        this.output = output;
    }

    @Override
    public void processTransitions(ClassifiedState state, Map<ClassifiedState, Double> successorRates) {
        writer.writeTransitions(state, successorRates, output);
    }
}
