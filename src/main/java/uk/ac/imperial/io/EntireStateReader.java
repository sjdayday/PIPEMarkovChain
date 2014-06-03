package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Input;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class reads all the records in an input stream
 */
public final class EntireStateReader implements MultiStateReader {

    private final StateReader reader;

    public EntireStateReader(StateReader reader) {
        this.reader = reader;
    }

    /**
     * Processes all records in the input
     *
     * @param input
     * @return collection of records
     */
    @Override
    public Collection<Record> readRecords(Input input) {
        Collection<Record> results = new ArrayList<>();
        while (!input.eof()) {
            Record record = reader.readRecord(input);
            results.add(record);
        }
        return results;
    }

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
