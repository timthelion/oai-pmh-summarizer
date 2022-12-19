package cz.hobbs.openaire;
import java.io.File;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class FileCacheTest
{
    @Test
    public void testCache() throws Exception
    {
        try {
            String cache_dir = "/tmp/openaire-testCache";
            //delete cache
            // recursively delete cache directory
            FileUtils.deleteDirectory(new File(cache_dir));

            FileCache cache = new FileCache(cache_dir);
            assertEquals(cache.getFile("https://hobbs.cz/index.html"), "/tmp/openaire-testCache/0.xml");
            assertTrue(new File("/tmp/openaire-testCache/oai-pmh-cache.json").exists());
            FileCache reloaded_cache = new FileCache(cache_dir);
            assertEquals(reloaded_cache.cache.get("https://hobbs.cz/index.html"), "/tmp/openaire-testCache/0.xml");
        } catch (Exception e){
            throw e;
        }
    }
}
