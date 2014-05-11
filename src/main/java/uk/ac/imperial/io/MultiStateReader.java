package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Input;
import uk.ac.imperial.state.Record;

import java.util.Collection;

/**
 * Class that digests multiple states from an Input stream
 */
public interface MultiStateReader {
    public Collection<Record> readRecords(Input input);
}
