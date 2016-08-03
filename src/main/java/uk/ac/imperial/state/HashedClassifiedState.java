package uk.ac.imperial.state;

import com.google.common.hash.HashCode;

import java.util.Collection;
import java.util.Map;

/**
 * Java Bean ClassifiedState implementation that wraps a State
 * to contain extra classification information required
 */
public final class HashedClassifiedState implements ClassifiedState {
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
     * @param state for constructor
     * @param tangible true if tangible
     */
    public HashedClassifiedState(State state, boolean tangible) {
        this.state = state;
        this.tangible = tangible;
    }

    /**
     * @param state tangible state
     * @return new state that represents a tangible state with the following tokens
     */
    public static HashedClassifiedState tangibleState(State state) {
        return new HashedClassifiedState(state, true);
    }

    /**
     * @param state vanishing state
     * @return new state that represents a vanishing state with the following tokens
     */
    public static HashedClassifiedState vanishingState(State state) {
        return new HashedClassifiedState(state, false);
    }

    /**
     *
     * @return the underlying unclassified state
     */
    public State getState() {
        return state;
    }

    /**
     *
     * @param state underlying unclassified state
     */
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

    /**
     *
     * @return true if tangible state, false if vanishing
     */
    @Override
    public boolean isTangible() {
        return tangible;
    }

    /**
     * Needed for java bean marshalling when serializing the state with Kyro libraries
     *
     * @param tangible true if the state is tangible, false if vanishing
     */
    public void setTangible(boolean tangible) {
        this.tangible = tangible;
    }

    /**
     *
     * @param id Place id
     * @return map of token id to token count for a given place
     */
    @Override
    public Map<String, Integer> getTokens(String id) {
        return state.getTokens(id);
    }

    /**
     *
     * @param id Place id
     * @return true if the specified place has any tokens
     */
    @Override
    public boolean containsTokens(String id) {
        return state.containsTokens(id);
    }

    /**
     *
     * @return all places referenced in this state
     */
    @Override
    public Collection<String> getPlaces() {
        return state.getPlaces();
    }

    @Override
    public int primaryHash() {
        return state.primaryHash();
    }

    @Override
    public HashCode secondaryHash() {
        return state.secondaryHash();
    }

    @Override
    public Map<String, Map<String, Integer>> asMap() {
        return state.asMap();
    }

    /**
     *
     * @return string representation of the state containing it's classification too
     */
    @Override
    public String toString() {
        String stateString = state.toString();
        if (isTangible()) {
            return "Tangible: " + stateString;
        }
        return "Vanishing: " + stateString;

    }
}
