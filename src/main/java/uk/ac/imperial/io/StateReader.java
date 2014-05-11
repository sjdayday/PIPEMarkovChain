package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Input;
import uk.ac.imperial.state.Record;


public interface StateReader {

    /**
     * Read a single record from the input stream
     * @param input
     * @return Record containing a state and all its successors
     */
    public Record readRecord(Input input);
}
