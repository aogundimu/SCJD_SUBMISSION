/*
 * BrokerLocalClient.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.common.AppRunMode;

import suncertify.client.gui.BrokerAppClientGUI;

import suncertify.server.BrokerServerException;
import suncertify.server.BrokerServer;

/**
 * The <code>BrokerLocalClient</code> is the client used when the application 
 * the application is running in "stand alone" mode. It extends the BrokerClient
 * class and implements the DBUpdateListener interface.
 *
 * @see suncertify.client.BrokerClient
 * @see suncertify.client.DBUpdateListener
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerLocalClient extends BrokerClient implements DBUpdateListener { 

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     *
     *
     * @see java.util.logging.Logger
     */
    private final Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * This is the reference to the <code>BrokerServer</code> object being used
     * by this client. 
     *
     * @see suncertify.server.BrokerServer
     */
    private BrokerServer databaseServer;
    
    /**
     * The no argument constructor. Notice that is has package access and that 
     * is because instances of this class cannot be created directly. This is 
     * achieved through calling the baseclass which also serves as a factory.
     * 
     * @see suncertify.client.BrokerClient#getClient(AppRunMode)
     */
    BrokerLocalClient() {
	
    }

    /**
     * This method executes the startup process for the local client. The steps 
     * include the following:
     *
     *<ul>
     *<li> Request client configuration parameters by displaying the client 
     *     configuration dialog
     *<li> Acquiring a reference to the BrokerServer object
     *<li> Acquiring a reference to a LocalClientController object 
     *<li> Registering for DB updates with the DB Server
     *<li> Starting the server - this step is needed because the client is 
     *     running is stand alone mode
     *<li> Creating the client GUI 
     *</ul>
     *
     * 
     * @see suncertify.client.LocalClientController
     * @see suncertify.server.BrokerLocalServer#startServer
     * @see suncertify.client.gui.BrokerAppClientGUI
     *
     * @throws BrokerClientException if the process was unsuccessful.
     */
    @Override
    public void startClient() throws BrokerClientException {
	
	super.startClient();
	
	getClientConfigParams( AppRunMode.STAND_ALONE );

	try {

	    databaseServer = BrokerServer.getServer(AppRunMode.STAND_ALONE);

	    controller = ClientController.getController(AppRunMode.STAND_ALONE);

	    clientId = databaseServer.registerUpdateListener( this );
	    
	    databaseServer.startServer();

	    clientGUI = new BrokerAppClientGUI( controller, this );

	    clientStarted = true;
	    
	} catch( Exception exc ) {
	    BrokerClientException e =
	        new BrokerClientException( "Caught exception while starting" +
					     " client - " + exc.getMessage(),
					     exc );
	    logger.log( Level.SEVERE,
			"Caught BrokerServerException during client startup - " +
			exc.getMessage(),
			exc );
	    
	    logger.throwing("BrokerLocalClient", "startClient()", e );
	    
	    throw e;
	}	
    }

    /**
     * This method executes the client shutdown process if the client 
     * startup was previously done successfully. The client shutdown process 
     * includes:
     * 
     * <ul>
     * <li> Unregistering of the client from DB from the DB BrokerServer
     * <li> Stopping the controller.
     * <li> Stopping the server.
     * </ul> 
     *
     * @see suncertify.server.BrokerLocalServer#unregisterUpdateListener(int)
     * @see suncertify.client.LocalClientController#stopController
     * @see suncertify.server.BrokerLocalServer#stopServer
     * 
     * @throws BrokerClientException if an error was encountered during the 
     * shutdown process.
     */
    @Override
    public void stopClient() throws BrokerClientException {

	if (clientStarted ) {
	    
	    try {
		databaseServer.unregisterUpdateListener( clientId );
		
		controller.stopController();	

		databaseServer.stopServer();
		
	    } catch( Exception ex ) {
		BrokerClientException e =
		    new BrokerClientException( "Exception in stopClient - " +
					       ex.getMessage(), ex );
		logger.log( Level.SEVERE,
			    "Caught an exception during server shutdown - " +
			    ex.getMessage() );
		logger.throwing("BrokerLocalClient", "stopClient()", e );

		throw e;
	    }
	}
    }
}
