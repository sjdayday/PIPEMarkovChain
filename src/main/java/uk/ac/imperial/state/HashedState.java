package uk.ac.imperial.state;

import com.google.common.hash.HashCode;
import uk.ac.imperial.utils.StateUtils;

import java.util.*;

/**
 * Implements the {@link uk.ac.imperial.state.State} interface and uses
 * a hashmap to store each places token counts in the given state
 */
public final class HashedState implements State {

    private int hashOne;

    private HashCode hashTwo;

    /**
     * The token counts for the current State.
     * Contains Place id -&gt; {Token -&gt; Integer count}
     */
    private Map<String, Map<String, Integer>> tokenCounts = new HashMap<>();


    /**
     * Empty constructor for Java bean needed to marshal object
     */
    public HashedState() {
    }

    /**
     * Constructor containing the counts for each place
     *
     * @param tokenCounts for each place in the state
     */
    public HashedState(Map<String, Map<String, Integer>> tokenCounts) {
        this.tokenCounts.putAll(tokenCounts);
        hashOne = calculateHashOne().asInt();
        hashTwo = calculateHashTwo();
    }

    /**
     * @return secondary hash of the state
     */
    private HashCode calculateHashTwo() {
        return StateUtils.hashCodeForState(this, StateUtils.getSecondaryHash());
    }

    /**
     * @return primary hash of the state
     */
    private HashCode calculateHashOne() {
        return StateUtils.hashCodeForState(this, StateUtils.getPrimaryHash());
    }


    /**
     * @return a map representation of the state, mapping place id -&gt; {token id -&gt; count}
     */
    public Map<String, Map<String, Integer>> getTokenCounts() {
        return tokenCounts;
    }

    /**
     * Java bean method
     *
     * @param tokenCounts new token counts for the state, the old token counts will be replaced with these
     */
    public void setTokenCounts(Map<String, Map<String, Integer>> tokenCounts) {
        this.tokenCounts = tokenCounts;
    }

    /**
     * @param id Place id
     * @return map token id -&gt;  counts for the given place in this state
     */
    @Override
    public Map<String, Integer> getTokens(String id) {
        return tokenCounts.get(id);
    }

    /**
     * @param id Place id
     * @return true if the place has any tokens
     */
    @Override
    public boolean containsTokens(String id) {
        return tokenCounts.containsKey(id);
    }

    /**
     * @return all places referenced in this state
     */
    @Override
    public Collection<String> getPlaces() {
        return tokenCounts.keySet();
    }

    @Override
    public int primaryHash() {
        return hashOne;
    }

    @Override
    public HashCode secondaryHash() {
        return hashTwo;
    }

    @Override
    public Map<String, Map<String, Integer>> asMap() {
        return tokenCounts;
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

        if (hashOne != that.hashOne) {
            return false;
        }
        if (!hashTwo.equals(that.hashTwo)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = hashOne;
        result = 31 * result + hashTwo.hashCode();
        return result;
    }

    /**
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
