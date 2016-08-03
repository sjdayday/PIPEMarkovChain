package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Input;
import uk.ac.imperial.state.Record;

import java.io.IOException;


/**
 * State reader reads a single record from the input stream
 */
public interface StateReader {

    /**
     * Read a single record from the input stream
     * @param input source
     * @return Record containing a state and all its successors
     * @throws IOException if IO error occurs during the read
     */
    Record readRecord(Input input) throws IOException;

    /**
     * Reads a single state mapping from the input
     *
     * @param inputStream stream
     * @return object containing the id and the state
     */
    StateMapping readState(Input inputStream);
}
