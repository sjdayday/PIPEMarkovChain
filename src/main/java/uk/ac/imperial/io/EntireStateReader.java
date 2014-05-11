package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Input;
import uk.ac.imperial.state.Record;

import java.util.Collection;
import java.util.LinkedList;

/**
 * This class reads all the records in an input stream
 */
public class EntireStateReader implements  MultiStateReader {

    private final StateReader reader;

    public EntireStateReader(StateReader reader) {
        this.reader = reader;
    }

    /**
     * Processes all records in the input
     * @param input
     * @return collection of records
     */
    @Override
    public Collection<Record> readRecords(Input input) {
        Collection<Record> results = new LinkedList<>();
        while(!input.eof()) {
                Record record = reader.readRecord(input);
                results.add(record);
        }
        return results;
    }
}
