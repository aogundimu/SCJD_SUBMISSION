/*
 * BrokerServerIF.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.server; 

import java.rmi.RemoteException;

import suncertify.db.RecordNotFoundException;
import suncertify.db.DuplicateKeyException;

import suncertify.client.DBUpdateListener;

/**
 * The BrokerServerIF interface defines the APIs implementation requirements 
 * for all the servers in the application.
 *
 * @see BrokerServer
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0 
 */
public interface BrokerServerIF {

    /**
     * Add a new record to the application database.
     *
     * @param  record A reference to a ContractorRecord object. 
     *
     * @throws RemoteException If the implementing server is a remote server and
     *         there was a problem communicating with it.
     *
     * @throws DuplicateKeyException If there is an existing record in the 
     *         database with keys that match the new record.
     *
     * @return An integer value denoting the unique identifier for the newly
     *         created record.
     * @see suncertify.server.BrokerServer#addRecord(ContractorRecord)
     */
    int addRecord(ContractorRecord record) throws RemoteException,
						         DuplicateKeyException;
					
    
    /**
     * Retrieve a record from the application database.
     *
     * @param recNo An integer value uniquely identifying the record to be 
     *         retrieved.
     *
     * @throws RemoteException If the implementing server is a remote server and
     *         there was a problem communicating with it.
     * 
     * @throws RecordNotFoundException If there is no record uniquely 
     *         identified by the record number parameter or if such a record
     *         exists but had been deleted.
     *
     * @return A reference to a ContractorRecord.
     */
    ContractorRecord getRecord(int recNo) throws RemoteException,
					      RecordNotFoundException;


    /**
     * Delete a record from the application database.
     *
     * @param record The reference of the ContractorRecord object to be deleted.
     *
     * @throws RemoteException If the implementing server is a remote server and
     *         there was a problem communicating with it.
     *
     * @throws RecordNotFoundException If there is no record uniquely 
     *         identified by the record number parameter or if such a record
     *         exists but had been deleted.
     *
     * @throws InvalidRecordStateException If the record had changed since when 
     *         it was last read by the client requesting the deletion.
     */
    void deleteRecord(ContractorRecord record) throws RemoteException,
					 InvalidRecordStateException,
					 RecordNotFoundException;

    /**
     * Update the attributes of a record in the application database.
     *
     * @param  oldVal A reference to a ContractorRecord object denoting the 
     * state of the record before the update.
     *
     * @param  newVal A reference to a ContractorRecord object denoting the
     * state of the record after the update.
     *
     * @throws RemoteException If the implementing server is a remote server and
     *         there was a problem communicating with it.
     *
     * @throws InvalidRecordStateException If the record had changed since when 
     *         it was last read by the client requesting the update.
     * 
     * @throws RecordNotFoundException If there is no record uniquely 
     *         identified by the record number parameter or if such a record
     *         exists but had been deleted.
     */
    void updateRecord(ContractorRecord oldVal, ContractorRecord newVal) throws
	                                                  RemoteException,
					       InvalidRecordStateException,
						   RecordNotFoundException;

    /**
     * Book a record.
     *
     * @param record A reference to a ContractorRecord object denoting the record
     * to be booked.
     *
     * @throws RemoteException If the implementing server is a remote server and
     *         there was a problem communicating with it.
     *
     * @throws InvalidRecordStateException If the record had changed since when 
     *         it was last read by the client requesting the booking.
     *
     * @throws RecordNotFoundException If there is no record uniquely 
     *         identified by the record number parameter or if such a record
     *         exists but had been deleted.
     */
    void bookRecord(ContractorRecord record) throws RemoteException,
						   InvalidRecordStateException,
						   RecordNotFoundException;

    /**
     * Release a booked record.
     *
     * @param  record A reference to a ContractorRecord object denoting the record
     * to be released.
     *
     * @throws RemoteException If the implementing server is a remote server and
     *         there was a problem communicating with it.
     *
     * @throws InvalidRecordStateException If the record had changed since when 
     *         it was last read by the client requesting the release.
     *
     * @throws RecordNotFoundException If there is no record uniquely 
     *         identified by the record number parameter or if such a record
     *         exists but had been deleted.
     */
    void releaseRecord(ContractorRecord record) throws RemoteException,
						   InvalidRecordStateException,
						   RecordNotFoundException;

    /**
     * Find records in the application database that match a specified criteria.
     *
     * @param  criteria A reference to a ContractorRecord object denoting the 
     * search attributes.
     *
     * @return An array of ContractorRecord objects denoting the records that 
     * match the specified criteria.
     *
     * @throws RemoteException If the implementing server is a remote server and
     *         there was a problem communicating with it.
     *
     * @throws RecordNotFoundException If no record matching the specified 
     *         criteria was found.
     */
    ContractorRecord[] findRecords(ContractorRecord criteria) throws
	                                                         RemoteException,
	                                                  RecordNotFoundException;
	                                           
    /**
     * Register for database update notifications.
     *
     * @param listener A reference to a DBUpdateListener object.
     *
     * @see suncertify.client.DBUpdateListener
     *
     * @throws RemoteException If the implementing server is a remote server and
     *         there was a problem communicating with it.
     *
     * @return An integer value that uniquely identifies the caller of this 
     *         method.
     */
    int registerUpdateListener(DBUpdateListener listener)
	                                           throws RemoteException;

    /**
     * Unregister for database update notifications.
     *
     * @param listenerId The unique identifier of the client requesting the
     *        deregistration.
     * 
     * @throws RemoteException If the implementing server is a remote server and
     *         there was a problem communicating with it.
     */
    void unregisterUpdateListener(int listenerId) throws RemoteException;
	                                            
}
