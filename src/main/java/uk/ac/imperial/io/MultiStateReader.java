package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Input;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Class that digests multiple states from an Input stream
 */
public interface MultiStateReader {
    /**
     *
     * @param input stream to evaluate
     * @return state transitions in the input file
     * @throws IOException if IO error occurs during the read
     */
    Collection<Record> readRecords(Input input) throws IOException;

    /**
     *
     * @param input state file to evaluate
     * @return state mappings in the state file
     */
    Map<Integer, ClassifiedState> readStates(Input input);
}
