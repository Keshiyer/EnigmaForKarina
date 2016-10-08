package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author
 */
public class RotorTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTS ***** */
    Alphabet az = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    Rotor r = new Rotor("I", new Permutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", az));

    @Test
       public void testConvertForward() {
           r.set(5);
           assertEquals(r.convertForward(5), 8);
       }

       @Test
       public void testConvertBackward() {
           r.set(5);
           assertEquals(r.convertBackward(9), 7);
       }

       @Test
       public void testAtNotch() {
           assertEquals(r.atNotch(), false);
    }

}
