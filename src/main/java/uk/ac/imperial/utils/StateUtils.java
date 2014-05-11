package uk.ac.imperial.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.HashedClassifiedState;
import uk.ac.imperial.state.HashedState;
import uk.ac.imperial.state.State;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for building states from Json strings
 */
public class StateUtils {

    private StateUtils() {}

    public static State stateFromJson(String jsonState) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, Integer>> map =
                mapper.readValue(jsonState, new TypeReference<HashMap<String, HashMap<String, Integer>>>() {
                });
        return new HashedState(map);
    }

    public static ClassifiedState vanishingStateFromJson(String jsonState) throws IOException {
        State state = stateFromJson(jsonState);
        return HashedClassifiedState.vanishingState(state);
    }

    public static ClassifiedState tangibleStateFromJson(String jsonState) throws IOException {
        State state = stateFromJson(jsonState);
        return HashedClassifiedState.tangibleState(state);
    }


}
