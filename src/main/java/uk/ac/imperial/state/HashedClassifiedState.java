package uk.ac.imperial.state;

import java.util.Collection;

public class HashedClassifiedState implements ClassifiedState {
    public State getState() {
        return state;
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
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + (tangible ? 1 : 0);
        return result;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setTangible(boolean tangible) {
        this.tangible = tangible;
    }

    /**
     * Internally wrapped state
     */
    private  State state;

    /**
     * Represents if this is a tangible state or a vanishing state
     */
    private boolean tangible;

    public HashedClassifiedState() {
    }

    /**
     * Private constructor. Use factory methods
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
     *
     * @param state
     * @return new state that represents a vanishing state with the following tokens
     */
    public static HashedClassifiedState vanishingState(State state) {
        return new HashedClassifiedState(state, false);
    }


    @Override
    public boolean isTangible() {
        return tangible;
    }

    @Override
    public java.util.Map<String, Integer> getTokens(String id) {
        return state.getTokens(id);
    }

    @Override
    public Collection<String> getPlaces() {
        return state.getPlaces();
    }
}
