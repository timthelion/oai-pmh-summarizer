package cz.hobbs.openaire;
import cz.hobbs.openaire.OAIPMHDataset;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OAIPMHDatasetTest
{
    @Test
    public void parseArgsGetsURI() throws Exception
    {
        try {
        	OAIPMHDataset ds = new OAIPMHDataset("https://pub.uni-bielefeld.de/oai");
        	ds.dataset.show();	
        	assertTrue(ds.dataset.count() > 60000);
        } catch (Exception e){
            throw e;
        }
    }
}
