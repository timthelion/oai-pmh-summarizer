package cz.hobbs.openaire;
import cz.hobbs.openaire.FileCache;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class FileCacheTest
{
    @Test
    public void testCache()
    {
        try {
            String cache_dir = "/tmp/openaire-testCache";
            //delete cache
            // recursively delete cache directory
            FileUtils.deleteDirectory(new File(cache_dir));

            FileCache cache = new FileCache(cache_dir);
            assertEquals(FileCache.getFile("https://hobbs.cz/index.html"), "/tmp/openaire-testCache/1.xml");
            assertTrue(new File("/tmp/openaire-testCache/oai-pmh-cache.json").exists())
        } catch (Exception e){
            assertTrue(false);
        }
    }
}
