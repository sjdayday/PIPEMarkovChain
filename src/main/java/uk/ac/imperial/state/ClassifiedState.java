package uk.ac.imperial.state;

/**
 * A classified state behaves the same as a state, except it has methods
 * to determine if it is a tangible of vanishing state
 *
 * A tangible state is one in which all enabled transitions from it are timed
 * A vanishing state is one in which one or more enabled transitions from it are immediate
 * and is known as vanishing because the transition will happen immediately.
 */
public interface ClassifiedState extends State {
    /**
     *
     * @return true if the state is tangible
     */
    boolean isTangible();
}
