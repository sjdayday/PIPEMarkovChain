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
     * Array to store LinkedList of HashCode in. This is the underlying 'Set' structure
     */
    private final List<TreeSet<StateEntry>> array;

    private final Map<HashEntry, Integer> idMappings = new TreeMap<>();


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
            array.add(new TreeSet<StateEntry>());
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
        HashCode value = hashTwo(state);
        TreeSet<StateEntry> list = array.get(location);
        int previousSize = list.size();
        list.add(new StateEntry(value));
        idMappings.put(new HashEntry(hashOne(state), value), id);
        if (list.size() > previousSize) {
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

    public boolean contains(ClassifiedState state) {
        int location = getLocation(state);
        HashCode value = hashTwo(state);
        TreeSet<StateEntry> list = array.get(location);
        return list.contains(new StateEntry(value));
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

    private HashCode hashTwo(ClassifiedState state) {
        return hashCodeForState(state, secondaryHash);
    }

    private int hashOneInt(ClassifiedState state) {
        return hashOne(state).asInt();
    }

    private HashCode hashCodeForState(ClassifiedState state, HashFunction hf) {
        return hf.newHasher().putObject(state, funnel).hash();
    }

    private HashCode hashOne(ClassifiedState state) {
        return hashCodeForState(state, primaryHash);
    }

    public void clear() {
        for (TreeSet<StateEntry> list : array) {
            list.clear();
        }
    }

    public int size() {
        return itemCount;
    }

    /**
     * @param state
     * @return the unique id given to this state
     */
    public int getId(ClassifiedState state) {
        HashEntry entry = new HashEntry(hashOne(state), hashTwo(state));
        return idMappings.get(entry);
        //        int index = list.toArray(new StateEntry(value));
        //        return list.get(index).id;

    }

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

        @Override
        public int compareTo(StateEntry o) {
            return Integer.compare(o.secondaryHash.asInt(), secondaryHash.asInt());
        }


    }

    private static final class HashEntry implements Comparable<HashEntry> {
        private final HashCode h1;

        private final HashCode h2;

        private HashEntry(HashCode h1, HashCode h2) {
            this.h1 = h1;
            this.h2 = h2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof HashEntry)) {
                return false;
            }

            HashEntry entry = (HashEntry) o;

            if (!h1.equals(entry.h1)) {
                return false;
            }
            if (!h2.equals(entry.h2)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = h1.hashCode();
            result = 31 * result + h2.hashCode();
            return result;
        }

        @Override
        public int compareTo(HashEntry o) {
            return Integer.compare(h2.asInt(), o.h2.asInt());
        }
    }

}
