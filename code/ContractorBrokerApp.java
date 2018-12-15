/*
 * ContractorBrokerApp.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;

import java.io.IOException;

import suncertify.server.gui.BrokerAppServerGUI;

import suncertify.client.BrokerClient;

import suncertify.common.AppConfigManager;
import suncertify.common.AppConfigParam;
import suncertify.common.AppRunMode;

/**
 * The <code>ContractorBrokerApp</code> implements the main method for 
 * starting the Contractor Brokerage application.
 *
 *
 * @author Augustine Ogundimu
 * @version 1.1 
 * @since 1.1
 */
public class ContractorBrokerApp {

    /**
     * This method is called by the main method for the configuration and 
     * initialization of all base loggers used in the Contractor Brokerage
     * application.  
     *
     * @param acm A reference to the AppConfigManager object 
     *
     * @see suncertify.common.AppConfigManager
     *
     * @throws IOException is thrown if there are issues accessing the 
     *         properties or log file.
     */
    private static void initiateLoggers( AppConfigManager acm ) throws
                                                             IOException {

	String logFileName = acm.get( AppConfigParam.LOG_FILE_NAME );
	
	FileHandler logHandler = new FileHandler(logFileName, 50_000, 1, true);
	logHandler.setFormatter( new SimpleFormatter() );
		
	String dbLogger = acm.get( AppConfigParam.DB_LOGGER_NAME );
	String serverLogger = acm.get( AppConfigParam.SERVER_LOGGER_NAME );
	String clientLogger = acm.get( AppConfigParam.CLIENT_LOGGER_NAME );
	String commonLogger = acm.get( AppConfigParam.COMMON_LOGGER_NAME );

	Logger baseLogger = Logger.getLogger("suncertify");
	baseLogger.addHandler( logHandler );
	baseLogger.setUseParentHandlers( false );	
	Logger.getLogger(dbLogger);
	Logger.getLogger(serverLogger);
	Logger.getLogger(clientLogger);
	Logger.getLogger(commonLogger);
    }

    
    /**
     * This is the starting point for the Contractor Brokerage application - the
     * main method. 
     *
     * @param args Array of String objects denoting the command line arguments
     */
    public static void main(String ... args) {

	try {
	    
	    AppConfigManager acm = AppConfigManager.getInstance();
	    
	    if ( args.length == 1 ) {
	    				
		if ( acm.get( AppConfigParam.SERVER_ONLY).equals( args[0] ) ) {

		    initiateLoggers( acm );
		    
		    acm.set( AppConfigParam.SERVER_TYPE, args[0] );
		
		    new BrokerAppServerGUI();
		    
		} else if (acm.get(AppConfigParam.STAND_ALONE).equals(args[0])) {

		    initiateLoggers( acm );
		    
		    BrokerClient client =
			BrokerClient.getClient(AppRunMode.STAND_ALONE);
			
		    acm.set( AppConfigParam.SERVER_TYPE, args[0] );

		    client.startClient();
		    
		} else {
		    
		    System.out.println(
				"Usage: java -jar runme.jar [ run-mode ]");
		    System.out.println(
				"   run-mode: \"server\" - to start the server");
		    System.out.println(
		 "              \"alone\" - to start the client in stand alone mode");
		    System.out.println(
		 "                      - to start the network client");
		}
		
	    } else {

		initiateLoggers( acm );
		
		BrokerClient client =
		    BrokerClient.getClient(AppRunMode.NETWORK_CLIENT);
		    
		client.startClient();
	    }
	    
	} catch( Exception ex) {
	    
	    System.out.println("There was a problem running the application - " 
			       + ex.getMessage() + ex );
	    System.exit(0);
	}
    }
}
