/*
 * ClientController.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client;

import java.rmi.RemoteException;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.common.AppRunMode;

import suncertify.db.RecordNotFoundException;
import suncertify.db.DuplicateKeyException;

import suncertify.server.BrokerServerIF;
import suncertify.server.BrokerServerException;
import suncertify.server.InvalidRecordStateException;
import suncertify.server.ContractorRecord;

/**
 * The ClientController is the abstract base class for all the controllers in the 
 * the Contractor Broker application. It also serves as a parameterized factory for 
 * the creation of ClientController types.
 * 
 * @see #getController(AppRunMode)
 * @see suncertify.client.LocalClientController
 * @see suncertify.client.RemoteClientController
 * 
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0 
 */
public abstract class ClientController {

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     *
     * @see java.util.logging.Logger
     */
    private final Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * A String object denoting the text displayed when the controller 
     * encounters an error when calls are made to the server.
     */
    private static String REMOTE_SERVER_ERROR_MSG =
	           "There was an error in the remote server; reason - ";
    
    /**
     * This is a reference to a BrokerServerIF object. 
     */
    protected BrokerServerIF databaseServer;

    /**
     * This is an integer denoting the unique ID assigned to this controller
     * when it registered with the DB Server for DB updates.
     * 
     * @see suncertify.server.BrokerServer#registerUpdateListener(DBUpdateListener)
     */
    protected int controllerId;
    
    /**
     * This method provides the mechanism for the creation of Controller 
     * objects. The type of object created depends on the parameter.
     * 
     * @param mode This is the application run mode
     *
     * @return A reference to a ClientController object type depending on the 
     *         input parameter.
     *
     * @see suncertify.common.AppRunMode
     * @see suncertify.client.LocalClientController
     * @see suncertify.client.RemoteClientController
     *
     * @throws BrokerClientException If the wrong mode was specified.
     * @throws BrokerServerException If the controller being requested had issues 
     *         communicating with the server. 
     */
    public static ClientController getController(AppRunMode mode) throws
	                      BrokerClientException, BrokerServerException {

	switch( mode ) {
	case STAND_ALONE:
	    return new LocalClientController();
	case NETWORK_CLIENT:
	    return new RemoteClientController();	    
	}

	throw new BrokerClientException( "Invalid run mode " );
    }

    /**
     * This method adds a new record to the database on behalf of the client
     * associated with this controller.
     *
     * @param record A reference to the ContractorRecord object to be added to 
     *        the database.
     *
     * @return An integer value uniquely identifying the added record. The 
     *         value may be the record number of a previously deleted record
     *         that matches the new record being added.
     *
     * @throws DuplicateKeyException if the Name and Location attributes of the
     *         record match that of an existing record in the database.
     *   
     * @throws BrokerServerException if there are errors encountered in the add
     *         process.
     *
     * @see suncertify.server.BrokerServer#addRecord(ContractorRecord)
     */
    public int addRecord( ContractorRecord record ) throws DuplicateKeyException,
                                                      BrokerServerException {
	try {
	    
	    return databaseServer.addRecord( record );
	    
	} catch( RemoteException ex ) {
	    
	    BrokerServerException e =
		new BrokerServerException( REMOTE_SERVER_ERROR_MSG +
					   ex.getMessage(), ex );
	    
	    logger.log( Level.SEVERE,
			"Caught a RemoteException - " + ex.getMessage() );
	    
	    logger.throwing( "ClientController", "addRecord(String)", e );
	    
	    throw e;
	}
    }

