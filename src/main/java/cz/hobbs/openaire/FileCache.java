package cz.hobbs.openaire;

import java.io.FileWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * File cache so we don't end up downloading things redundantly. Especially usefull during development to save time and allow offline development.
 * 
 * @author Timothy
 *
 */
public class FileCache
{
    public HashMap<String, String> cache;
    String cacheDir;

    String fileNameMap = "oai-pmh-cache.json";

    /**
     * Initializes a file cache object
     * 
     * @param cacheDir is where the files are downloaded. They are mapped to their original urls in a json file also stored in this directory.
     */
    public FileCache(String cacheDir)
    {
        this.cacheDir = cacheDir;
        this.reloadCacheFromDisk();
    }
    
    /**
     * The cache state is stored in a JSON file on disk.
     * This function loads the cache state from disk, discarding whatever cache state is in memory.
     */
    public void reloadCacheFromDisk() {
    
    	File cacheFile = new File(cacheDir, this.fileNameMap);
    	if(!cacheFile.exists()) {
    		this.cache = new HashMap<String, String>();
    		this.save();
    		return;
    	}
        ObjectMapper mapperObj = new ObjectMapper();
        try {
			this.cache = mapperObj.readValue(cacheFile,
			        new TypeReference<HashMap<String,String>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    /**
     * Saves the cache state to disk.
     */
    public void save()
    {

        try {
            // Writing to a file
            File file=new File(cacheDir + "/" + this.fileNameMap);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(new JSONObject(this.cache).toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a file from the cache or the internet.
     * 
     * @param url to fetch
     * @return A local path to a downloaded file
     * @throws IOException
     */
    public String getFile(String url) throws IOException
    {
        if (cache.containsKey(url))
        {
            return cache.get(url);
        }
        else
        {
            String filename = String.format("%s/%d.xml", this.cacheDir, this.cache.size());
            // Download file from url
            InputStream xmlStream;
			try {
				xmlStream = new URL(url).openStream();
	            try (OutputStream outputStream = new FileOutputStream(filename)) {
	                IOUtils.copy(xmlStream, outputStream);
	            } catch (IOException e) {
					e.printStackTrace();
				}
	            this.cache.put(url, filename);
	            this.save();
	            return filename;
			} catch (IOException e1) {
				throw e1;
			}

        }
    }
}
