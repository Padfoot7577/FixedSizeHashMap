import static org.junit.Assert.*;

import org.junit.Test;

public class FixedSizedHashMapTest {
    
    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
}
