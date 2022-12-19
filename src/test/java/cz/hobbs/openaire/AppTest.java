package cz.hobbs.openaire;
import cz.hobbs.openaire.App;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    @Test
    public void parseArgsGetsURI()
    {
        try {
            assertEquals(App.parse_args(new String[] {"-e", "foo"}), Optional.of("foo"));
        } catch (Exception e){
            assertTrue(false);
        }
    }
}
