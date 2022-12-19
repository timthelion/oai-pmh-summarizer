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

public class FileCache
{
    public HashMap<String, String> cache;
    String cacheDir;

    String fileNameMap = "oai-pmh-cache.json";

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

    public FileCache(String cacheDir)
    {
        this.cacheDir = cacheDir;
        this.reloadCacheFromDisk();
    }

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
