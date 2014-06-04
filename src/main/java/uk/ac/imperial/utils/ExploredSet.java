package uk.ac.imperial.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.*;
import uk.ac.imperial.state.ClassifiedState;

import java.util.*;

/**
 * Uses a probabilistic method to compress data by double hashing items.
 * The first hash yields the location for the object and the second is used for
 * object equality comparisons.
 * <p/>
 * The idea is that false-positives are very low due to the double hash.
 */
public final class ExploredSet {

    /**
     * Size of array
     */
    private final int arraySize;

    /**
     * Due to states potentially having different ordering of the places in their map
     * this affects their hash value.
     * <p/>
     * Thus this list defines a definitive ordering for querying places in the state
     */
    private final List<String> placeOrdering;

    /**
     * Funnel used to generate HashCode of ExplorerState
     * <p/>
     * Due to the behaviour of a HashMap, order is not guarnateed on objects
     * so we cannot loop through the map of the explorer state and add the
     * primitive types, because a differing order will generate a different hash code.
     * <p/>
     * It appears though that the map hashcode method returns the same value
     * no matter the order so this has been used here.
     */
    private final Funnel<ClassifiedState> funnel = new Funnel<ClassifiedState>() {
        @Override
        public void funnel(ClassifiedState from, PrimitiveSink into) {
            into.putBoolean(from.isTangible());
            for (String place : placeOrdering) {
                into.putString(place, Charsets.UTF_8);
                for (Map.Entry<String, Integer> entry : from.getTokens(place).entrySet()) {
                    into.putString(entry.getKey(), Charsets.UTF_8);
                    into.putInt(entry.getValue());
                }
            }
        }
    };

    /**
     * List to store TreeMap of HashCode in. This is the underlying 'Set' structure  and contains
     * the state to its id
     */
    private final List<TreeMap<StateEntry, Integer>> array;


    /**
     * 32 bit hash function
     */
    private final HashFunction primaryHash = Hashing.crc32();

    private final HashFunction secondaryHash = Hashing.adler32();

    /**
     * Number of items in set
     */
    private int itemCount = 0;

    /**
     * Initialises the underlying structure of the set
     *
     * @param arraySize underlying size of the set. It will not change
     */
    public ExploredSet(int arraySize, List<String> placeOrdering) {
        this.arraySize = arraySize;
        array = new ArrayList<>(arraySize);
        for (int i = 0; i < arraySize; i++) {
            array.add(new TreeMap<StateEntry, Integer>());
        }
        this.placeOrdering = new LinkedList<>(placeOrdering);
    }

    /**
     * Uses two hashes to compress the state/
     * <p/>
     * The first hash is used to determine the items location in memory
     * The second hash is stored for the object equality
     *
     * @param state
     */
    public void add(ClassifiedState state, int id) {
        int location = getLocation(state);
        HashCode h2 = hashTwo(state);
        TreeMap<StateEntry, Integer> structure = array.get(location);
        int previousSize = structure.size();
        structure.put(new StateEntry(h2), id);
        if (structure.size() > previousSize) {
            itemCount++;
        }
    }

    /**
     * Compresses states and adds them to the explored data structure
     *
     * @param states all states that have been explored
     * @param ids    all matching ids for the states
     */
    public void addAll(Collection<ClassifiedState> states, List<Integer> ids) {
        int i = 0;
        for (ClassifiedState state : states) {
            add(state, ids.get(i));
            i++;
        }
    }

    /**
     *
     * @param state
     * @return true if the state is stored as a compressed value in the set
     */
    public boolean contains(ClassifiedState state) {
        int location = getLocation(state);
        HashCode value = hashTwo(state);
        TreeMap<StateEntry, Integer> structure = array.get(location);
        return structure.containsKey(new StateEntry(value));
    }

    /**
     * Works out where the state should be placed/found in array.
     * <p/>
     * It does this by working out its hashcode and taking the absolute value.
     * <p/>
     * This is then modded by the size of the array to give a guaranteed index
     * into the array.
     *
     * @param state
     * @return the location that this state falls in the array
     */
    public int getLocation(ClassifiedState state) {
        return Math.abs(hashOneInt(state) % arraySize);
    }

    /**
     *
     * @param state
     * @return secondary hash of the state
     */
    private HashCode hashTwo(ClassifiedState state) {
        return hashCodeForState(state, secondaryHash);
    }

    /**
     *
     * @param state
     * @return integer representation of the primary hash of the state
     */
    private int hashOneInt(ClassifiedState state) {
        return hashOne(state).asInt();
    }

    /**
     *
     * Hashes the state using the specified funnel
     *
     * @param state
     * @param hf function to hash the state with
     * @return hash code for state using the specified hash function
     */
    private HashCode hashCodeForState(ClassifiedState state, HashFunction hf) {
        return hf.newHasher().putObject(state, funnel).hash();
    }

    /**
     *
     * @param state
     * @return primary hash of the state
     */
    private HashCode hashOne(ClassifiedState state) {
        return hashCodeForState(state, primaryHash);
    }

    /**
     * Clears the entire set
     */
    public void clear() {
        for (TreeMap<StateEntry, Integer> list : array) {
            list.clear();
        }
    }

    /**
     *
     * @return number of states housed in the set
     */
    public int size() {
        return itemCount;
    }

    /**
     * @param state
     * @return the unique id given to this state
     */
    public int getId(ClassifiedState state) {
        int location = getLocation(state);
        HashCode value = hashTwo(state);
        TreeMap<StateEntry, Integer> structure = array.get(location);
        StateEntry entry = new StateEntry(value);
        return structure.get(entry);
    }

    /**
     * Internal class for storing the secondary hash within the sets array
     */
    private static final class StateEntry implements Comparable<StateEntry> {
        /**
         * Second hash of a state used to identify equality
         */
        public final HashCode secondaryHash;

        private StateEntry(HashCode secondaryHash) {
            this.secondaryHash = secondaryHash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof StateEntry)) {
                return false;
            }

            StateEntry that = (StateEntry) o;

            if (!secondaryHash.equals(that.secondaryHash)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return secondaryHash.hashCode();
        }

        /**
         * Compares secondary hashes integer representations against each other.
         * Uses integer representation since the secondaryHash is currently a 32 bit hash code generator.
         * If this is changed it could be a Long comparison.
         * @param o
         * @return comparison of secondary hashes
         */
        @Override
        public int compareTo(StateEntry o) {
            return Integer.compare(o.secondaryHash.asInt(), secondaryHash.asInt());
        }


    }

}
