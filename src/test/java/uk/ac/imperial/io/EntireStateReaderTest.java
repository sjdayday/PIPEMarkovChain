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
        ClassifiedState state =
                StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 1}, \"P1\": {\"Default\": 0}}");
        ClassifiedState successor =
                StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 0}, \"P1\": {\"Default\": 1}}");
        Map<ClassifiedState, Double> successors = new HashMap<>();
        successors.put(successor, 1.0);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try  (Output outputStream = new Output(stream)) {
                kryoIo.writeTransitions(state, successors, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                 Input inputStream = new Input(s)) {
                Collection<Record> records = reader.readRecords(inputStream);
                assertEquals(1, records.size());

                Record record = records.iterator().next();
                assertEquals(state, record.state);
                assertEquals(successors, record.successors);
            }
        }
    }

    @Test
    public void twoRecords() throws IOException {
        ClassifiedState state =
                StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 1}, \"P1\": {\"Default\": 0}}");
        ClassifiedState successor =
                StateUtils.tangibleStateFromJson("{\"P0\": {\"Default\": 0}, \"P1\": {\"Default\": 1}}");
        Map<ClassifiedState, Double> successors = new HashMap<>();
        successors.put(successor, 1.0);

        Map<ClassifiedState, Double> successors2 = new HashMap<>();
        successors2.put(state, 2.0);

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try  (Output outputStream = new Output(stream)) {
                kryoIo.writeTransitions(state, successors, outputStream);
                kryoIo.writeTransitions(successor, successors2, outputStream);
            }
            try (ByteArrayInputStream s = new ByteArrayInputStream(stream.toByteArray());
                 Input inputStream = new Input(s)) {
                List<Record> records = new LinkedList<>(reader.readRecords(inputStream));
                assertEquals(2, records.size());

                Record record = records.get(0);
                assertEquals(state, record.state);
                assertEquals(successors, record.successors);


                Record record2 = records.get(1);
                assertEquals(successor, record2.state);
                assertEquals(successors2, record2.successors);
            }
        }
    }

}