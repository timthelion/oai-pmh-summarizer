package cz.hobbs.openaire;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.ml.feature.Bucketizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.apache.spark.sql.functions.*;


/**
 * The OAI_PMH Dataset class loads multi-page OAI_PMH data,
 * breaks it down into columns for easy querying,
 * and provides functions for sumarizing the data in a user friendly fashion.
 * 
 * @author Timothy
 *
 */
public class OAIPMHDataset
{
	/**
	 * A file cache used for fetching endpoint data without doing so redundantly to reduce server load/bandwidth usage.
	 */
    FileCache fileCache;
    /**
     * The loaded dataset.
     */
    public Dataset<Row> dataset;
    SparkSession spark;

    /**
     * This creates a dataset. Note that this includes fetching the dataset from the network and loading it into memory. This is a long running blocking operation.
     * 
     * @param endpoint
     * @throws IOException
     */
    public OAIPMHDataset(String endpoint) throws IOException {
    	String cacheDir = Paths.get("").toAbsolutePath().normalize() + "/oaipmh-cache/";
        FileCache fileCache = new FileCache(cacheDir);

        endpoint = endpoint + "?verb=ListRecords";
        // Create a SparkSession
        spark = SparkSession.builder()
            .appName("OAI_PMH_loader")
            .config("spark.master", "local")
            .getOrCreate();

        spark.sparkContext().setLogLevel("ERROR");

        String firstPage = endpoint + "&metadataPrefix=oai_datacite";
        String page = firstPage;
        
        // We have to put everything in a giant xml file because multiple file inputs aren't supported without a HadoopFsRelationProvider and I don't want to set one up.
        String bigXMLFile = cacheDir + "the-big-one.xml";
        FileWriter bigXMLFileWriter = new FileWriter(bigXMLFile);
        ArrayList<String> pages = new ArrayList<String>();
        while(true) {
	        String localPage = fileCache.getFile(page);
        	pages.add(localPage);
        	// We need to get the resumption token so we list through the pages.
            // It is unclear to me how to load the resumption tokens when I've selected the record tags for my rows. So I load everything again, I know this isn't great. I'm new to spark.
        	// Indeed it seems its impossible https://github.com/databricks/spark-xml/issues/516
	        String resumptionToken = "";
        	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			try {
	            DocumentBuilder builder = domFactory.newDocumentBuilder();
	            Document dDoc = builder.parse(localPage);

	            XPath xPath = XPathFactory.newInstance().newXPath();
	            
	            Node recordsList = (Node) xPath.evaluate("/OAI-PMH/ListRecords", dDoc, XPathConstants.NODE);
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
	            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	            StreamResult xmlOutput = new StreamResult(bigXMLFileWriter);
	            transformer.transform(new DOMSource(recordsList), xmlOutput);
	            
	            resumptionToken = (String) xPath.evaluate("/OAI-PMH/ListRecords/resumptionToken", dDoc, XPathConstants.STRING);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

            // Extract the resumptionToken and completeListSize values from the DataFrame
            
            if (resumptionToken == "") {
                break;
            }
            page = endpoint + "&resumptionToken=" + resumptionToken;
        }
        bigXMLFileWriter.close();
        // Load the XML data from the stream into a Dataset<Row>
        Dataset<Row> bareXMLRows = spark.read()
            .format("xml")
            .option("rowTag", "record")
            .load(bigXMLFile);
        bareXMLRows.printSchema();
        this.dataset = bareXMLRows
            	.withColumn("recordId", col("metadata.resource.identifier._VALLUE"))
            	.withColumn("recordType", col("metadata.resource.resourceType._VALUE"))
            	.withColumn("publicationYear", col("metadata.resource.publicationYear"))
            	.withColumn("authors", col("metadata.resource.creators"));
    }
    
    /**
     * Print data summaries to stdout.
     */
    public void summarize() {
    	this.summarizeByYear();
    	this.summarizeByTypology();
    	this.summarizeInFiveYearPeriods();
    }
    
    public void summarizeInFiveYearPeriods() {
    	System.out.println("Publications by 5 year period");
    	ArrayList<Integer> splits = new ArrayList<Integer>();
    	int thisYear = Year.now().getValue();
    	for (Integer i = 1970; i < thisYear; i+=5) {
    		splits.add(i);
    	}
    	Bucketizer b = new Bucketizer();
    }

	public void summarizeByTypology() {
		System.out.println("Publications by type");
		System.out.println(this.dataset.groupBy("recordType").count().showString(100000, 250, false));
	}

	public void summarizeByYear() {
        System.out.println("Publications by year");
		System.out.println(this.dataset.groupBy("publicationYear").count().showString(100000, 250, false));
	}
	
    public void stop()
    {
        this.spark.stop();
    }
}
