package uk.ac.imperial.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HashedState implements State {
    public Map<String, Map<String, Integer>> getTokenCounts() {
        return tokenCounts;
    }

    public void setTokenCounts(Map<String, Map<String, Integer>> tokenCounts) {
        this.tokenCounts = tokenCounts;
    }

    /**
     * The token counts for the current State.
     * Contains Place id -> {Token -> Integer count}
     */
    private Map<String, Map<String, Integer>> tokenCounts = new HashMap<>();

    /**
     * Empty contructor for Java bean
     */
    public HashedState() {
    }

    public HashedState(Map<String, Map<String, Integer>> tokenCounts) {
        this.tokenCounts.putAll(tokenCounts);
    }

    @Override
    public Map<String, Integer> getTokens(String id) {
        return tokenCounts.get(id);
    }

    @Override
    public Collection<String> getPlaces() {
        return tokenCounts.keySet();
    }

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

    @Override
    public int hashCode() {
        return tokenCounts != null ? tokenCounts.hashCode() : 0;
    }
}
