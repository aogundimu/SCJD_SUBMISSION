/*
 * LocalClientController.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.server.BrokerServer;

import suncertify.common.AppRunMode;

/**
 * The LocalClientController class is a controller type that uses a local
 * BrokerServer for database operations. 
 *
 * @see suncertify.client.ClientController
 * @see suncertify.client.DBUpdateListener 
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class LocalClientController extends ClientController implements
						       DBUpdateListener {
    
    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * The constructor. It obtains a reference to the appropriate BrokerServer
     * object and register for database updates notifications with the server.
     * 
     * @throws BrokerClientException If an error is encountered in the process.
     */
    LocalClientController() throws BrokerClientException {

	try {

	    databaseServer = BrokerServer.getServer( AppRunMode.STAND_ALONE );
	    
	    controllerId = databaseServer.registerUpdateListener( this );
	    
	} catch( Exception ex ) {
	    
	    logger.log( Level.SEVERE,
			"Caught exception while creating controller - " +
			ex.getMessage() );
	    
	    BrokerClientException e =
		new BrokerClientException( "Exception registering controller " +
					   ex.getMessage(), ex );
	    
	    logger.throwing("LocalClientController","LocalClientController", e);

	    throw e;
	}
    }  
}
