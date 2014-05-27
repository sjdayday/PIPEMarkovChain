package uk.ac.imperial.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.*;
import uk.ac.imperial.state.ClassifiedState;

import java.util.*;

/**
 * Uses a probabilistic method to compress data by double hashing items.
 * The first hash yields the location for the object and the second is used for
 * object equality comparisons.
 *
 * The idea is that false-positives are very low due to the double hash.
 */
public class ExploredSet {

    /**
     * Size of array
     */
    private final int arraySize;

    /**
     * Number of items in set
     */
    private int size = 0;

    /**
     * Due to states potentially having different ordering of the places in their map
     * this affects their hash value.
     *
     * Thus this list defines a definitive ordering for querying places in the state
     */
    private final List<String> placeOrdering;


    /**
     * Array to store LinkedList of HashCode in. This is the underlying 'Set' structure
     */
    private final List<HashSet<HashCode>> array;

    private final Map<HashEntry, Integer> idMappings = new TreeMap<>();


    /**
     * 32 bit hash function
     */
    private final HashFunction murmur3 =  Hashing.murmur3_32();

    /**
     * Funnel used to generate HashCode of ExplorerState
     *
     * Due to the behaviour of a HashMap, order is not guarnateed on objects
     * so we cannot loop through the map of the explorer state and add the
     * primitive types, because a differing order will generate a different hash code.
     *
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
     * Initialises the underlying structure of the set
     *
     * @param arraySize underlying size of the set. It will not change
     */
    public ExploredSet(int arraySize, List<String> placeOrdering) {
        this.arraySize = arraySize;
        array = new ArrayList<>(arraySize);
        for (int i = 0; i < arraySize; i++) {
            array.add(new HashSet<HashCode>());
        }
        this.placeOrdering = new LinkedList<>(placeOrdering);
    }

    /**
     * Uses two hashes to compress the state/
     *
     * The first hash is used to determine the items location in memory
     * The second hash is stored for the object equality
     * @param state
     */
    public void add(ClassifiedState state, int id) {
        int location = getLocation(state);
        HashCode value = hashTwo(state);
        HashSet<HashCode> list = array.get(location);
        int previousSize = list.size();
        list.add(value);
        idMappings.put(new HashEntry(hashOne(state), value), id);
        if (list.size() > previousSize) {
            size++;
        }
    }

    /**
     *
     * Compresses states and adds them to the explored data structure
     *
     * @param states all states that have been explored
     * @param ids all matching ids for the states
     */
    public void addAll(Collection<ClassifiedState> states, List<Integer> ids) {
        int i = 0;
        for (ClassifiedState state : states) {
            add(state, ids.get(i));
            i++;
        }
    }

    /**
     * Adds all elements in the exploredSet into this one
     *
     * Sadly since the original item has been lost, we cannot re-hash it
     * into this set. Therefore we must loop through it and keep items in their
     * same location in memory
     *
     * @param exploredSet
     */
    public void addAll(ExploredSet exploredSet) {
        throw new UnsupportedOperationException();
//        if (exploredSet.array.size() != this.array.size()) {
//            throw new RuntimeException("Cannot combine sets with different sized arrays. Due to compression here is no item to reconstruct hashcode from!");
//        }
//        for (int i = 0; i < exploredSet.array.size(); i++) {
//            List<StateEntry> theirs = exploredSet.array.get(i);
//            List<StateEntry> ours = array.get(i % arraySize);
//            ours.addAll(theirs);
//        }
    }

    /**
     *
     * Works out where the state should be placed/found in array.
     *
     * It does this by working out its hashcode and taking the absolute value.
     *
     * This is then modded by the size of the array to give a guaranteed index
     * into the array.
     *
     * @param state
     * @return the location that this state falls in the array
     */
    public int getLocation(ClassifiedState state) {
        return  Math.abs(hashOneInt(state) % arraySize);
    }

    public boolean contains(ClassifiedState state) {
        int location = getLocation(state);
        HashCode value = hashTwo(state);
        HashSet<HashCode> list = array.get(location);
        return list.contains(value);
    }



    private int hashOneInt(ClassifiedState state) {
        return hashOne(state).asInt();
    }

    private HashCode hashOne(ClassifiedState state) {
         return hashCodeForState(state, murmur3);
    }

    private HashCode hashTwo(ClassifiedState state) {
        HashFunction hf = Hashing.sha1();
        return hashCodeForState(state, hf);
    }

    private HashCode hashCodeForState(ClassifiedState state, HashFunction hf) {
        return hf.newHasher().putObject(state, funnel).hash();
    }

    public void clear() {
        for (HashSet<HashCode> list : array) {
            list.clear();
        }
    }

    public int size() {
        int size = 0;
        for (HashSet<HashCode> list : array) {
            size += list.size();
        }
        return size;
    }

    /**
     *
     * @param state
     * @return the unique id given to this state
     */
    public int getId(ClassifiedState state) {
        HashEntry entry = new HashEntry(hashOne(state), hashTwo(state));
        return idMappings.get(entry);
//        int index = list.toArray(new StateEntry(value));
//        return list.get(index).id;

    }

    private class StateEntry {
        private StateEntry(HashCode hashCode, int id) {
            this.hashCode = hashCode;
            this.id = id;
        }

        /**
         * Constructor used when asking if the set contains this object
         * since equality is done via the hashcode only.
         * @param hashCode
         */
        private StateEntry(HashCode hashCode) {
            this.hashCode = hashCode;
            this.id = 0;
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

            if (!hashCode.equals(that.hashCode)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return hashCode.hashCode();
        }

        /**
         * Second hash of a state used to identify equality
         */
        public final HashCode hashCode;

        /**
         * Unique id meta data for each state
         */
        public final int id;
    }

    private class HashEntry implements Comparable<HashEntry> {
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
            return Long.compare(h2.asLong(), o.h2.asLong());
        }
    }

}
