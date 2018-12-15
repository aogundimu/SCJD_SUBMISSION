/*
 * BrokerRMIServer.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.db.DataAccessObjectFactory;

import suncertify.common.AppConfigManager;
import suncertify.common.AppConfigParam;

/**
 * The BrokerRMIServer class is a singleton which servers as the application
 * server when the application is run in server mode. It extends the BrokerServer
 * class and implements the BrokerRMIServerIF. 
 * 
 * @see BrokerRMIServerIF
 * @see BrokerServer
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerRMIServer extends BrokerServer
                            implements BrokerRMIServerIF {

    /**
     *
     */
    private static final long serialVersionUID = 20120731125400L;

    /**
     * This is a reference to a Logger object. The logger's name is the fully 
     * qualitified name for this class. 
     *
     * @see java.util.logging.Logger
     */
    private final Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * The ServerCleanp class serves as a shutdown hook with the java runtime. 
     * This ensures that in the case of a system shutdown or exit, the necessary
     * cleanup steps are done from the RMI server perspective. It extends the 
     * Thread class and overrides the run() method.
     */
    private class ServerCleanup extends Thread {

	/**
	 * The reference to the RMI server object enclosing this class.
	 */
	BrokerRMIServer server;

	/**
	 * The constructor.
	 * @param server A reference to a BrokerRMIServer object.
	 */
	ServerCleanup(BrokerRMIServer server) {
	    this.server = server;
	}

	/**
	 * The run method calls the stopServer() method of the BrokerRMIServer
	 * object. 
	 * 
	 * @see suncertify.server.BrokerRMIServer#stopServer()
	 */
	@Override
	public void run() {
	    try {
		this.server.stopServer();		
	    } catch( BrokerServerException exc ) {
		logger.log( Level.SEVERE,
			    "Caught an exception is ServerCleanup.run() - " +
			    exc.getMessage() + " - " +  exc );
			    
	    }
	}
    }
	
    /**
     * The reference to the single instance of this class. Access to the 
     * instance is provided in the getInstance() method.
     *
     * @see #getInstance
     */
    private static BrokerRMIServer instance = null;

    /**
     * A String object denoting the RMI server name. This value is read 
     * from the application configuration file.
     *
     * @see #startServer
     */
    private String serverName;

    /**
     * An integer denoting the RMI listening port number. This value is 
     * read from the application configuration file.
     *
     * @see #startServer
     */
    private int portNumber;

    /**
     * A boolean value indicating whether the server is running or not.
     */
    private boolean serverRunning = false;

    /**
     * A boolean value indicating whether the RMI registry for this server
     * had been created or not.
     */
    private boolean registryCreated = false;

    private boolean serverExported = false;

    private boolean serverBound = false ;

    /**
     * A reference to the RMI registry.
     */
    private Registry registry;
    
    /**
     * A private default constructor which aids in enforcing the singleton 
     * pattern implementation.
     */
    private BrokerRMIServer() {
	
    }

    /**
     * This method provides access to the single instance of this class as part
     * of the singleton pattern implementation.
     *
     * @return The reference to the only instance of this object.
     */
    static BrokerServer getInstance() {
	
	if ( instance == null ) {
	    instance = new BrokerRMIServer();
	}
	
	return instance;
    }

    /**
     * This method executes the RMI server startup process. The steps include the
     * following:
     * 
     * <ul>
     * <li> It retrieves the server configuration parameters from the application
     *      configuration file. This includes the RMI server name and port number.
     * <li> It creates an RMI registry at the port specified in the application
     *      configuration file.
     * <li> It exports the single instance on of this class to the RMI runtime
     *      to enable to receive remote calls.
     * <li> It binds the server to the RMI registry.
     * <li> It creates an instance of the data access object.
     * <li> It registers an instance of the ServerCleanup thread with the java
     *      runtime as shutdown hook.
     * </ul>
     *
     * @throws BrokerServerException is thrown if an error is encountered during
     *         startup process. 
     */
    public synchronized void startServer() throws BrokerServerException {
	
	try {

	    dbAccessObject = DataAccessObjectFactory.createDataAccessObject();
		    
	    AppConfigManager configMgr = AppConfigManager.getInstance();

	    serverName = configMgr.get( AppConfigParam.SERVER_NAME );


	    portNumber = Integer.parseInt(
		       configMgr.get( AppConfigParam.SERVER_PORT_NUMBER) );

	    if ( registryCreated ) {

		registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
		
	    } else {

		registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		registryCreated = true;
	    }

	    BrokerRMIServerIF serverStub = null;
	    
	    if ( ! serverExported ) {
		serverStub = (BrokerRMIServerIF)UnicastRemoteObject.exportObject(
				       (BrokerRMIServerIF)instance, portNumber);
	    }

	    if ( ! serverBound ) {
		registry.rebind( serverName, serverStub );
	    }

	    String timeout = configMgr.get(AppConfigParam.RMI_RESPONSE_TIME_OUT);
		      		
	    System.setProperty("sun.rmi.transport.tcp.responseTimeout", timeout);
	    
	    Runtime.getRuntime().addShutdownHook( new ServerCleanup(this) );

	    serverRunning = true;

	} catch( Exception exc ) {
	    String msg = "Caught exception starting Server - "
		                          + exc.getClass().getName();
	    logger.log(Level.SEVERE, msg, exc );
	    BrokerServerException e = new BrokerServerException(msg, exc );      
	    logger.throwing("BrokerRMIServer", "startServer()", e );
	    throw e;
	}
    }

    /**
     * This method performs the process of suspending the RMI server if the 
     * server is up and running. The process involves a couple of steps.
     * 
     * <ul>
     * <li> It unbinds itself from the RMI registry.
     * <li> It unexport itself from the RMI runtime thus disabling the receipt
     *      of remote calls.
     * </ul>
     *
     * @throws BrokerServerException is thrown if an error is encountered during
     * the server shutdown process.
     */
    public synchronized void stopServer() throws BrokerServerException {

	if ( serverRunning ) {
	    try {
		registry = LocateRegistry.getRegistry( portNumber );
		
		registry.unbind( serverName );

		serverBound = false;
		
		UnicastRemoteObject.unexportObject( this, true );

		serverExported = false;
		
		serverRunning = false;
		
	    } catch( Exception exc  ) {
		String msg = "Caught exception stopping Server - "
		                       + exc.getClass().getName();
		logger.log(Level.SEVERE, msg, exc );
		BrokerServerException e = new BrokerServerException(msg, exc );      
		logger.throwing("BrokerRMIServer", "stopServer()", e );
		throw e;		
	    }
	} 
    }
 }
