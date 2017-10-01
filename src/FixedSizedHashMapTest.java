import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tester class for FixedSizeHashMap.
 * @author Yanlin Li
 */
public class FixedSizedHashMapTest {
    
    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /*
     * Testing strategy for each operation of FixedSizeHashMap
     * 
     * set():
     *     - map size = 0, > 0
     *     - key to set already exists, or not
     *     - key to set has a collision with another key, or no collision detected
     *     - fixed-size constraint: cannot set more unique keys if map is full
     * 
     * get():
     *     - map size = 0, > 0
     *     - key to get has a collision with another key, or no collision detected
     *     - key to get is present or absent (never inserted or already deleted)
     * 
     * delete():
     *     - map size = 0, > 0
     *     - key to delete has a collision with another key, or no collision detected
     *     - key to delete is present or absent (never inserted or already deleted)
     * 
     * load():
     *     - map size = 0, > 0
     * 
     * Each part of the partition above is covered by at least one test case.
     * 
     */
    
    private static final int SMALL_MAP_SIZE = 4;
    private static final int LARGE_MAP_SIZE = 10;
    private static final String K_KEY = "K";
    private static final String P_KEY = "P";
    private static final String COLLISION_P_KEY = "p";
    private static final String C_KEY = "C";
    private static final String B_KEY = "B";
    private static final String K_VALUE = "Kleiner";
    private static final String P_VALUE = "Perkins";
    private static final String COLLISION_P_VALUE = "pERKINS";
    private static final String C_VALUE = "Caufield";
    private static final String B_VALUE = "Byers";
    private static final double DELTA = 0.0001;
    
