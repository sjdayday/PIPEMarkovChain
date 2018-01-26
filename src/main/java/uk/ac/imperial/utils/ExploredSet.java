package uk.ac.imperial.utils;

import com.google.common.hash.HashCode;
import uk.ac.imperial.state.ClassifiedState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * Uses a probabilistic method to compress data by double hashing items.
 * The first hash yields the location for the object and the second is used for
 * object equality comparisons.
 * <p>
 * The idea is that false-positives are very low due to the double hash. </p>
 */
public final class ExploredSet {

    /**
     * Size of array
     */
    private final int arraySize;

    /**
     * List to store TreeMap of HashCode in. This is the underlying 'Set' structure  and contains
     * the state to its id
     */
    private final List<TreeMap<WrappedHash, Integer>> array;

    /**
     * Number of items in set
     */
    private int itemCount = 0;

    /**
     * Initialises the underlying structure of the set
     *
     * @param arraySize underlying size of the set. It will not change
     */
    public ExploredSet(int arraySize) {
        this.arraySize = arraySize;
        array = new ArrayList<>(arraySize);
        for (int i = 0; i < arraySize; i++) {
            array.add(new TreeMap<WrappedHash, Integer>());
        }
    }

    /**
     * Uses two hashes to compress the state/
     * <p>
     * The first hash is used to determine the items location in memory
     * The second hash is stored for the object equality
     * </p>
     * @param state to be added and hashed
     * @param id of the state 
     */
    public void add(ClassifiedState state, int id) {
        int location = getLocation(state);
        TreeMap<WrappedHash, Integer> structure = array.get(location);
        int previousSize = structure.size();
        structure.put(new WrappedHash(state.secondaryHash()), id);
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
     * @param state to search for
     * @return true if the state is stored as a compressed value in the set
     */
    public boolean contains(ClassifiedState state) {
        int location = getLocation(state);
        TreeMap<WrappedHash, Integer> structure = array.get(location);
        WrappedHash wrappedHash = new WrappedHash(state.secondaryHash());
        return structure.containsKey(wrappedHash);
    }

    /**
     * Works out where the state should be placed/found in array.
     * <p>
     * It does this by working out its hashcode and taking the absolute value.
     * </p><p>
     * This is then modded by the size of the array to give a guaranteed index
     * into the array.
     * </p>
     * @param state whose location is found or should be placed 
     * @return the location that this state falls in the array
     */
    public int getLocation(ClassifiedState state) {
        int location = hashOneInt(state) % arraySize;
        return Math.abs(location);
    }

    /**
     *
     * @param state to be hashed
     * @return integer representation of the primary hash of the state
     */
    private int hashOneInt(ClassifiedState state) {
        return hashOne(state);
    }

    /**
     *
     * @param state to be hashed
     * @return primary hash of the state
     */
    private int hashOne(ClassifiedState state) {
        return state.primaryHash();
    }

    /**
     * Clears the entire set
     */
    public void clear() {
        for (TreeMap<WrappedHash, Integer> list : array) {
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
     * @param state state
     * @return the unique id given to this state
     */
    public int getId(ClassifiedState state) {
        int location = getLocation(state);
        //        int value = hashTwo(state);
        TreeMap<WrappedHash, Integer> structure = array.get(location);
        WrappedHash wrappedHash = new WrappedHash(state.secondaryHash());
        return structure.get(wrappedHash);
    }

    /**
     * Private class for the TreeMap in order to make a states hash codes comparable
     */
    private static final class WrappedHash implements Comparable<WrappedHash> {
        /**
         * Secondary hash
         */
        private final HashCode hash;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof WrappedHash)) {
                return false;
            }

            WrappedHash that = (WrappedHash) o;

            if (!hash.equals(that.hash)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return hash.hashCode();
        }

        /**
         * Constructor
        
         * @param hash secondary hash
         */
        private WrappedHash(HashCode hash) {
            this.hash = hash;
        }

        /**
         *
         * @param o object to be compared
         * @return comparison of long hashcodes
         */
        @Override
        public int compareTo(WrappedHash o) {
            return Integer.compare(hash.asInt(), o.hash.asInt());
        }
    }
}
