package cz.hobbs.openaire;

import org.apache.spark.sql.Dataset;
import org.apache.log4j.*;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.SparkFiles;
import org.apache.spark.SparkContext;

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
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.spark.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.apache.spark.sql.functions.col;

public class OAIPMHDataset
{
    FileCache fileCache;
    public Dataset<Row> dataset;
    SparkSession spark;

    public OAIPMHDataset(String endpoint) throws IOException {
    	String cacheDir = Paths.get("").toAbsolutePath().normalize() + "/oaipmh-cache/";
        FileCache fileCache = new FileCache(cacheDir);

        endpoint = endpoint + "?verb=ListRecords";
        // Create a SparkSession
        spark = SparkSession.builder()
            .appName("OAI_PMH_loader")
            .config("spark.master", "local")
            .getOrCreate();

        Logger.getLogger("org.apache").setLevel(Level.WARN); // This doesn't seem to do anything, but it should work.

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
	        
	            System.out.println("Resumption token:" + resumptionToken);
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
            .option("rowTag", "record") // specify the XML tag that defines a row
            .load(bigXMLFile);
        bareXMLRows.show();
        this.dataset = bareXMLRows.selectExpr();
    }

    public void stop()
    {
        this.spark.stop();
    }
}
