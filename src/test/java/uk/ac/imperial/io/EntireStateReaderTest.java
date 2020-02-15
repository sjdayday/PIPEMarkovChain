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
import java.util.*;

import static org.junit.Assert.assertEquals;

public class EntireStateReaderTest {

    EntireStateReader reader;

    KryoStateIO kryoIo;

    @Before
    public void setUp() {
        kryoIo = new KryoStateIO();
        reader = new EntireStateReader(kryoIo);
    }

    @Test
    public void singleRecord() throws IOException {
        Map<Integer, Double> successors = new HashMap<>();
        successors.put(2, 1.0);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try (Output outputStream = new Output(stream);) {
                kryoIo.writeTransitions(1, successors, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                    Input inputStream = new Input(s)) {
                Collection<Record> records = reader.readRecords(inputStream);
                assertEquals(1, records.size());

                Record record = records.iterator().next();
                assertEquals(1, record.state);
                assertEquals(successors, record.successors);
            }
        }
    }

    @Test
    public void twoRecords() throws IOException {
        Map<Integer, Double> successors = new HashMap<>();
        successors.put(2, 1.0);

        Map<Integer, Double> successors2 = new HashMap<>();
        successors2.put(1, 2.0);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try (Output outputStream = new Output(stream)) {
                kryoIo.writeTransitions(1, successors, outputStream);
                kryoIo.writeTransitions(2, successors2, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                    Input inputStream = new Input(s)) {
                List<Record> records = new LinkedList<>(reader.readRecords(inputStream));
                assertEquals(2, records.size());

                Record record = records.get(0);
                assertEquals(1, record.state);
                assertEquals(successors, record.successors);

                Record record2 = records.get(1);
                assertEquals(2, record2.state);
                assertEquals(successors2, record2.successors);
            }
        }
    }

    @Test
    public void readState() throws IOException {
        ClassifiedState state = StateUtils
                .tangibleStateFromJson("{\"P0\": {\"Default\": 1}, \"P1\": {\"Default\": 0}}");

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try (Output outputStream = new Output(stream)) {
                kryoIo.writeState(state, 1, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                    Input inputStream = new Input(s)) {
                Map<Integer, ClassifiedState> mappings = reader.readStates(inputStream);
                assertEquals(1, mappings.size());
                assertEquals(state, mappings.get(1));
            }
        }
    }

    @Test
    public void twoStates() throws IOException {
        ClassifiedState state = StateUtils
                .tangibleStateFromJson("{\"P0\": {\"Default\": 1}, \"P1\": {\"Default\": 0}}");
        ClassifiedState state2 = StateUtils
                .tangibleStateFromJson("{\"P0\": {\"Default\": 0}, \"P1\": {\"Default\": 1}}");

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try (Output outputStream = new Output(stream)) {
                kryoIo.writeState(state, 1, outputStream);
                kryoIo.writeState(state2, 2, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                    Input inputStream = new Input(s)) {
                Map<Integer, ClassifiedState> mappings = reader.readStates(inputStream);
                assertEquals(2, mappings.size());
                assertEquals(state, mappings.get(1));
                assertEquals(state2, mappings.get(2));
            }
        }
    }

}