    /*
     * This test covers:
     *     set(): map size > 0
     *     get(): map size > 0
     *     delete(): map size > 0
     *     load(): map size > 0
     */
    @Test
    public void testSizeZeroMap() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(0);
        final boolean operationSuccess = map.set(K_KEY, K_VALUE);
        assertFalse("Expected \"K\" to be added unsuccessfully", operationSuccess);
        final String kValue = map.get(K_KEY);
        assertEquals("Expected null for absent key \"K\"", null, map.get(K_KEY));
        final String deletedKValue = map.delete(K_KEY);
        assertEquals("Expected null value for key \"K\" not contained in map", null, deletedKValue);
        final String deletedCValue = map.delete(C_KEY);
        assertEquals("Expected null value for key \"C\" not contained in map", null, deletedCValue);
        final double expectedLoad = 1.0;
        assertEquals("Expected load 1", expectedLoad, map.load(), DELTA);
    }
    
    /*
     *  This test covers: 
     *      set(): map size > 0, key to set does not yet exist, key to set has no collision
     *      get(): map size > 0, key to get has no collision, keys to get are present and absent
     */
    @Test
    public void testSetAndGetWithoutCollision() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(SMALL_MAP_SIZE);
        final boolean operationSuccessK = map.set(K_KEY, K_VALUE);
        final boolean operationSuccessP = map.set(P_KEY, P_VALUE);
        assertTrue("Expected \"K\" to be added successfully", operationSuccessK);
        assertTrue("Expected \"P\" to be added successfully", operationSuccessP);
        assertEquals("Expected key/value (K: Kleiner)", K_VALUE, map.get(K_KEY));
        assertEquals("Expected key/value (P: Perkins)", P_VALUE, map.get(P_KEY));
        assertEquals("Expected null for absent key \"C\"", null, map.get(C_KEY));
    }
    
    /*
     *  This test covers: 
     *      set(): map size > 0, key to set already exists, key to set has no collision
     *      get(): map size > 0, key to get has no collision, keys to get are present
     */
    @Test
    public void testSetAndGetExistingKey() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(SMALL_MAP_SIZE);
        map.set(K_KEY, K_VALUE);
        map.set(P_KEY, P_VALUE);
        final boolean operationSuccess = map.set(P_KEY, COLLISION_P_VALUE);
        assertTrue("Expected \"P\" to be added successfully", operationSuccess);
        assertEquals("Expected key/value (P: pERKINS)", COLLISION_P_VALUE, map.get(P_KEY));
    }
    
    /*
     *  This test covers: 
     *      set(): map size > 0, key to set already exists, key to set has collision
     *      get(): map size > 0, key to get has collision, keys to get are present
     */
    @Test
    public void testSetAndGetCollidingKey() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(SMALL_MAP_SIZE);
        map.set(K_KEY, K_VALUE);
        map.set(P_KEY, P_VALUE);
        final boolean operationSuccess = map.set(COLLISION_P_KEY, COLLISION_P_VALUE);
        assertTrue("Expected \"p\" to be added successfully", operationSuccess);
        assertEquals("Expected key/value (K: Kleiner)", K_VALUE, map.get(K_KEY));
        assertEquals("Expected key/value (P: Perkins)", P_VALUE, map.get(P_KEY));
        assertEquals("Expected key/value (p: pERKINS)", COLLISION_P_VALUE, map.get(COLLISION_P_KEY));
    }
    
    /*
     *  This test covers: 
     *      set(): map size > 0, key to set already exists, key to set has collision, fixed-size map is full
     *      get(): map size > 0, key to get has collision, keys to get are present and absent
     */
    @Test
    public void testSetFullMap() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(SMALL_MAP_SIZE);
        map.set(K_KEY, K_VALUE);
        map.set(P_KEY, P_VALUE);
        map.set(C_KEY, C_VALUE);
        map.set(B_KEY, B_VALUE);
        assertEquals("Expected key/value (K: Kleiner)", K_VALUE, map.get(K_KEY));
        assertEquals("Expected key/value (P: Perkins)", P_VALUE, map.get(P_KEY));
        assertEquals("Expected key/value (C: Caufield)", C_VALUE, map.get(C_KEY));
        assertEquals("Expected key/value (B: Byers)", B_VALUE, map.get(B_KEY));
        final boolean operationSuccessOverrideExistingKey = map.set(P_KEY, COLLISION_P_VALUE);
        assertTrue("Expected \"P\" to be added successfully", operationSuccessOverrideExistingKey);
        assertEquals("Expected key/value (P: pERKINS)", COLLISION_P_VALUE, map.get(P_KEY));
        final boolean operationSuccessSetNewKey = map.set(COLLISION_P_KEY, COLLISION_P_VALUE);
        assertFalse("Expected \"p\" to be added unsuccessfully", operationSuccessSetNewKey);
        assertEquals("Expected null for absent key \"p\"", null, map.get(COLLISION_P_KEY));
    }
    
    /*
     *  This test covers: 
     *      delete(): map size > 0, key to delete has no collision, key to delete is present
     */
    @Test
    public void testDeleteExistingKey() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(SMALL_MAP_SIZE);
        map.set(K_KEY, K_VALUE);
        map.set(P_KEY, P_VALUE);
        assertEquals("Expected key/value (K: Kleiner)", K_VALUE, map.get(K_KEY));
        assertEquals("Expected key/value (P: Perkins)", P_VALUE, map.get(P_KEY));
        final String kValue = map.delete(K_KEY);
        assertEquals("Expected deleted value \"Kleiner\"", K_VALUE, kValue);
        assertEquals("Expected null value for key \"K\" not contained in map", null, map.get(K_VALUE));
        assertEquals("Expected key/value (P: Perkins)", P_VALUE, map.get(P_KEY));
    }
    
    /*
     *  This test covers: 
     *      delete(): map size > 0, key to delete has no collision, key to delete is absent (never inserted)
     */
    @Test
    public void testDeleteAbsentKey() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(SMALL_MAP_SIZE);
        map.set(K_KEY, K_VALUE);
        map.set(P_KEY, P_VALUE);
        assertEquals("Expected key/value (K: Kleiner)", K_VALUE, map.get(K_KEY));
        assertEquals("Expected key/value (P: Perkins)", P_VALUE, map.get(P_KEY));
        assertEquals("Expected null for absent key \"C\"", null, map.get(C_KEY));
        final String cValue = map.delete(C_KEY);
        assertEquals("Expected null value for key \"C\" not contained in map", null, cValue);
        assertEquals("Expected key/value (K: Kleiner)", K_VALUE, map.get(K_KEY));
        assertEquals("Expected key/value (P: Perkins)", P_VALUE, map.get(P_KEY));
    }
    
    /*
     *  This test covers: 
     *      delete(): map size > 0, key to delete has collision, key to delete is present
     */
    @Test
    public void testDeleteCollidingKey() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(SMALL_MAP_SIZE);
        map.set(K_KEY, K_VALUE);
        map.set(P_KEY, P_VALUE);
        map.set(COLLISION_P_KEY, COLLISION_P_VALUE);
        assertEquals("Expected key/value (K: Kleiner)", K_VALUE, map.get(K_KEY));
        assertEquals("Expected key/value (P: Perkins)", P_VALUE, map.get(P_KEY));
        assertEquals("Expected key/value (p: pERKINS)", COLLISION_P_VALUE, map.get(COLLISION_P_KEY));
        String pValue = map.delete(COLLISION_P_KEY);
        assertEquals("Expected deleted value \"pERKINS\"", COLLISION_P_VALUE, pValue);
        assertEquals("Expected null for deleted key \"p\"", null, map.get(COLLISION_P_KEY));
        assertEquals("Expected key/value (K: Kleiner)", K_VALUE, map.get(K_KEY));
        assertEquals("Expected key/value (P: Perkins)", P_VALUE, map.get(P_KEY));
    }
    
    /*
     *  This test covers: 
     *      delete(): map size > 0, key to delete has no collision, key to delete is absent (already deleted)
     */
    @Test
    public void testDeleteDeletedKey() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(SMALL_MAP_SIZE);
        map.set(K_KEY, K_VALUE);
        map.set(P_KEY, P_VALUE);
        final String kValue = map.delete(K_KEY);
        assertEquals("Expected deleted value \"Kleiner\"", K_VALUE, kValue);
        final String kValuePostDeletion = map.delete(K_KEY);
        assertEquals("Expected null value for key \"K\" not contained in map", null, kValuePostDeletion);
        assertEquals("Expected null for deleted key \"K\"", null, map.get(K_VALUE));
    }
    
    /*
     * This test covers:
     *     load(): map size > 0
     *     set(): map size > 0, key to set already exists, key to set has collision, fixed-size map is full
     *     delete(): map size > 0, key to delete has no collision, key to delete is present
     */
    @Test
    public void testMapLoad() {
        final FixedSizeHashMap<String> map = new FixedSizeHashMap<>(SMALL_MAP_SIZE);
        final double[] expectedLoads = {0, 0.25, 0.5, 0.75, 1, 1, 0.75};
        assertEquals("Expected load 0", expectedLoads[0], map.load(), DELTA);
        map.set(K_KEY, K_VALUE);
        assertEquals("Expected load 0.25", expectedLoads[1], map.load(), DELTA);
        map.set(P_KEY, P_VALUE);
        assertEquals("Expected load 0.5", expectedLoads[2], map.load(), DELTA);
        map.set(C_KEY, C_VALUE);
        assertEquals("Expected load 0.75", expectedLoads[3], map.load(), DELTA);
        map.set(B_KEY, B_VALUE);
        assertEquals("Expected load 1", expectedLoads[4], map.load(), DELTA);
        map.set(COLLISION_P_KEY, COLLISION_P_VALUE);
        assertEquals("Expected load 1", expectedLoads[5], map.load(), DELTA);
        map.delete(P_KEY);
        assertEquals("Expected load 0.75", expectedLoads[6], map.load(), DELTA);
    }
    
    /*
     *  This test covers a variety of operations from set(), get(), delete(), 
     *  and load() on a map of size 10. Keys to set/get/delete may have collisions
     *  with other keys and may be already present/absent.
     */
    @Test
    public void testEverything() {
        final String lettersToAdd = "abcdefghijk";
        final String repeatSetLetter = "a";
        final String firstLetterToDelete = "i";
        final String secondLetterToDelete = "j";
        final String thirdLetterToDelete = "z";
        final String moreLettersToAdd = "lmn";
        final FixedSizeHashMap<Integer> map = new FixedSizeHashMap<>(LARGE_MAP_SIZE);
        for (int i = 0; i < lettersToAdd.length(); i++) {
            String letter = lettersToAdd.substring(i, i+1);
            map.set(letter, i); // expect to add abcdefgh
        }
        final Integer iValue = map.delete(firstLetterToDelete); // delete i
        final Integer jValue = map.delete(secondLetterToDelete); // delete j
        final Integer zValue = map.delete(thirdLetterToDelete); // delete absent z
        final boolean repeatSetSuccess =  map.set(repeatSetLetter, 100 + map.get(repeatSetLetter));
        final double load1 = map.load(); // expect 0.8
        for (int i = 0; i < moreLettersToAdd.length(); i++) {
            String letter = moreLettersToAdd.substring(i, i+1);
            map.set(letter, lettersToAdd.length() + i); // expect to add lm
        }
        final double load2 = map.load(); // expect 1
        
        assertTrue("Expect setting key/value pair (a: Integer(100)) operation successful", repeatSetSuccess);
        
        assertEquals("Expected key/value pair (a: 100)", new Integer(100), map.get("a"));
        assertEquals("Expected key/value pair (b: 0)", new Integer(1), map.get("b"));
        assertEquals("Expected key/value pair (c: 0)", new Integer(2), map.get("c"));
        assertEquals("Expected key/value pair (d: 0)", new Integer(3), map.get("d"));
        assertEquals("Expected key/value pair (e: 0)", new Integer(4), map.get("e"));
        assertEquals("Expected key/value pair (f: 0)", new Integer(5), map.get("f"));
        assertEquals("Expected key/value pair (g: 0)", new Integer(6), map.get("g"));
        assertEquals("Expected key/value pair (h: 0)", new Integer(7), map.get("h"));
        assertEquals("Expected key/value pair (l: 0)", new Integer(11), map.get("l"));
        assertEquals("Expected key/value pair (m: 0)", new Integer(12), map.get("m"));
        
        assertEquals("Expected null value for key \"i\" not contained in map", null, map.get("i"));
        assertEquals("Expected null value for key \"j\" not contained in map", null, map.get("j"));
        assertEquals("Expected null value for key \"k\" not contained in map", null, map.get("k"));
        assertEquals("Expected null value for key \"n\" not contained in map", null, map.get("n"));
        
        assertEquals("Expected deleted value Integer(8) for deleted key \"i\"", new Integer(8), iValue);
        assertEquals("Expected deleted value Integer(9) for deleted key \"j\"", new Integer(9), jValue);
        assertEquals("Expected null value for key \"z\" not contained in map", null, zValue);
        
        assertEquals("Expected load 0.8 after removing keys \"i\" and \"j\"", 0.8, load1, DELTA);
        assertEquals("Expected load 1 after setting keys \"l\", \"m\", and \"n\"", 1, load2, DELTA);
    }
    
    public static void main(String[] args) {
        System.out.println("a".hashCode()); // 97
        System.out.println("b".hashCode()); // 98
        System.out.println("c".hashCode()); // 99
        System.out.println("d".hashCode()); // 100
        System.out.println("e".hashCode()); // 101
        
        System.out.println("K".hashCode()); // 75
        System.out.println("P".hashCode()); // 80
        System.out.println("C".hashCode()); // 67
        System.out.println("B".hashCode()); // 66
        
        System.out.println("k".hashCode()); // 107
        System.out.println("p".hashCode()); // 112
        System.out.println("c".hashCode()); // 99
        System.out.println("b".hashCode()); // 98
    }
    
}
