/*
 * BrokerLocalServer.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.server;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.IOException;

import suncertify.db.DataAccessObjectFactory;
import suncertify.db.DBAccessException;

/**
 * The BrokerLocalServer class is a singleton which servers as the application
 * server when the application is run in stand-alone mode. It is a subclass of 
 * BrokerServer.
 * 
 * @see BrokerServer
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerLocalServer extends BrokerServer {

    /**
     *
     */
    private static final long serialVersionUID = 20120736666665L;

    /**
     * This is a reference to a Logger object. The logger's name is the fully 
     * qualitified name for this class. 
     *
     *
     * @see java.util.logging.Logger
     */
    private final Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * The reference to the single instance of this class. Access to this is 
     * provided through the getInstance() method.
     *
     * @see #getInstance
     */
    private static BrokerServer instance = null;

    /**
     * The private default construction helps in enforcing the singleton 
     * pattern implementation.
     */
    private BrokerLocalServer() {	
    }

    /**
     * This method returns the reference to the single instance of the class
     * as part of the Singleton pattern implementation.
     *
     * @return The reference to a The single instance of this class.
     */
    static BrokerServer getInstance() {

	if ( instance == null ) {
	    instance = new BrokerLocalServer();
	}
	
	return instance;
    }

    /**
     * This method starts the server. In this case where the server is a 
     * non-network server, all that is required is to get the reference to 
     * the database access object.
     *
     * @throws BrokerServerException If there was an error encountered during
     *         the start startup process.
     */
    @Override
    public void startServer() throws BrokerServerException {
	
	try {
	    
	    dbAccessObject = DataAccessObjectFactory.createDataAccessObject();
	    
	} catch( DBAccessException ex ) {
	    
	    String msg = "Caught exception in BrokerLocalServer.startServer() - "
		+ ex.getMessage();
	    logger.log( Level.SEVERE, msg );

	    BrokerServerException e = new BrokerServerException( msg, ex );
	    logger.throwing( "BrokerLocalServer", "startServer()", e );
	    throw e;
	}
    }

    /**
     * This method does nothing but is needed since the baseclass expects all 
     * subclasses to implement their own server stoppage routine.   
     */
    @Override
    public void stopServer() {
	
    }
}
