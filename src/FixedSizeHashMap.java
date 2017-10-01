
/**
 * A fixed-sized hash map that associates string keys with arbitrary data object references.
 * Collision is resolved through linear probing.
 * @author Yanlin Li
 * @param <V> object type of values
 */
public class FixedSizeHashMap<V> {
    
    /** Instance variables. */
    private int size; // fixed size of bucket
    private Entry<V>[] buckets; // array where map's key/value pair entries are stored
    private int itemCount; // number of key/value pairs currently present in map
    
    /**
     * Make a new fixed-size hash map of the specified size.
     * @param size fixed size of the map
     */
    public FixedSizeHashMap(final int size) {
        this.size = size;
        this.buckets = new Entry[size];
        this.itemCount = 0;
    }
    
    /**
     * Stores the specified value with the specified key in this fixed-size map. 
     * If the map previously contained a mapping for the key, the old value is replaced.
     * @param key String key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return true if the given key/value pair is stored successfully, false otherwise
     */
    public boolean set(final String key, final V value) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null.");
        
        if (size == 0) return false; // operation always fails if map has size 0
        
        int keyHash = hash(key);
        Entry<V> entry = new Entry<>(key, value); // create a new bucket entry
        int probe = keyHash; // probe starts at index keyHash
        do { 
            // Add item if location in bucket is empty
            if (buckets[probe] == null || buckets[probe].isDeleted()) {
                buckets[probe] = entry;
                itemCount++;
                return true;
            }
            // If location in bucket is nonempty, resolve collision 
            else if (buckets[probe].getKey().equals(key)) {
                buckets[probe].setValue(value); // reset value if keys are the same String
                return true;
            }
            // If different keys (same hash), further resolve collision through linear probing
            probe++; // increment to the next buckets index
            if (probe == size) probe = 0; // wrap around to 0 at the end of the array
        } while (probe != keyHash);
        return false; // if probe looped back to keyHash, then buckets is full, operation failure
    }
    
    /**
     * Returns the value to which the specified key is mapped, 
     * or null if this map contains no mapping for the key.
     * @param key String key whose associated value is to be returned
     * @return the value to which the specified key is mapped, 
     *         or null if this map contains no mapping for the key
     */
    public V get(String key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null.");
        
        if (size == 0) return null; // map of size 0 has no value to get
        
        int keyHash = hash(key);
        int probe = keyHash; // probe starts at index keyHash
        do { 
            if (buckets[probe] == null) {
                return null;
            }
            else if (buckets[probe].getKey().equals(key)) { // if both hash and keys match
                if (buckets[probe].isDeleted())
                    return null; // return null if entry is already deleted
                return buckets[probe].getValue();
            }
            // If different keys (same hash), continue probing
            probe++; // increment to the next buckets index
            if (probe == size) probe = 0; // wrap around to 0 at the end of the array
        } while (probe != keyHash);
        return null; // if probe looped back to keyHash, then key does not exist
    }
    
    /**
     * Deletes the value associated with the given key if present.
     * @param key String key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    public V delete(String key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null.");
        
        if (size == 0) return null; // map of size 0 has no value to delete
        
        int keyHash = hash(key);
        int probe = keyHash; // probe starts at index keyHash
        do { 
            if (buckets[probe] == null) {
                return null;
            }
            else if (buckets[probe].getKey().equals(key)) { // if both hash and keys match
                if (buckets[probe].isDeleted()) return null; // return null if entry is already deleted
                V value = buckets[probe].getValue();
                buckets[probe].delete();
                itemCount--;
                return value;
            }
            // If different keys (same hash), continue probing
            probe++; // increment to the next buckets index
            if (probe == size) probe = 0; // wrap around to 0 at the end of the array
        } while (probe != keyHash);
        return null; // if probe looped back to keyHash, then key does not exist
    }
    
    /**
     * Returns the load factor (`(items in map)/(size of map)`). 
     * @return load factor. 
     *         Since the size of the map is fixed, this should never be greater than 1. 
     *         If map has size 0, this value is 1.
     */
    public double load() {
        if (size == 0) return 1.0; // map of size 0 has load factor 1
        return (double) itemCount / size;
    }
    
    /**
     * Hashes the key to find the bucket index at which the key is stored.
     * @param key key to be hashed
     * @return hash value
     */
    private int hash(String key) {
        return Math.abs(key.hashCode()) % size;
    }

    /**
     * Entry representing the object in the map's buckets where the key/value pair is stored.
     * @param <V> object type of values
     */
    class Entry<V> {
        
        /** Instance variables. */
        private String key;
        private V value;
        private boolean isDeleted;
        
        /**
         * Make a new entry stroing the key/value pair.
         * @param key String key with which the specified value is to be associated
         * @param value value to be associated with the specified key
         */
        Entry(String key, V value) {
           this.key = key;
           this.value = value;
           this.isDeleted = false;
        }
        
        /**
         * @return key of the entry
         */
        String getKey() {
           return key;
        }
        
        /**
         * @return value of the entry
         */
        V getValue() {
           return value;
        }
        
        /**
         * @return true if the entry is deleted, false otherwise
         */
        boolean isDeleted() {
            return isDeleted;
        }
        
        /**
         * Sets the key of the entry to be the new specified key.
         * NOT USED.
         * @param key new specified key
         */
        void setKey(String key) {
            this.key = key;
        }
        
        /**
         * Sets the value of the entry to be the new specified value.
         * @param value new specified value
         */
        void setValue(V value) {
            this.value = value;
        }
         /**
          * Sets the key to be deleted.
          */
        void delete() {
            isDeleted = true;
        }
        
    }
}
