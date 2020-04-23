package uk.ac.imperial.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.google.common.hash.Funnel;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;

import uk.ac.imperial.state.ClassifiedState;
import uk.ac.imperial.state.HashedClassifiedState;
import uk.ac.imperial.state.HashedState;
import uk.ac.imperial.state.State;

/**
 * Utility class for building states from Json strings
 */
public final class StateUtils {

    /**
     * Private constrictor for utility class
     */
    private StateUtils() {
    }

    /**
     *
     * @param jsonState json representation of a map place id -&gt; {token id -&gt; count}
     *                  E.g. "{\"P0\" : { \"Default\" : 0, \"Red\" : 1 }, \"P1\" : { \"Default\" : 1, \"Red\" : 0 } }"
     * @return unclassified state from the json string representation
     * @throws IOException if IO error occurs during read
     */
    public static State stateFromJson(String jsonState) throws IOException {
        // address cve: CVE-2017-7525 https://medium.com/@cowtowncoder/jackson-2-10-features-cd880674d8a2
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .build();
        ObjectMapper mapper = JsonMapper.builder()
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL)
                .build();
        JsonNode node = mapper.readTree(jsonState);
        Map<String, Map<String, Integer>> map = buildStateMap(node);
        return new HashedState(map);
    }

    private static Map<String, Map<String, Integer>> buildStateMap(JsonNode node) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Iterator<Entry<String, JsonNode>> it = node.fields();
        JsonNode countsNode = null;
        Map<String, Integer> countmap = null;
        Entry<String, JsonNode> placeEntry = null;
        String placeId = null;
        while (it.hasNext()) {
            placeEntry = it.next();
            placeId = placeEntry.getKey();
            countsNode = placeEntry.getValue();
            countmap = buildCountsForCurrentPlace(countsNode);
            map.put(placeId, countmap);
        }
        return map;
    }

    private static Map<String, Integer> buildCountsForCurrentPlace(JsonNode counts) {
        Iterator<Entry<String, JsonNode>> countfields = counts.fields();
        Map<String, Integer> countmap = new HashMap<>();
        Entry<String, JsonNode> countEntry;
        String token = null;
        while (countfields.hasNext()) {
            countEntry = countfields.next();
            token = countEntry.getKey();
            int count = countEntry.getValue().asInt();
            countmap.put(token, count);
        }
        return countmap;
    }

    /**
     *
     * @param jsonState json representation of a map place id -&gt; {token id -&gt; count}
     *                  E.g. "{\"P0\" : { \"Default\" : 0, \"Red\" : 1 }, \"P1\" : { \"Default\" : 1, \"Red\" : 0 } }"
     * @return state from the json string representation that has been classified as vanishing
     * @throws IOException if IO error occurs during read
     */
    public static ClassifiedState vanishingStateFromJson(String jsonState) throws IOException {
        State state = stateFromJson(jsonState);
        return HashedClassifiedState.vanishingState(state);
    }

    /**
     *
     * @param jsonState json representation of a map place id -&gt; {token id -&gt; count}
     *                  E.g. "{\"P0\" : { \"Default\" : 0, \"Red\" : 1 }, \"P1\" : { \"Default\" : 1, \"Red\" : 0 } }"
     * @return state from the json string representation that has been classified as tangible
     * @throws IOException if IO error occurs during read
     */
    public static ClassifiedState tangibleStateFromJson(String jsonState) throws IOException {
        State state = stateFromJson(jsonState);
        return HashedClassifiedState.tangibleState(state);
    }

    /**
     * Funnel used to generate HashCode of ExplorerState
     * <p>
     * Due to the behaviour of a HashMap, order is not guaranteed on objects
     * so we cannot loop through the map of the explorer state and add the
     * primitive types, because a differing order will generate a different hash code.
     * </p><p>
     * It appears though that the map hash code method returns the same value
     * no matter the order so this has been used here.</p>
     * @param placeOrdering ordering of places in the state
     * @return Funnel
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
        return Hashing.murmur3_128();
    }

    /**
     * Hashes the state using the specified funnel
     *
     * @param state to be hashed
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
     * @param state to be ordered
     * @return ordering based on sorting the places
     */
    private static List<String> getOrdering(State state) {
        List<String> placeOrdering = new ArrayList<>(state.getPlaces());
        Collections.sort(placeOrdering);
        return placeOrdering;
    }

}
