package uk.ac.imperial.state;

import java.util.Collection;
import java.util.Map;

/**
 * Java Bean ClassifiedState implementation that wraps a State
 * to contain extra classification information required
 */
public class HashedClassifiedState implements ClassifiedState {
    /**
     * Internally wrapped state
     */
    private State state;

    /**
     * Represents if this is a tangible state or a vanishing state
     */
    private boolean tangible;

    /**
     * Empty constructor for Java bean. Needed to marshal object
     */
    public HashedClassifiedState() {
    }

    /**
     * Private constructor. Use factory methods
     *
     * @param state
     * @param tangible
     */
    public HashedClassifiedState(State state, boolean tangible) {
        this.state = state;
        this.tangible = tangible;
    }

    /**
     * @param state
     * @return new state that represents a tangible state with the following tokens
     */
    public static HashedClassifiedState tangibleState(State state) {
        return new HashedClassifiedState(state, true);
    }

    /**
     * @param state
     * @return new state that represents a vanishing state with the following tokens
     */
    public static HashedClassifiedState vanishingState(State state) {
        return new HashedClassifiedState(state, false);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + (tangible ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HashedClassifiedState)) {
            return false;
        }

        HashedClassifiedState that = (HashedClassifiedState) o;

        if (tangible != that.tangible) {
            return false;
        }
        if (!state.equals(that.state)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isTangible() {
        return tangible;
    }

    public void setTangible(boolean tangible) {
        this.tangible = tangible;
    }

    @Override
    public Map<String, Integer> getTokens(String id) {
        return state.getTokens(id);
    }

    @Override
    public Collection<String> getPlaces() {
        return state.getPlaces();
    }
}
