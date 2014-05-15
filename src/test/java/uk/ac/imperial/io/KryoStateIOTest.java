package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Before;
import org.junit.Test;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.Record;
import uk.ac.imperial.utils.StateUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class KryoStateIOTest {
    private KryoStateIO io;


    @Before
    public void setUp() {
        io = new KryoStateIO();
    }

    @Test
    public void singleSuccessor() throws IOException {
        Map<Integer, Double> successors = new HashMap<>();
        successors.put(2, 1.0);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try  (Output outputStream = new Output(stream)) {
                io.writeTransitions(1, successors, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                 Input inputStream = new Input(s)) {
                Record record = io.readRecord(inputStream);

                assertEquals(1, record.state);
                assertEquals(successors, record.successors);
            }
        }
    }

    @Test
    public void doubleSuccessor() throws IOException {
        Map<Integer, Double> successors = new HashMap<>();
        successors.put(2, 1.0);
        successors.put(3, 2.0);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try  (Output outputStream = new Output(stream)) {
                io.writeTransitions(1, successors, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                 Input inputStream = new Input(s)) {
                Record record = io.readRecord(inputStream);

                assertEquals(1, record.state);
                assertEquals(successors, record.successors);
            }
        }
    }

    @Test
    public void writesState() throws IOException {
        ClassifiedState state =
            StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 1}, \"P1\": {\"Default\": 0}}");
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try  (Output outputStream = new Output(stream)) {
                io.writeState(state, 1, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                 Input inputStream = new Input(s)) {
                StateMapping mapping = io.readState(inputStream);

                assertEquals(1, mapping.id);
                assertEquals(state, mapping.state);
            }
        }
    }


}