/*
 * BrokerServer.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.server;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.TreeMap;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;

import java.rmi.RemoteException;

import java.util.concurrent.atomic.AtomicInteger;

import suncertify.db.DBMain;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.DBAccessException;

import suncertify.common.AppRunMode;

import suncertify.client.DBUpdateListener;

/**
 * The BrokerServer class is the abstract baseclass for the server types in the 
 * application. It also servers as the factory for the creation of the 
 * appropriate server type depending on the application run mode. It implements
 * the BrokerServerIF interface.
 *
 * @see #getServer(AppRunMode)
 * @see BrokerServerIF
 * @see BrokerLocalServer
 * @see BrokerRMIServer 
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public abstract class BrokerServer implements BrokerServerIF {

    /**
     *
     */
    private static final long serialVersionUID = 20120731155555L;

    /**
     * This is a reference to a Logger object. The logger's name 
     * is the fully qualified name for this class. 
     *
     * @see java.util.logging.Logger
     */
    private final Logger logger = Logger.getLogger( this.getClass().getName() );

    /**
     * A reference to a DBMain object 
     */
    protected DBMain dbAccessObject;

    /**
     * A reference to an AtomicInteger object used by the server for generating
     * unique identifiers for DBUpdateListener registering for database updates.
     *
     * @see java.util.concurrent.atomic.AtomicInteger
     */
    protected AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * A reference to a Map object used for the mapping of unique identifiers 
     * to DBUpdateListener objects.
     */
    protected Map<Integer, DBUpdateListener> updateListeners;

    /**
     * The default constructor. This creates the map for storing the 
     * listener ID/DBUpdateListener pairs.
     */
    protected BrokerServer() {
	
	updateListeners = new TreeMap<>();
    }
    
    /**
     * This method provides the API for the creation of the different server
     * types supported in this application. 
     * 
     * @param runMode The application run mode.
     *
     * @see suncertify.common.AppRunMode
     * @see BrokerLocalServer
     * @see BrokerRMIServer
     *
     * @return A reference to BrokerServer object type depending on the run mode
     *         parameter.
     * 
     * @throws BrokerServerException If an invalid run mode is specified or if an
     *         error was encountered while creating the appropriate server.
     */
    public static BrokerServer getServer(AppRunMode runMode) throws
	                                       BrokerServerException {

	switch( runMode ) {

	case STAND_ALONE:
	    return BrokerLocalServer.getInstance();	

	case SERVER:
	case NETWORK_CLIENT:
	    return BrokerRMIServer.getInstance();
	}

	Logger logger = Logger.getLogger( "suncertify.server.BrokerServer" ); 
	BrokerServerException ex =
	    new BrokerServerException( "Invalid run mode specified");
	logger.log( Level.SEVERE,
		    "Invalid run mode specified to BrokerServer.getServer!" );
	logger.throwing("BrokerServer", "getServer(AppRunMode)", ex );
	throw ex;
    }

    /**
     * This is an abstract method that must be implemented in the subclasses
     * of this class. The expectation is that the startup process will be 
     * unique to each server type.
     *
     * @throws BrokerServerException is thrown when an error is encountered 
     *         in the startup process.
     */
    public abstract void startServer() throws BrokerServerException;

    /**
     * This is an abstract method that must be implemented in the subclasses
     * of this class. The expectation is that the shutdown process will be 
     * unique to each server type.
     *
     * @throws BrokerServerException is thrown when an error is encountered 
     *         in the shutdown process.
     */
    public abstract void stopServer() throws BrokerServerException;

    /**
     * This method adds a new record to the application database and notifies
     * all the database update listeners of the change to the database.
     *
     * @param  record The reference to the ContractorRecord object to be 
     *         added to the database.
     *
     * @return An integer value denoting a unique identifier for the newly
     *         added record.
     *
     * @throws DuplicateKeyException is thrown if a record exists with a key
     *         that matches that of the new record. 
     *
     * @throws RemoteException If the server is a remote server and there was an 
     *         issue communicating with it.   
     *
     * @see suncertify.db.Data#create(String[])     
     */
    public int addRecord( ContractorRecord record ) throws DuplicateKeyException,
                                                    RemoteException {

	int recNo = dbAccessObject.create( record.getAttributes() );

	logger.info( "The record - " + record.toString()
		                       + " was added to the database");
	notifyListeners();

	return recNo;
    }

    /**
     * This method retrieves a record from the application database.  
     *
     * @param recNo An integer denoting the unique identifier for the record.
     *
     * @return A reference to the ContractorRecord object requested.
     * 
     * @throws RecordNotFoundException If a record uniquely identified by the 
     *         record number does not exist or had been deleted.
     *         
     * @throws RemoteException If the server is a remote server and there was an 
     *         issue communicating with it. 
     *
     * @see  suncertify.db.Data#read(int)
     */
    public ContractorRecord getRecord(int recNo) throws RecordNotFoundException,
                                                RemoteException {
	
	String [] attributes = dbAccessObject.read( recNo );
	return new ContractorRecord( recNo, attributes );
    }

    /**
     * This method deletes a record from the application database. It compares
     * the attributes of the record with the current value retrieved from 
     * the database, if there are changes the delete operation will not be done. 
     * The record is locked and unlocked before and after the delete operation
     * respectively.
     *
     *
     * @param record A reference to the ContractorRecord object to be deleted.
     *
     * @throws RecordNotFoundException If a record uniquely identified by the 
     *         record number does not exist or had been deleted.
     *
     * @throws InvalidRecordStateException If the record had changes since it 
     *         was last read by the client requesting the delete operation or 
     *         if the record is currently locked by another thread.
     * 
     * @throws RemoteException If the server is a remote server and there was an 
     *         issue communicating with it. 
     *
     * @see  suncertify.db.Data#delete(int)
     * @see  suncertify.db.Data#lock(int)
     * @see  suncertify.db.Data#unlock(int)
     */
    public void deleteRecord(ContractorRecord record) throws
	                                         RecordNotFoundException,
	                                         InvalidRecordStateException,
                                                 RemoteException {

	
	if ( dbAccessObject.isLocked( record.getRecordNumber() ) ) {

	    String msg = "The record - " + record.toString() +
		               " is currently locked, it cannot be deleted.";
	    logger.log( Level.INFO, msg );	    
	    InvalidRecordStateException e = new InvalidRecordStateException(msg);	    
	    logger.throwing( "BrokerServer", "deleteRecord(int,String[])", e);
	    throw e;
	    
	} else {

	    int recNo = record.getRecordNumber();
	    
	    dbAccessObject.lock( recNo );

	    String [] currVal = dbAccessObject.read( recNo );
	    ContractorRecord currRecord = new ContractorRecord(recNo, currVal);
	    
	    if (record.differsFrom(currRecord) ) {
		
		dbAccessObject.unlock( recNo );
		
		String reason = record.getReason();
		
		String msg = reason + " - Record will not be deleted";
		
		InvalidRecordStateException e =
		               new InvalidRecordStateException( msg );

		logger.info( msg );
		logger.throwing("BrokerServer","deleteRecord(int,String[])", e);
				       
		throw e;
		
	    } else if ( currRecord.isBooked() ) {

		dbAccessObject.unlock( recNo );
		
		String reason = currRecord.getReason();
		
		String msg = reason + " - Record will not be deleted";
		
		InvalidRecordStateException e =
		               new InvalidRecordStateException( msg );

		logger.info( msg );
		
		logger.throwing("BrokerServer","deleteRecord(int,String[])", e);
				       
		throw e;

	    }else {
	    
		dbAccessObject.delete( recNo );
	    
		dbAccessObject.unlock( recNo );

		logger.info("The record - " + record.toString()
			                            + " was deleted");
		notifyListeners();
	    }
	}
    }

    /**
     * This method updates a record in the application database. It compares
     * preupdate attributes of the record with the current value retrieved from 
     * the database, if there are changes the update operation will not be done.
     * The record is locked and unlocked before and after the update operation
     * respectively.
     *
     *
     * @param oldRec A reference to a ContractorRecord object denoting the state 
     *         of the record before the update.
     * 
     * @param newRec A reference to a ContractorRecord object denoting the state
     *        of the record after the update.
     *
     * @throws InvalidRecordStateException If the record had changes since it 
     *         was last read by the client requesting the update operation or if 
     *         the record is currently locked by another thread.
     * 
     * @throws RecordNotFoundException If a record uniquely identified by the 
     *         record number does not exist or had been deleted.
     *
     * @throws RemoteException If the server is a remote server and there was an 
     *         issue communicating with it. 
     * 
     * @see  suncertify.db.Data#update(int, String[])
     * @see  suncertify.db.Data#lock(int)
     * @see  suncertify.db.Data#unlock(int)
     */
    public void updateRecord(ContractorRecord oldRec, ContractorRecord newRec)
	                                       throws InvalidRecordStateException,
	                                              RecordNotFoundException,
                                                       RemoteException {
	
	if ( dbAccessObject.isLocked( oldRec.getRecordNumber() ) ) {

	    String msg = "The record - " + oldRec.toString() +
		               " is currently locked, it cannot be updated.";

	    logger.log( Level.INFO, msg );	    

	    InvalidRecordStateException e = new InvalidRecordStateException(msg);	    

	    logger.throwing("BrokerServer",
		     "updateRecord(ContractorRecord,ContractorRecord)",e);

	    throw e;
	    
	} else {

	    int recNo = oldRec.getRecordNumber();
	    
	    dbAccessObject.lock( recNo );
	    
	    String [] currVal = dbAccessObject.read( recNo );
	    ContractorRecord currRecord = new ContractorRecord(recNo, currVal);

	    if ( ( oldRec.differsFrom( currRecord ) ) ||
		 ( currRecord.isBooked() ) ) {
		
		dbAccessObject.unlock( recNo );

		String reason = oldRec.getReason() + currRecord.getReason();
		String msg = reason + " Record will not be updated!";

		InvalidRecordStateException e =
		            new InvalidRecordStateException( msg );

		logger.info( msg );
		logger.throwing("BrokerServer",
				"updateRecord(ContractorRecord,ContractorRecord)",
				e );
		throw e;
		
	    } else {
		
		dbAccessObject.update(recNo, newRec.getAttributes() );
		
		dbAccessObject.unlock( recNo );

		logger.info( "The record - " + newRec.toString() + " was updated");

		notifyListeners();				
	    }	    	    
	}
    }

    /**
     * This method books a record. It compares the pre booking attributes of the 
     * record with the current value retrieved from the database, if there are 
     * changes the booking operation will not be done. The record is locked and 
     * unlocked before and after the booking operation respectively.
     *
     * 
     * @param record An reference to the ContractorRecord object to be booked.
     *
     * @throws RemoteException If the server is a remote server and there was an 
     *         issue communicating with it. 
     *
     * @throws InvalidRecordStateException If the record had changed since it 
     *         was last read by the client requesting the book operation or if 
     *         the record is currently locked by another thread.
     * 
     * @throws RecordNotFoundException If a record uniquely identified by the 
     *         record number does not exist or had been deleted.
     */
    public void bookRecord(ContractorRecord record) throws RemoteException,
						   InvalidRecordStateException,
						   RecordNotFoundException {
	this.updateRecord(record, record);

	logger.info("The record - " + record.toString() + " has been booked." );
		     
    }
    
    /**
     * This method releases a booked record. It compares pre release attributes 
     * of the record with the current value retrieved from the database, if there 
     * are changes the release operation will not be done. The record is locked 
     * and unlocked before and after the release operation respectively.
     *
     *
     * @param record A reference to the ContractorRecord object to be released.
     *
     * @throws RemoteException If the server is a remote server and there was an 
     *         issue communicating with it. 
     * 
     * @throws InvalidRecordStateException If the record had changed since it 
     *         was last read by the client requesting the release  operation or 
     *         if the record is currently locked by another thread.
     * 
     * @throws RecordNotFoundException If a record uniquely identified by the 
     *         record number does not exist or had been deleted.
     *
     * @see  suncertify.db.Data#update(int, String[])
     * @see  suncertify.db.Data#lock(int)
     * @see  suncertify.db.Data#unlock(int)
     */
    public void releaseRecord(ContractorRecord record) throws
	                                           RemoteException,
						   InvalidRecordStateException,
						   RecordNotFoundException {

	if ( dbAccessObject.isLocked( record.getRecordNumber() ) ) {

	    String msg = "The record - " + record.toString() +
		            " is currently locked, it cannot be released.";

	    logger.log( Level.INFO, msg );	    

	    InvalidRecordStateException e = new InvalidRecordStateException(msg);	    

	    logger.throwing("BrokerServer", "releaseRecord(ContractorRecord)",e);

	    throw e;

	} else {

	    int recNo = record.getRecordNumber();
	    
	    dbAccessObject.lock( recNo );
	    
	    String [] currVal = dbAccessObject.read( recNo );
	    ContractorRecord currRecord = new ContractorRecord(recNo, currVal);
	    
	    if ( ( record.differsFrom( currRecord ) ) ||
		 ( ! currRecord.isBooked() ) ) {
		
		dbAccessObject.unlock( recNo );		

		String reason = record.getReason() + currRecord.getReason();
		String msg = reason + " The record will not be released!";

		InvalidRecordStateException e =
		            new InvalidRecordStateException( msg );

		logger.info( msg );
		logger.throwing( "BrokerServer",
				 "releaseRecord(ContractorRecord)", e);
				  
		throw e;

	    } else {
		
		dbAccessObject.update(recNo, record.getAttributes() );
	    
		dbAccessObject.unlock( recNo );

		logger.info("The record - " + record.toString() +
			    " was released");
			                              
			
		notifyListeners();		
	    }	    	    
	}
    }
    
    /**
     * This method queries the application database for records that match 
     * a set of criteria. 
     * 
     * @param criteria A reference to a ContractorRecord object denoting the
     *         record search criteria. 
     *
     * @return An array of ContractorRecord objects denoting the records that 
     *         match the specified criteria. 
     *
     * @throws RecordNotFoundException If no record matching the specified 
     *         criteria was found.
     * 
     * @throws RemoteException If the server is a remote server and there was an 
     *         issue communicating with it. 
     * 
     * @see suncertify.db.Data#find(String[])
     */
    public ContractorRecord[] findRecords(ContractorRecord criteria) throws
	                                                RecordNotFoundException,
                                                        RemoteException {
	
	int [] recNumbers = dbAccessObject.find( criteria.getAttributes() );

	List<ContractorRecord> result = new ArrayList<>();
	
	for (int rec  : recNumbers) {
	    try {
		String [] recFields = dbAccessObject.read( rec );
		result.add( new ContractorRecord(rec, recFields));		
	    } catch( RecordNotFoundException ex ) {
		logger.log( Level.WARNING,
			    "Record number - " + rec + " does not exist");
	    }
	}

	ContractorRecord [] retArray = new ContractorRecord[ result.size() ];
	for (int i = 0; i < retArray.length; ++i ) {
	    retArray[i] = result.get(i);
	}

	return retArray;
    }


    /**
     * This method adds a DBUpdateListener object to the list of database update
     * listeners in this server.     
     * 
     * @param lstnr A reference to a DBUpdateListener object.
     *
     * @return An integer value uniquely identifying the registered listener in 
     * this server. This number is needed for the call to the 
     * unregisterUpdateListener.
     *
     * @see #unregisterUpdateListener(int)
     * @see suncertify.client.DBUpdateListener
     * 
     * @throws RemoteException If the server is a remote server and there was an 
     *         issue communicating with it. 
     */
    public synchronized int registerUpdateListener(DBUpdateListener lstnr) throws
                                                          RemoteException {
	
	int listenerId = atomicInteger.getAndIncrement();

	updateListeners.put( listenerId, lstnr );

	logger.info( "DBUpdateListener with listener ID - " + listenerId +
		     " was added to the listeners list. " );

	return listenerId;
    }

    /**
     * This method removes a DBUpdateListener from the list of database update
     * listeners registered with this server.
     * 
     * @param listenerId The unique identifier issued to the DBUpdateListener 
     *         when it registered for database update notifications.
     * 
     * @throws RemoteException If the server is a remote server and there was an 
     *         issue communicating with it. 
     */
    public synchronized void unregisterUpdateListener(int listenerId) throws
	                                                      RemoteException {

	updateListeners.remove( listenerId );

	logger.info( "DBUpdateListener with listener ID - " + listenerId +
		     " was removed from the listeners list. " );
    }
    
    /**
     * This method iterates through the list of registered DBUpdateListeners
     * and notifies them of the changes to the database.
     *
     * @see suncertify.client.DBUpdateListener
     */
    protected synchronized void notifyListeners() {

	Collection<DBUpdateListener> listeners = updateListeners.values();

	for ( DBUpdateListener listener : listeners ) {
	    try {
		listener.notifyUpdate();		
	    } catch( RemoteException ex ) {
		logger.log( Level.WARNING,
		"Caught RemoteException during during update notification - " +
			    ex.getMessage(), ex );
			    
	    }
	}
    }
}
