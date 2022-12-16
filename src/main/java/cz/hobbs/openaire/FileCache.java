package cz.hobbs.openaire;

import java.io.FileReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileCache
{
    HashMap<String, String> cache = new HashMap();
    String cacheDir;

    String fileNameMap = "oai-pmh-cache.json";

    public reloadCacheFromDisk()
    {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(cacheDir + "/" + this.fileNameMap));
        this.cache.readValue(jsonObject, HashMap.class);
    }

    public save()
    {

        try {
            // Writing to a file
            File file=new File(cacheDir + "/" + this.fileNameMap);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);

            fileWriter.write(countryObj.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileCache(String cacheDir)
    {
        this.cacheDir = cacheDir;
        cache = new ObjectMapper();
        this.reloadCacheFromDisk();
    }

    public String getFile(String url)
    {
        if (cache.containsKey(url))
        {
            return cache.get(url);
        }
        else
        {
            String filename = String.format("%d.xml", this.cache.size());
            // Download file from url
            InputStream xmlStream = new URL(endpointURI).openStream();
            try (OutputStream outputStream = new FileOutputStream(filename)) {
                IOUtils.copy(xmlStream, outputStream);
            }
            this.save()
            return filename;
        }
    }
}
