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
public final class StateUtils {

    /**
     * Private consturctor for utility class
     */
    private StateUtils() {}

    /**
     *
     * @param jsonState json representation of a map place id -> {token id -> count}
     *                  E.g. "{\"P0\" : { \"Default\" : 0, \"Red\" : 1 }, \"P1\" : { \"Default\" : 1, \"Red\" : 0 } }"
     * @return unclassified state from the json string representation
     * @throws IOException
     */
    public static State stateFromJson(String jsonState) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, Integer>> map =
                mapper.readValue(jsonState, new TypeReference<HashMap<String, HashMap<String, Integer>>>() {
                });
        return new HashedState(map);
    }

    /**
     *
     * @param jsonState json representation of a map place id -> {token id -> count}
     *                  E.g. "{\"P0\" : { \"Default\" : 0, \"Red\" : 1 }, \"P1\" : { \"Default\" : 1, \"Red\" : 0 } }"
     * @return state from the json string representation that has been classified as vanishing
     * @throws IOException
     */
    public static ClassifiedState vanishingStateFromJson(String jsonState) throws IOException {
        State state = stateFromJson(jsonState);
        return HashedClassifiedState.vanishingState(state);
    }

    /**
     *
     * @param jsonState json representation of a map place id -> {token id -> count}
     *                  E.g. "{\"P0\" : { \"Default\" : 0, \"Red\" : 1 }, \"P1\" : { \"Default\" : 1, \"Red\" : 0 } }"
     * @return state from the json string representation that has been classified as tangible
     * @throws IOException
     */
    public static ClassifiedState tangibleStateFromJson(String jsonState) throws IOException {
        State state = stateFromJson(jsonState);
        return HashedClassifiedState.tangibleState(state);
    }


}
