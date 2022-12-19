package cz.hobbs.openaire;

import java.util.Optional;

import org.apache.commons.cli.*;

public class App
{
    /**
     * Parse CLI args and print help message
     * 
     * @param args are the cli arguments passed by the user
     *  @return endpoint to load from if set
     */
    public static Optional<String> parse_args(String[] args) throws Exception {
        Options options = new Options(); // Modified from https://stackoverflow.com/a/367714/2126889

        Option endpoint = new Option("e", "endpoint", true, "Full URL of OAI-PMH endpiont to use");
        endpoint.setRequired(true);
        options.addOption(endpoint);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);
            return Optional.of(cmd.getOptionValue("endpoint"));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("collate-publications", options);

            return Optional.empty();
        }
    }
    
    /**
     * The main function.
     * 
     * @param args
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception
    {
        Optional<String> endpointURIOptional = App.parse_args(args);

        if (!endpointURIOptional.isPresent()) {
            System.exit(1);
        }

        String endpointURI = endpointURIOptional.get();
        OAIPMHDataset data = new OAIPMHDataset(endpointURI);
        data.summarize();
    }
}