    /**
     * This method returns an array of String objects denoting the attributes
     * of the record uniquely identified by the integer record number in the 
     * parameter. The call is relayed to the DB server.
     *
     * @param recNo - An integer value denoting the unique identifier of 
     *        the record to be fetched from the database.
     *
     * @return A reference to a ContractorBroker object denoting the requested
     *         record.
     *
     * @throws RecordNotFoundException If the record number does not match 
     *         any record in the database or if a matching record was found
     *         but had been deleted.
     *
     * @throws BrokerServerException If there were problems communicating
     *         with the DB server.
     *
     * @see suncertify.server.BrokerServer#getRecord(int)
     */
    public ContractorRecord getRecord(int recNo) throws RecordNotFoundException,
                                                   BrokerServerException {
	try {
	    
	    return databaseServer.getRecord(recNo);
	    
	} catch( RemoteException ex ) {
	    
	    logger.log( Level.SEVERE,
			"Caught RemoteException during getRecord call - " +
			ex.getMessage() );
	    
	    BrokerServerException e =
		new BrokerServerException( REMOTE_SERVER_ERROR_MSG +
					   ex.getMessage(), ex );
	    
	    logger.throwing("ClientController", "getRecord(int)", e );
	    
	    throw e;
	}
    }

    /**
     * This method relays the call to delete from the database the record 
     * uniquely identified by the integer value parameter value. This is 
     * accomplished by forwarding the call to the database server.
     *
     * @param record - A reference to the ContractorRecord object to be 
     *        deleted.
     * 
     * @throws RecordNotFoundException If the record number does not match 
     *         any record in the database or if a matching record was found
     *         but had been deleted.
     * 
     * @throws InvalidRecordStateException If the attributes of the record had 
     *         changed since it was fetched from the database. 
     *
     * @throws BrokerServerException If there was a problem communicating with 
     *         the server.
     * 
     * @see suncertify.server.BrokerServer#deleteRecord(ContractorRecord)
     */
    public void deleteRecord( ContractorRecord record ) throws
	                                         RecordNotFoundException,
					         InvalidRecordStateException,
                                                 BrokerServerException {
	try {
	    
	    databaseServer.deleteRecord( record );
	    
	} catch( RemoteException ex ) {
	    
	    logger.log( Level.SEVERE,
			"Caught RemoteException in call to deleteRecord - " +
			ex.getMessage() );
	    
	    BrokerServerException e =
		new BrokerServerException( REMOTE_SERVER_ERROR_MSG +
					   ex.getMessage(), ex );

	    logger.throwing("ClientController",
			       "deleteRecord(ContractorRecord)", e);

	    throw e;
	}
    }

    /**
     * This method forwards requests to update a record to the DB server.
     *
     *
     * @param oldVal A reference to a ContractorRecord object denoting the 
     * state of the record before the update.
     *
     * @param newVal A reference to a ContractorRecord object denoting the
     * state of the record after the update. 
     *
     * @throws RecordNotFoundException - If the recNo does not match any record
     *         in the database or the matching record had been deleted. 
     *
     * @throws InvalidRecordStateException If the attributes of the record had 
     *         changed since it was fetched from the database. 
     *
     * @throws BrokerServerException - If there was a problem communicating with 
     *         the server.
     * 
     * @see suncertify.server.BrokerServer#updateRecord(ContractorRecord,ContractorRecord)
     */
    public void updateRecord(ContractorRecord oldVal, ContractorRecord newVal)
	                                         throws RecordNotFoundException,
						    InvalidRecordStateException,
	                                                BrokerServerException {
	try {
	    
	    databaseServer.updateRecord(oldVal, newVal );
	    
	} catch(RemoteException ex ) {
	    
	    logger.log( Level.SEVERE,
			"Caught RemoteException in call to deleteRecord - " +
			ex.getMessage() );
	    
	    BrokerServerException e =		
		new BrokerServerException( REMOTE_SERVER_ERROR_MSG +
					   ex.getMessage(), ex );

	    logger.throwing( "ClientController",
			     "updateRecord(int,String[],String[]", e);

	    throw e;
	}
    }

