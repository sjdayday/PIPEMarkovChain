package uk.ac.imperial.io;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.HashedClassifiedState;
import uk.ac.imperial.state.HashedState;
import uk.ac.imperial.state.Record;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation that uses Kryo external library to serialize objects
 *
 * Using the Kryo external library means that objects do not have to implement
 * the Serializable interface
 *
 * Can read and write state objects
 */
public class KryoStateIO implements StateWriter, StateReader {

    private Kryo kryo = new Kryo();

    /**
     * Constructor
     *
     * Registers commonly used classes to improve efficiency
     * of writing a class name during writeTransitions
     */
    public KryoStateIO() {
        kryo.register(HashedState.class);
        kryo.register(HashedClassifiedState.class);
    }


    /**
     *
     * Writes:
     *   - State
     *   - Number of successors
     *   - {
     *      - Successor
     *      - Rate into Successor
     *     }
     *
     * @param state state
     * @param successors successors of state with the rate at which they are entered
     * @param output Kryo output to write to
     */
    @Override
    public void writeTransitions(ClassifiedState state, Map<ClassifiedState, Double> successors, Output output) {
        kryo.writeObject(output, state);
        kryo.writeObject(output, successors.size());
        for (Map.Entry<ClassifiedState, Double> entry : successors.entrySet()) {
            kryo.writeObject(output, entry.getKey());
            kryo.writeObject(output, entry.getValue());
        }
    }
    /**
     *
     * Reads:
     *   - State
     *   - Number of successors
     *   - {
     *      - Successor
     *      - Rate into Successor
     *     }
     *
     * @param input Kryo intput to read from
     */
    @Override
    public Record readRecord(Input input) {
        HashedClassifiedState state = kryo.readObject(input, HashedClassifiedState.class);
        int successors = kryo.readObject(input, Integer.class);
        Map<ClassifiedState, Double> successorRates = new HashMap<>();
        for (int i = 0; i < successors; i++) {
            ClassifiedState successor = kryo.readObject(input, HashedClassifiedState.class);
            Double rate = kryo.readObject(input, Double.class);
            successorRates.put(successor, rate);
        }
        return new Record(state, successorRates);
    }
}
