package uk.ac.imperial.io;

import com.esotericsoftware.kryo.io.Output;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.imperial.state.ClassifiedState;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StateIOProcessorTest {

    @Mock
    private Output output;

    @Mock
    private StateWriter writer;

    @Mock
    private ClassifiedState state;

    @Mock
    private ClassifiedState successor;

    private Map<ClassifiedState, Double> successors;

    private StateIOProcessor processor;

    @Before
    public void setUp() {
        successors = new HashMap<>();
        successors.put(successor, 1.4);
        processor = new StateIOProcessor(writer, output);
    }

    @Test
    public void writesToStateWriter() {
        processor.processTransitions(state, successors);
        verify(writer).writeTransitions(state, successors, output);
    }

}