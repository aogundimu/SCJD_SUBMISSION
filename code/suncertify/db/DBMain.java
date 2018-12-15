/*
 * DBMain.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.db;

/**
 * The DBMain interface. Defines the data access APIs for the application.
 *
 * @see Data
 * 
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public interface DBMain {

    /**
     * Reads a record from the database file.
     * 
     * @param recNo The number for the record to be read.
     *
     * @return Returns an array where each element is a record value.
     *
     * @throws RecordNotFoundException If the record does not exist or had been
     *         deleted.
     */
    public String [] read(int recNo) throws RecordNotFoundException;
    
    /**
     * Modifies the fields of a record.
     *
     * @param recNo The number of the record to be updated.
     *
     * @param vals The new value for field n appears in data[n]
     * 
     * @throws RecordNotFoundException If the record does not exist or had been
     *         deleted.
     */
    public void update(int recNo, String [] vals) throws RecordNotFoundException;

    /**
     * Deletes a record, making the record number and associated fisk storage
     * available for reuse.
     *
     * @param recNo The number of the record to be deleted.
     *
     * @throws RecordNotFoundException If the record does not exist or had been
     *         deleted.
     */
    public void delete(int recNo) throws RecordNotFoundException;

    /**
     * Returns an array of record numbers that match the specified criteria.
     * Field n in the database file is described by criteria[n]. A null value in
     * criteria[n] matches any field. A non-null value in criteria[n] matches any
     * field value that begins with criteria[n]. (For example, "Fred" matches 
     * "Fred" or "Freddy".
     *
     * @param criteria An array of String objects.
     *
     * @return An array of record numbers. 
     *
     * @throws RecordNotFoundException If no record matching the specified 
     * criteria was found. 
     */
    public int [] find(String [] criteria) throws RecordNotFoundException;

    /**
     * Creates a new record in the database (possibly reusing a deleted entry). 
     * Inserts the given data, and returns the record number of the new record.
     *
     * @param data Values for the new record
     *
     * @return The new record number
     *
     * @throws DuplicateKeyException If a duplicate record already exists.
     */
    public int create(String [] data) throws DuplicateKeyException;

    /**
     * Locks a record so that it can only be updated or deleted by this client.
     * If the specified record is already locked, the current thread gives up
     * the CPU and consumes no CPU cycles until the record is unlocked.
     *
     * @param recNo The number of the record to lock.
     *
     * @throws RecordNotFoundException If the record does not exist or had been
     * deleted.
     */
    public void lock(int recNo) throws RecordNotFoundException;

    /**
     * Releases the lock on a record. 
     * 
     * @param recNo The number of the record to lock.
     *
     * @throws RecordNotFoundException If the record does not exist or had been
     * deleted.
     */
    public void unlock(int recNo) throws RecordNotFoundException;

    /**
     * Determines if a record is currenly locked. Returns true if the record is 
     * locked, false otherwise.
     *
     * @param recNo The number of the record.
     *
     * @return A boolean value true if the record is locked and false otherwise.
     *
     * @throws RecordNotFoundException If the record does not exist or had been 
     * deleted.
     */
    public boolean isLocked(int recNo) throws RecordNotFoundException;

}
