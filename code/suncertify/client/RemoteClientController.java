/*
 * RemoteClientController.java 
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

import suncertify.server.BrokerRMIServerIF;

import suncertify.common.AppConfigManager;
import suncertify.common.AppConfigParam;

/**
 * The RemoteClientController class is a controller type that uses a remote
 * BrokerServer for database operations.  
 *
 * @see suncertify.client.ClientController
 * @see suncertify.client.DBUpdateListener
 * 
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class RemoteClientController extends ClientController implements
						 DBUpdateListener {

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * The constructor. The following steps are executed.
     * 
     * <ul>
     * <li> Using the server hostname and the server name information obtained 
     *      from the APP configuration, it obtains a reference to a BrokerServer
     *      object.
     * <li> It exports itself to the RMI runtime to facilitate the receipt of
     *      the notification of updates from the remote BrokerServer object.
     * <li> It registers for DB updates with the remote BrokerServer object. 
     * </ul>
     *
     * @throws BrokerClientException if an error is encountered in the 
     *         process.*
     * @see suncertify.server.BrokerServer#registerUpdateListener(DBUpdateListener)
     */
    RemoteClientController() throws BrokerClientException {

	try {

	    AppConfigManager acm = AppConfigManager.getInstance();
	    
	    String serverHostName = acm.get( AppConfigParam.SERVER_HOST_NAME );
	    
	    String serverName = acm.get( AppConfigParam.SERVER_NAME );
	    
	    Registry registry = LocateRegistry.getRegistry( serverHostName );

	    databaseServer =  (BrokerRMIServerIF)registry.lookup(serverName);

	    UnicastRemoteObject.exportObject(this, 0);	    
	  	    
	    controllerId =
		databaseServer.registerUpdateListener( (DBUpdateListener)this );
	    
	} catch( Exception ex ) {
	    
	    logger.log( Level.SEVERE,
			"Caught exception while creating controller - " +
			ex.getMessage() );
	    
	    BrokerClientException e =
		new BrokerClientException("Exception registering controller - "
					  + ex.getMessage(), ex );

	    logger.throwing( "RemoteClientController",
			            "RemoteClientController", e);

	    throw e;
	}
    }

    /**
     * This method performs performs the shutdown process for the controller. 
     * This includes unregistering for updates from the remote DB BrokerServer. 
     * It also unexports itself as a remote object from the RMI runtime.
     * 
     * @throws BrokerClientException if an error is encountered in the process.
     *
     * @see suncertify.server.BrokerServer#unregisterUpdateListener(int)
     */
    @Override
    public void stopController() throws BrokerClientException {
	try {

	    databaseServer.unregisterUpdateListener( controllerId );

	    UnicastRemoteObject.unexportObject( this, true );
	    
	} catch( Exception ex ) {
	    
	    logger.log( Level.SEVERE,
			"Caught exception while stopping the controller - " +
			ex.getMessage() );
	    
	    BrokerClientException e =
		new BrokerClientException( "Exception caught stopping the" +
					   " controller - " +
					   ex.getMessage(), ex );
	    
	    logger.throwing( "RemoteClientController",
			            "stopController()", e);

	    throw e;
	}
    }
}
