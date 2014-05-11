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
        ClassifiedState state =
                StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 1}, \"P1\": {\"Default\": 0}}");
        ClassifiedState successor =
                StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 0}, \"P1\": {\"Default\": 1}}");
        Map<ClassifiedState, Double> successors = new HashMap<>();
        successors.put(successor, 1.0);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try  (Output outputStream = new Output(stream)) {
                io.writeTransitions(state, successors, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                 Input inputStream = new Input(s)) {
                Record record = io.readRecord(inputStream);

                assertEquals(state, record.state);
                assertEquals(successors, record.successors);
            }
        }
    }

    @Test
    public void doubleSuccessor() throws IOException {
        ClassifiedState state =
                StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 2}, \"P1\": {\"Default\": 0}}");
        ClassifiedState successor1 =
                StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 1}, \"P1\": {\"Default\": 1}}");
        ClassifiedState successor2 =
                StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 0}, \"P1\": {\"Default\": 2}}");
        Map<ClassifiedState, Double> successors = new HashMap<>();
        successors.put(successor1, 1.0);
        successors.put(successor2, 2.0);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try  (Output outputStream = new Output(stream)) {
                io.writeTransitions(state, successors, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                 Input inputStream = new Input(s)) {
                Record record = io.readRecord(inputStream);

                assertEquals(state, record.state);
                assertEquals(successors, record.successors);
            }
        }
    }


}