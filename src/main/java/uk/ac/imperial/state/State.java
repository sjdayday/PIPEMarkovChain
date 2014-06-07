package uk.ac.imperial.state;

import java.util.Collection;
import java.util.Map;

/**
 * Represents the state of a Petri net in the Markov Chain
 * For each place it will contain the number of tokens stored
 * E.g
 * At a given point in time if the following places contain the following tokens:
 * P1 = 1
 * P2 = 2
 * P3 = 3
 * P4 = 1
 * P4 = 0
 * <p/>
 * Then the state will be as follows:
 * (1, 2, 3, 1, 0)
 */
public interface State {


    /**
     * @param id Place id
     * @return number of tokens for the place with the given id
     */
    Map<String, Integer> getTokens(String id);

    /**
     *
     * @param id Place id
     * @return true if there are registered tokens for the given place
     */
    boolean containsTokens(String id);

    /**
     *
     * @return List of places associated with this state
     */
    Collection<String> getPlaces();

    int primaryHash();

    int secondaryHash();

}
