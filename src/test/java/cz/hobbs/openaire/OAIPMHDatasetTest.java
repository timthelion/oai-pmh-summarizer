package cz.hobbs.openaire;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.containsString;
import org.junit.Before;
import org.junit.Test;

public class OAIPMHDatasetTest
{
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private OAIPMHDataset ds;
    
    @Before
    public void loadDataset() throws Exception {
    	this.ds = new OAIPMHDataset("https://pub.uni-bielefeld.de/oai");
    }
    
    @Test
    public void parseArgsGetsURI()
    {    	
       	assertTrue(ds.dataset.count() > 20);
    }

    @Test
    public void out() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    	
        ds.summarize();
        assertThat(outContent.toString(), containsString("2004"));
        assertThat(outContent.toString(), containsString("publicationYear"));
        
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.out.println(outContent);
    }

}
