package ibm.labs.kc.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ContainersClient {

public static void main( String[] args ) {

    System.out.println("Hello !!");

    Option help = new Option( "help", "print this message" );
    Option create = new Option("c", "create", false, "Create all containers");
    Option delete = new Option("d", "delete", false, "Delete all containers");
    Option list = new Option("l", "list", false, "List all containers");
    Option ncont = Option.builder("n").longOpt( "num-containers" ).desc("Number of containers").hasArg().argName("NCONT").build();

    Options options = new Options();
    options.addOption( help );
    options.addOption( create );
    options.addOption( delete );
    options.addOption( list );
    options.addOption( ncont );

    // create the parser
    CommandLineParser parser = new DefaultParser();
    try {
        // parse the command line arguments
        CommandLine line = parser.parse( options, args );

        if( line.hasOption("help")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "ContainersClient", options );
        }

        if( line.hasOption("create") && line.hasOption( "num-containers" ) ) {
            // initialise the member variable
            String str = line.getOptionValue( "num-containers" );
            System.out.println("num-containers="+str);
        }
    }
    catch( ParseException exp ) {
        // oops, something went wrong
        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
    }

    
}

}