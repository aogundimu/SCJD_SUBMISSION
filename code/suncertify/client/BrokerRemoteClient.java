/*
 * BrokerRemoteClient.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.common.AppRunMode;
import suncertify.common.AppConfigManager;
import suncertify.common.AppConfigParam; 

import suncertify.server.BrokerServer;
import suncertify.server.BrokerRMIServer;
import suncertify.server.BrokerRMIServerIF;
import suncertify.server.BrokerServerException;

import suncertify.client.gui.BrokerAppClientGUI;

/**
 * The <code>BrokerRemoteClient</code> is the client used when the application
 * is running in network mode where both the client and server are running in 
 * different JVMs and may also be on different hosts. This is a subclass of 
 * the BrokerClient class and it also implements the DBUpdateListener interface.
 *
 * @see suncertify.client.BrokerClient
 * @see suncertify.client.DBUpdateListener
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerRemoteClient extends BrokerClient implements
                                                    DBUpdateListener {
    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     *
     * @see java.util.logging.Logger
     */
    private final Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * This is a reference to a <code>BrokerRMIServerIF</code> object being used
     * by this remote client.
     *
     * @see suncertify.server.BrokerRMIServerIF
     * @see suncertify.server.BrokerRMIServer
     */
    private  BrokerRMIServerIF databaseServer;
    
    
    /**
     * The no-argument constructor. The constructor has package visibility to 
     * that instances cannot be created outside of the package. Creation of 
     * instances of this class can be achieved through the baseclass which also
     * serves as a factory.
     *
     * @see suncertify.client.BrokerClient#getClient(AppRunMode)
     */
    BrokerRemoteClient() {
	
    }

    /**
     * This method executes the remote client startup process. The process 
     * involves the following steps:
     *
     * <ul>
     * <li> Request client and server configuration parameters by displaying 
     *      the client configuration dialog to the user.
     * <li> Lookup of the remote server from the RMI registry.
     * <li> Exporting itself as a remote object to enable it to receive incoming
     *      calls.
     * <li> Registering itself with the remote server as DB update listener 
     * <li> Obtaining the RemoteClientController reference
     * <li> Creating the client GUI
     * </ul>
     *
     * @see suncertify.client.RemoteClientController
     * @see suncertify.server.BrokerRMIServer#startServer
     * @see suncertify.client.gui.BrokerAppClientGUI
     * 
     * @throws BrokerClientException if an error is encountered during the 
     * startup process
     */
    public void startClient() throws BrokerClientException {

	super.startClient();

	getClientConfigParams( AppRunMode.NETWORK_CLIENT );

	try {

	    AppConfigManager acm = AppConfigManager.getInstance();
	    
	    String serverHostName = acm.get( AppConfigParam.SERVER_HOST_NAME );
	    
	    String serverName = acm.get( AppConfigParam.SERVER_NAME );
	    
	    Registry registry = LocateRegistry.getRegistry( serverHostName );

	    databaseServer = (BrokerRMIServerIF)registry.lookup(serverName);
	    
	    DBUpdateListener listener =
		(DBUpdateListener)UnicastRemoteObject.exportObject(this, 0);
	    
	    clientId = databaseServer.registerUpdateListener(
						(DBUpdateListener)this  );

	    controller =
		ClientController.getController(AppRunMode.NETWORK_CLIENT);

	    String timeout = acm.get(AppConfigParam.RMI_RESPONSE_TIME_OUT);
		      	     
	    System.setProperty("sun.rmi.transport.tcp.responseTimeout",timeout);
	    
	    clientGUI = new BrokerAppClientGUI( controller, this );

	    clientStarted = true;
	    
	} catch( Exception ex ) {
	    BrokerClientException e =
		new BrokerClientException( "Server exception - " +
					   ex.getMessage(), ex );
	    logger.log( Level.SEVERE,
			"Caught an exception during remote client startup - " +
			ex.getMessage() );
	    logger.throwing("BrokerRemoteClient", "startClient()", e );
	    
	    throw e;
	}
    }

    /**
     * This method executes the remote client termination process if the 
     * client startup was previously done successfully. The shutdown process
     * includes the following steps:
     * <ul>
     * <li> Unregistering the client for DB updates from the remote DB 
     * BrokerServer.
     * <li> Unexporting the client so it is removed from the RMI runtime. 
     * <li> Stopping the RemoteClientController
     * </ul>
     * 
     * @see suncertify.client.RemoteClientController#stopController
     * @see suncertify.server.BrokerRMIServer#unregisterUpdateListener(int)
     * @see java.rmi.server.UnicastRemoteObject#unexportObject(Remote,boolean) 
     *
     * @throws BrokerClientException if an error is encountered during the 
     * shutdown process
     */
    @Override
    public void stopClient() throws BrokerClientException {
	if ( clientStarted ) {
	    try {
		databaseServer.unregisterUpdateListener( clientId );
		UnicastRemoteObject.unexportObject( this, true );
		controller.stopController();
	    } catch( Exception ex ) {
		BrokerClientException e =
		    new BrokerClientException("Caught a remote exception - " +
					      ex.getMessage(), ex );
		logger.log( Level.SEVERE,
			    "Caught and exception during server shutdown - " +
			    ex.getMessage() );
		logger.throwing("BrokerRemoteClient", "stopClient()", e );
		throw e;
	    }
	}
    }
}

