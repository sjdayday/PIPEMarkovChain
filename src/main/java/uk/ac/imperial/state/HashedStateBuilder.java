package uk.ac.imperial.state;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for building {@link uk.ac.imperial.state.HashedState}
 * without having to explicitly create the Map
 */
public final class HashedStateBuilder {
    private Map<String, Map<String, Integer>> tokenCounts = new HashMap<>();

    public HashedStateBuilder() {
	}
    
    public HashedStateBuilder(HashedState state) {
    	this();
        for (String placeId : state.getPlaces()) {
            placeWithTokens(placeId, state.getTokens(placeId));
        }
	}

	/**
     * Adds the specified token and count to the place
     * @param place to be added
     * @param token to be added
     * @param count for the token
     * @return builder
     */
    public HashedStateBuilder placeWithToken(String place, String token, int count) {
        Map<String, Integer> tokens = getTokens(place);
        tokens.put(token, count);
        return this;
    }

    /**
     *
     * Adds all tokenValues to the place
     * @param place to be added
     * @param tokenValues all tokens and values for the place 
     * @return builder
     */
    public HashedStateBuilder placeWithTokens(String place, Map<String, Integer> tokenValues) {
        Map<String, Integer> tokens = getTokens(place);
        tokens.putAll(tokenValues);
        return this;
    }

    /**
     *
     * @param place place id in state
     * @return corresponding map for the place
     */
    private Map<String, Integer> getTokens(String place) {
        if (!tokenCounts.containsKey(place)) {
            tokenCounts.put(place, new HashMap<String, Integer>());
        }
        return tokenCounts.get(place);
    }

    public HashedState build() {
        return new HashedState(tokenCounts);
    }
}
