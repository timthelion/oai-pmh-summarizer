package cz.hobbs.openaire;

import org.apache.commons.cli.*;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws Exception 
    {
        Options options = new Options(); // Modified from https://stackoverflow.com/a/367714/2126889

        Option endpoint = new Option("e", "endpoint", true, "Full URL of OAI-PMH endpiont to use");
        endpoint.setRequired(true);
        options.addOption(endpoint);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            String endpointURI = cmd.getOptionValue("endpoint");
            System.out.println(endpointURI);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("collate-publications", options);

            System.exit(1);
        }
    }
}