    /**
     * This method forwards a record booking request to the database server.
     *
     * @param record A reference to a ContractorRecord object denoting the record
     * to be booked.
     *
     * @throws RecordNotFoundException If the record number does not match 
     *         any record in the database or if a matching record was found
     *         but had been deleted.
     *
     * @throws InvalidRecordStateException If attributes of the record had 
     *         changed since it was read from the database.
     * 
     * @throws BrokerServerException If there were problems communicating
     *         with the DB server.
     *
     * @see suncertify.server.BrokerServer#bookRecord(ContractorRecord)
     */
    public void bookRecord(ContractorRecord record) throws
	                                          RecordNotFoundException,
	                                          InvalidRecordStateException,
                                                  BrokerServerException {

	try {
	    
	    databaseServer.bookRecord( record );
	    
	} catch(RemoteException ex) {
	    
	    logger.log( Level.SEVERE,
			"Caught RemoteException in call to deleteRecord - " +
			ex.getMessage() );

	    BrokerServerException e =
		new BrokerServerException( REMOTE_SERVER_ERROR_MSG +
					           ex.getMessage(), ex );

	    logger.throwing( "ClientController", "bookRecord(int,String[])", e );

	    throw e;
	}
    }

    /**
     * This method forwards a record release request to the database server. 
     *
     *
     * @param record A reference to a ContractorRecord object denoting the record
     * to be released.
     *
     * @throws RecordNotFoundException If the record number does not match 
     *         any record in the database or if a matching record was found
     *         but had been deleted.
     *
     * @throws InvalidRecordStateException If attributes of the record had changed
     *         since it was read from the database.
     *
     * @throws BrokerServerException If there were problems communicating
     *         with the DB server.
     *
     * @see suncertify.server.BrokerServer#releaseRecord(ContractorRecord)
     */
    public void releaseRecord(ContractorRecord record) throws
	                                             RecordNotFoundException,
	                                             InvalidRecordStateException,
                                                   BrokerServerException {
	try {
	    
	    databaseServer.releaseRecord( record );
	    
	} catch( RemoteException ex ) {
	    
	    logger.log( Level.SEVERE,
			"Caught RemoteException in call to releaseRecord - " +
			ex.getMessage() );

	    BrokerServerException e =
		new BrokerServerException( REMOTE_SERVER_ERROR_MSG +
					   ex.getMessage(), ex );
	    
	    logger.throwing("ClientController", "releaseRecord(int,String[]", e);

	    throw e;
	}	
    }

    
    /**
     * This method forwards record search request to the database server.
     *
     * @param criteria A reference to a ContractorRecord object. The attributes
     * of the object denote the search criterion for the corresponding attribute
     * for each record in the database.
     *
     * @return An array of ContractorRecord objects denoting the records that 
     *         match the specified criteria.
     *
     * @throws RecordNotFoundException If not record matching the specified 
     *         criteria was found.
     *
     * @throws BrokerServerException If there were problems communicating with 
     *         the database server.
     *
     * @see suncertify.server.BrokerServer#findRecords(ContractorRecord)
     */
    public ContractorRecord[] findRecords(ContractorRecord criteria) throws
	                                                  RecordNotFoundException,
                                                          BrokerServerException {

	try {
	    
	    return databaseServer.findRecords( criteria );
	    
	} catch( RemoteException ex ) {
	    
	    logger.log( Level.SEVERE,
			"Caught RemoteException in call to findRecords - " +
			ex.getMessage() );

	    BrokerServerException e =
		new BrokerServerException( REMOTE_SERVER_ERROR_MSG +
					   ex.getMessage(), ex );

	    logger.throwing("ClientController", "findRecords(String)", e );

	    throw e;
	}
    }

    
    /**
     * This is the callback for nofification of DB updates.  
     *
     * @throws RemoteException If a communication problem is encountered in 
     *         calls to this method.
     */
    public void notifyUpdate() throws RemoteException {
	
    }

    
    /**
     * The call to this method will result in the controller unregistering
     * itself for DB updates from the DB server.
     *
     * 
     * @throws BrokerClientException If there was a problem communicating with
     *         the server.
     */
    public void stopController() throws BrokerClientException {

	try {
	    
	    databaseServer.unregisterUpdateListener(controllerId);

	} catch( RemoteException ex ) {

	    logger.log( Level.SEVERE,
			"Caught an exception durring contrller shutdown - " +
			ex.getMessage() );

	    BrokerClientException e =
		new BrokerClientException( "Caught remote exception" +
					   " unregistering controller - " +
					   ex.getMessage(), ex );

	    logger.throwing("ClientController", "stopController()", e );
	    
	    throw e;
	}
    }
}
