package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Input;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class reads all the records in an input stream
 */
public final class EntireStateReader implements MultiStateReader {

    /**
     * State reader for reading a single record
     */
    private final StateReader reader;

    /**
     *
     * @param reader single record reader to be used to read the entire input
     */
    public EntireStateReader(StateReader reader) {
        this.reader = reader;
    }

    /**
     * Processes all records in the input stream
     *
     * @param input input stream containing written records
     * @return collection of records
     */
    @Override
    public Collection<Record> readRecords(Input input) throws IOException {
        Collection<Record> results = new ArrayList<>();
        while (!input.eof()) {
            Record record = reader.readRecord(input);
            results.add(record);
        }
        return results;
    }

    /**
     * Reads the state mappings input stream that contains a map of an integer to a serialized
     * state. These integers represent those that are in the records input stream
     *
     * @param input input stream containing written state mappings of integer to serialized state
     * @return map of integer state representation to the classified state. These integers can be mapped
     *         directly to the transitions
     */
    @Override
    public Map<Integer, ClassifiedState> readStates(Input input) {
        Map<Integer, ClassifiedState> mappings = new HashMap<>();
        while (!input.eof()) {
            StateMapping stateMapping = reader.readState(input);
            mappings.put(stateMapping.id, stateMapping.state);
        }
        return mappings;
    }
}
