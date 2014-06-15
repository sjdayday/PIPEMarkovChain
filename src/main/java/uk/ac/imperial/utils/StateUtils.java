package uk.ac.imperial.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.HashedClassifiedState;
import uk.ac.imperial.state.HashedState;
import uk.ac.imperial.state.State;

import java.io.IOException;
import java.util.*;

/**
 * Utility class for building states from Json strings
 */
public final class StateUtils {

    /**
     * Private constrictor for utility class
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

    /**
     * Funnel used to generate HashCode of ExplorerState
     * <p/>
     * Due to the behaviour of a HashMap, order is not guaranteed on objects
     * so we cannot loop through the map of the explorer state and add the
     * primitive types, because a differing order will generate a different hash code.
     * <p/>
     * It appears though that the map hash code method returns the same value
     * no matter the order so this has been used here.
     */
    public static Funnel<State> getFunnel(final Collection<String> placeOrdering) {

        return new Funnel<State>() {
            @Override
            public void funnel(State from, PrimitiveSink into) {
                for (String place : placeOrdering) {
                    into.putBytes(place.getBytes());
                    for (Map.Entry<String, Integer> entry : from.getTokens(place).entrySet()) {
                        into.putBytes(entry.getKey().getBytes());
                        into.putInt(entry.getValue());
                    }
                }
            }
        };
    }

    /**
     *
     * @return primary hash function to be used
     */
    public static HashFunction getPrimaryHash() {
        return Hashing.adler32();
    }

    /**
     *
     * @return secondary hash function to be used
     */
    public static HashFunction getSecondaryHash() {
        return  Hashing.murmur3_128();
    }


    /**
     * Hashes the state using the specified funnel
     *
     * @param state
     * @param hf    function to hash the state with
     * @return hash code for state using the specified hash function
     */
    public static HashCode hashCodeForState(State state, HashFunction hf) {
        List<String> placeOrdering = getOrdering(state);
        Funnel<State> funnel = StateUtils.getFunnel(placeOrdering);
        return hf.newHasher().putObject(state, funnel).hash();
    }

    /**
     *
     * @param state
     * @return ordering based on sorting the places
     */
    private static List<String> getOrdering(State state) {
        List<String> placeOrdering = new ArrayList<>(state.getPlaces());
        Collections.sort(placeOrdering);
        return placeOrdering;
    }


}
