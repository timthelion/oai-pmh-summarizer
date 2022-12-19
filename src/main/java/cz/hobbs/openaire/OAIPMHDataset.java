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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.spark.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.apache.spark.sql.functions.col;

import static org.apache.spark.sql.functions.col;

import static org.apache.spark.sql.functions.col;

import static org.apache.spark.sql.functions.col;

import static org.apache.spark.sql.functions.col;

import static org.apache.spark.sql.functions.col;


public class OAIPMHDataset
{
    FileCache fileCache;
    public Dataset<Row> dataset;
    SparkSession spark;

    public OAIPMHDataset(String endpoint) throws IOException {
        FileCache fileCache = new FileCache( Paths.get("").toAbsolutePath().normalize() + "/oaipmh-cache/");

        endpoint = endpoint + "?verb=ListRecords";
        // Create a SparkSession
        spark = SparkSession.builder()
            .appName("OAI_PMH_loader")
            .config("spark.master", "local")
            .getOrCreate();

        Logger.getLogger("org.apache").setLevel(Level.WARN);

        String firstPage = endpoint + "&metadataPrefix=oai_datacite";
        System.out.println(firstPage);
        // Load the XML data from the stream into a Dataset<Row>
        Dataset<Row> bareXMLRows = spark.read()
            .format("xml")
            .option("rowTag", "record") // specify the XML tag that defines a row
            .load(fileCache.getFile(firstPage));
        bareXMLRows.show();

        String page = firstPage;
        ArrayList<String> pages = new ArrayList<String>();
        while(true) {
            pages.add(page);
        	// We need to get the resumption token so we list through the pages.
            // It is unclear to me how to load the resumption tokens when I've selected the record tags for my rows. So I load everything again, I know this isn't great. I'm new to spark.
        	// Indeed it seems its impossible https://github.com/databricks/spark-xml/issues/516
	        String resumptionToken = "";
        	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			try {
	            DocumentBuilder builder = domFactory.newDocumentBuilder();
	            Document dDoc = builder.parse(fileCache.getFile(page));

	            XPath xPath = XPathFactory.newInstance().newXPath();
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

        for(String page1 : pages) {
            Dataset<Row> extraRows = spark.read()
                .format("xml")
                .option("rowTag", "record")
                .load(fileCache.getFile(page1));

            extraRows.show();
            bareXMLRows = bareXMLRows.union(bareXMLRows);
        }
        this.dataset = bareXMLRows.selectExpr();
    }

    public void stop()
    {
        this.spark.stop();
    }
}
