package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Input;
import uk.ac.imperial.state.Record;

public interface StateReader {

    public Record readRecord(Input input);
}
