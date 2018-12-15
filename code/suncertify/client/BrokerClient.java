/*
 * BrokerClient.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.rmi.RemoteException;

import suncertify.common.AppRunMode;

import suncertify.client.gui.ClientConfigDialog;
import suncertify.client.gui.BrokerAppClientGUI;

import suncertify.client.ClientController;

/**
 * The <code>BrokerClient</code> class is the abstract baseclass for all the 
 * client types in the application. It also servers as a factory for the 
 * creation of the different client types.
 * 
 * @see suncertify.client.BrokerLocalClient
 * @see suncertify.client.BrokerRemoteClient
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public abstract class BrokerClient {
    
    /**
     * This is a reference to a Logger object. The logger's name 
     * is the fully qualitified name for this class. 
     *
     *
     * @see java.util.logging.Logger
     */
    private final Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * This is a reference to a <code>ReentrantLock</code> object used for 
     * synchronization during the client startup process.
     * 
     * @see java.util.concurrent.locks.ReentrantLock
     * @see #getClientConfigParams(AppRunMode)
     */
    private ReentrantLock lock;

    /**
     * This is a reference to a <code>Condition</code> object used for 
     * synchronization during the client startup process.
     *
     * @see java.util.concurrent.locks.Condition
     * @see #getClientConfigParams(AppRunMode)
     */
    private Condition condition;
    

    /**
     * This value indicates whether the client had been started or not. It is 
     * used during client shutdown to determine what course of action to take.
     *
     * @see #startClient()
     * @see #stopClient()
     * @see suncertify.client.BrokerLocalClient#startClient
     * @see suncertify.client.BrokerRemoteClient#startClient
     * @see suncertify.client.BrokerLocalClient#stopClient
     * @see suncertify.client.BrokerRemoteClient#stopClient
     */
    protected boolean clientStarted = false;

    /**
     * This is a reference to a <code>BrokerAppClientGUI</code> object. This
     * class implements all main application GUI.
     * 
     * @see suncertify.client.gui.BrokerAppClientGUI
     */
    protected BrokerAppClientGUI clientGUI;

    /**
     * This is a reference to a <code>ClientController</code> object. This acts 
     * as the controller for this client.
     *
     * @see suncertify.client.ClientController
     */
    protected ClientController controller;

    /**
     * This is the integer value uniquely identifying this client from the 
     * the perspective of the server. This value is returned from the server
     * when the client registers for model update notifications.
     *
     * @see #startClient
     * @see suncertify.client.BrokerLocalClient#startClient
     * @see suncertify.client.BrokerRemoteClient#startClient
     */
    protected int clientId;
    
    /**
     * The <code>ClientDialogThread</code> implements the Runnable inteface. Its
     * main responsibility is the display of the client configuration interface.
     *
     * @see java.lang.Runnable 
     */
    private class ClientDialogThread implements Runnable {

	/**
	 * This specifies the runMode for this client
	 */
	private AppRunMode runMode;

	/**
	 * This is the constructor for the <code>ClientDialogThread</code>. 
	 * 
	 * @param mode - the run mode for this client
	 */
	ClientDialogThread(AppRunMode mode) {
	    runMode = mode;
	}

	/**
	 * This is run method. It creates an instance of 
	 * <code>ClientConfigDialog</code> object which results in the display 
	 * dialog GUI.
	 *
	 * @see suncertify.client.gui.ClientConfigDialog
	 */
	public void run() {
	    new ClientConfigDialog(runMode, lock, condition );
	}
    }

    /**
     * The <code>ClientCleanupThread</code> class implements the Runnable 
     * interface. It servers as the shutdown and exit action for this client
     * and the JVM. An instance of this class is created and registered with 
     * the JVM during client startup.
     * 
     * @see #startClient
     * @see java.lang.Runnable
     */
    private class ClientCleanupThread extends Thread {

	/**
	 * Reference to the <code>BrokerClient</code> object denoting this 
	 * client.
	 */
	BrokerClient brokerClient;

	/**
	 * The constructor for the <code>ClientCleanupThread</code> class;
	 * 
	 * @param client A reference to the enclosing <code>BrokerClient</code>
	 * object
	 */
	ClientCleanupThread(BrokerClient client) {
	    
	    brokerClient = client;
	}

	/**
	 * The run method simply calls the enclosing client stopClient method.
	 * 
	 * @see suncertify.client.BrokerClient#stopClient
	 */
	public void run() {
	    try {
		brokerClient.stopClient();		
	    } catch( BrokerClientException ex ) {
		logger.log( Level.SEVERE,
			    "Caught exception while stopping client - " +
			    ex.getMessage(),
			    ex );
	    }
	}
    }
    
    /**
     * This method returns a reference to a <code>BrokerClient</code> object
     * depending on the runMode parameter. The client can be run in two modes
     * <ul>
     * <li> STAND_ALONE - The client and the server are running in the same JVM
     * <li> NETWORK_CLIENT - The client and the server are in separate JVMs; 
     *      they may or may not be running on different machines.
     * </ul>
     *
     * @param runMode The runMode parameter indicates the type of client object
     * that should be returned.
     * 
     * @see suncertify.common.AppRunMode
     *
     * @return A reference to a <code>BrokerClient</code> object, the concrete
     * type depends on the runMode parameter
     *
     * @throws BrokerClientException if the wrong mode is specified.
     */
    public static BrokerClient getClient(AppRunMode runMode) throws
	                                         BrokerClientException  {

	switch( runMode ) {
	    
	case STAND_ALONE:
	    return new BrokerLocalClient();	    
	case NETWORK_CLIENT:
	    return new BrokerRemoteClient();
	}	

	throw new BrokerClientException("Invalid run mode");
    }

    /**
     * This method creates the client dialog configuration GUI and then blocks
     * until the user has entered the appropriate configuration parameters in 
     * the dialog. This method is called during the client startup process.
     *  
     *
     * @param mode The specifies the client run mode for the client
     *
     * @see suncertify.common.AppRunMode
     * @see suncertify.client.BrokerClient.ClientDialogThread
     * @see suncertify.client.BrokerLocalClient#startClient
     * @see suncertify.client.BrokerRemoteClient#startClient
     */
    protected void getClientConfigParams( AppRunMode mode ) {
	
	lock = new ReentrantLock();
	condition = lock.newCondition();
	
	try {	    
	    lock.lock();
	    Thread dialogThread = new Thread( new ClientDialogThread(mode) );
	    dialogThread.start();
	    logger.info("Started the ClientDialogThread, waiting for notification");
	    condition.await();
	    logger.info("Received notification from ClientDialogThread!!");
	} catch( InterruptedException exc ) {
	    logger.log( Level.SEVERE,
			"Caught an InterruptedException while waiting for " +
			"client dialog thread - " + exc.getMessage(),
			exc );
	}finally {
	    lock.unlock();
	    lock = null;
	    condition = null;
	}	
    }

    
    /**
     * This method creates an instance of a <code>ClientCleanupThread</code>
     * object and registers it with the JVM. This ensures that appropriate 
     * cleanup steps are taken when the client crashes or exits.
     * <p> The subclasses of this class override this method, but calls are made 
     * to this baseclass version of this method to ensure registration of the
     * cleanup thread with the JVM. 
     * 
     * @see suncertify.client.BrokerLocalClient#startClient
     * @see suncertify.client.BrokerRemoteClient#startClient
     *
     * @throws BrokerClientException when errors are encountered in the 
     * client startup process.
     */
    public void startClient() throws BrokerClientException {

	Runtime.getRuntime().addShutdownHook( new ClientCleanupThread(this) );
    }

    /**
     * This method implements the client shutdown process.
     * 
     * @see suncertify.client.BrokerLocalClient#stopClient
     * @see suncertify.client.BrokerRemoteClient#stopClient
     * 
     * @throws BrokerClientException when erros are encountered in the 
     * client shutdown process.
     */
    public abstract void stopClient() throws BrokerClientException;

    /**
     * This method is the callback for receiving notifications of DB updates. 
     * The client registers with the DB server at startup time in order to 
     * these notifications.
     *
     * @see suncertify.client.BrokerLocalClient#startClient
     * @see suncertify.client.BrokerRemoteClient#startClient
     *
     * @throws RemoteException if there is any communication error during a call
     *         to this method.
     */
    public void notifyUpdate() throws RemoteException {
	
	logger.info("Received update notification from the DB server!!!" );
	clientGUI.update();
    }
}
