package uk.ac.imperial.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the {@link uk.ac.imperial.state.State} interface and uses
 * a hashmap to store each places token counts in the given state
 */
public final class HashedState implements State {
    /**
     * The token counts for the current State.
     * Contains Place id -> {Token -> Integer count}
     */
    private Map<String, Map<String, Integer>> tokenCounts = new HashMap<>();

    /**
     * Empty constructor for Java bean needed to marshal object
     */
    public HashedState() {
    }

    /**
     * Constructor containing the counts for each place
     * @param tokenCounts
     */
    public HashedState(Map<String, Map<String, Integer>> tokenCounts) {
        this.tokenCounts.putAll(tokenCounts);
    }

    /**
     *
     * @return a map representation of the state, mapping place id -> {token id -> count}
     */
    public Map<String, Map<String, Integer>> getTokenCounts() {
        return tokenCounts;
    }

    /**
     * Java bean method
     * @param tokenCounts new token counts for the state, the old token counts will be replaced with these
     */
    public void setTokenCounts(Map<String, Map<String, Integer>> tokenCounts) {
        this.tokenCounts = tokenCounts;
    }

    /**
     *
     * @param id Place id
     * @return map token id -> counts for the given place in this state
     */
    @Override
    public Map<String, Integer> getTokens(String id) {
        return tokenCounts.get(id);
    }

    /**
     *
     * @param id Place id
     * @return true if the place has any tokens
     */
    @Override
    public boolean containsTokens(String id) {
        return tokenCounts.containsKey(id);
    }

    /**
     *
     * @return all places referenced in this state
     */
    @Override
    public Collection<String> getPlaces() {
        return tokenCounts.keySet();
    }

    @Override
    public int hashCode() {
        return tokenCounts != null ? tokenCounts.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HashedState)) {
            return false;
        }

        HashedState that = (HashedState) o;

        if (tokenCounts != null ? !tokenCounts.equals(that.tokenCounts) : that.tokenCounts != null) {
            return false;
        }

        return true;
    }

    /**
     *
     * @return String representation of the state, displaying place to token counts e.g.
     * {P0 : {Default : 1, Red : 2}, P1 : {Default : 0, Red : 1}}
     */
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("{");
        int count = 0;
        for (Map.Entry<String, Map<String, Integer>> entry : tokenCounts.entrySet()) {
            builder.append("\"").append(entry.getKey()).append("\"").append(": {");
            int insideCount = 0;
            for (Map.Entry<String, Integer> entry1 : entry.getValue().entrySet()) {
                builder.append("\"").append(entry1.getKey()).append("\"").append(": ").append(entry1.getValue());
                if (insideCount < entry.getValue().size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append("}");

            if (count < tokenCounts.size() - 1) {
                builder.append(", ");
            }
            count++;
        }
        builder.append("}");
        return builder.toString();
    }
}
