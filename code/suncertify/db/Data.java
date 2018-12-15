/*
 * Data.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.db;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.lang.ref.SoftReference;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.common.AppConfigManager;
import suncertify.common.AppConfigParam;


/**
 * The Data class is a singleton that manages access to the application database
 * file. Following are the descriptions of some noteworthy behaviors of the class.
 *
 * <p> All the database records are cached and SoftReferences are used for the 
 * record references. This is in anticipation of possible garbage collection 
 * that may be induced by memory depletion. 
 *
 * <p> All operations that result in modifications to database entries are 
 * to the database file as part of the operation. This helps to guard against
 * loss of transactions and also it also provides support for the use of 
 * SoftReferences.
 *
 * <p> There is always the  assumption that all arrays of String objects passed 
 * as arguments to some methods in this class has a six elements. It is also 
 * assumed that the elements are in this order: 
 * 
 * <ul>
 * <li> Name - element 0
 * <li> Location - element 1
 * <li> Specialities - element 2
 * <li> Size - element 3
 * <li> Rate - element 4
 * <li> Owner - element 5
 * </ul>
 *
 * When the array argument denotes a record, the elements of the array are 
 * interpreted as the attributes using the mapping above. In the case of search 
 * criteria, each element is interpreted as the criterion for the corresponding
 * record attribute using the mapping. 
 *
 * @see DBMain
 * 
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class Data implements DBMain, AutoCloseable {

    /**
     *
     */
    private static final long serialVersionUID = 20120777777775L;

    /**
     * This is a reference to a Logger object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());


    /** 
     * The DatabaseCleanup class provides the mechanism for ensuring that 
     * the physical database file is closed in the event of a sytem shutdown
     * or exit. The class extends the Thread class and it is used as a shutdown
     * hook with the Runtime.
     */
    private class DatabaseCleanup extends Thread {

	/**
	 * A reference to a Data object.
	 */
	Data data;

	/**
	 * The constructor.
	 *
	 * @param data A reference to a Data object. This is the reference to the
	 *        instance enclosing this class.
	 */
	DatabaseCleanup( Data data ) {
	    this.data = data;
	}

	/**
	 * The run method. It calls the close method on the Data class instance.
	 */
	public void run() {
	    this.data.close();
	}
    }
    
    /**
     * The DatabaseLock class provides the mechanism for logical record locks. It
     * is a subclass of ReentrantLock. By extending the ReentrantLock class, 
     * it gains access to the protected member for accessing the thread that
     * currently owns the lock.
     *
     * @see java.util.concurrent.locks.ReentrantLock
     */
    private class DatabaseLock extends ReentrantLock {

	/**	
	 * 
	 */
    private static final long serialVersionUID = 1L;
		
	/**
	 * An atomic integer for keeping track of threads waiting for or owning
	 * this lock.
	 */
	private AtomicInteger holdCount = new AtomicInteger(0);

	/**
	 * The default constructor calls the constructor of the base class with
	 * the fair option. This ensures that the lock uses "fair ordering"
	 * policy.
	 * 
	 * @see java.util.concurrent.locks.ReentrantLock#ReentrantLock(boolean)
	 */
	DatabaseLock() {
	    super( true );
	}

	/**
	 * Method for retrieving the owner of this lock.
	 *
	 * @return A reference to a Thread object that currently owns the lock.
	 */
	protected Thread getOwner() {
	    return super.getOwner();
	}

	/**
	 * An integer value indicating the hold count of this thread.
	 */
	public int getHoldCount() { 	    
	    return holdCount.get();
	}

	/**
	 * Increments the hold count on this lock and calls the same 
	 * name method in the superclass.
	 */
	@Override
	public void lock() {
	    holdCount.getAndIncrement();
	    super.lock();
	}

	/**
	 * Decrements the hold count on this lock and calls the same name
	 * method in the superclass.
	 */
	@Override
	public void unlock() {
	    holdCount.getAndDecrement();
	    super.unlock();
	}
    }

    /**
     * This is a reference to the only instance of this class. Access to this 
     * object is through the getInstance() method.
     *
     * @see #getInstance()
     */
    private static DBMain instance = null;

    /**
     * This is the reference to a Map for storing the name/size pair information
     * for the database record attributes.
     *
     */
    private Map<String, Short> dbMetaData = new LinkedHashMap<>();

    /**
     * This is a reference to a RandomAccessFile used for accessing the physical
     * database file.
     *
     * @see java.io.RandomAccessFile
     */
    private RandomAccessFile databaseFile;

    /**
     * This is a reference to a TreeMap object for storing the 
     * record number/record reference pairs. This is the database cache.
     */
    private Map<Integer, SoftReference<DBRecord>> dbCache = new TreeMap<>();

    /**
     * This is a reference to a Map object for storing record number/DatabaseLock 
     * pairs.
     */
    private Map<Integer, DatabaseLock> lockCache = new HashMap<>();
    
    /**
     * An interger value denoting the database file cookie. This value is read 
     * from the database file.
     */
    private int fileCookieValue;
    
    /**
     * An integer value denoting the number of bytes preceding the first record
     * in the database file.
     */
    private int offsetToRecZero;

    /**
     * The Read/Write lock used for synchronization
     */
    private ReentrantReadWriteLock masterLock;

    /**
     * The write lock component of the read/write lock.
     */
    private ReentrantReadWriteLock.WriteLock writeLock;

    /**
     * The read lock component of the read/write lock.
     */
    private ReentrantReadWriteLock.ReadLock readLock;
	
    /**
     * An reference to an AtomicInteger for generating record numbers 
     * when the database records are being read from the database file 
     * for caching and also when a new record is added to the database.
     */
    private AtomicInteger recordNumber = new AtomicInteger(0);

    /**
     * An integer value denoting the size of each record. This value is 
     * calculated during the construction of this class.
     * 
     * @see #Data()
     */
    private int recordSize;

    /**
     * This is the constructor for this class. It does the following:
     *
     * <ul>
     * <li> Retrieves the database file name from the configuration manager and
     *      create a RandomAccessObject with the file name.
     * <li> It constructs the database schema object with values read from the
     *      database file.
     * <li> It constructs the database cache by reading each record from the file
     *      until the end of the database file is reached. 
     * <li> It constructs the lock objects needed for access synchronization. 
     * <li> It registers an instance of the DatabaseCleanup thread with the java
     *      runtime.
     * </ul>
     *
     * @throws DBAccessException If an error is encountered while accessing the 
     *         physical database file or if the file format is invalid.
     */
    private Data() throws DBAccessException {

	try {
	    
	    AppConfigManager configMgr = AppConfigManager.getInstance();
	    
	    String dbLocation = configMgr.get( AppConfigParam.DB_FILE_NAME );

	    databaseFile = new RandomAccessFile( dbLocation.trim(), "rws" );
	    
	    /* This is the first 4 bytes - the file cookie*/
	    fileCookieValue = databaseFile.readInt();
	    
	    /* Read the offset to the first record 4 bytes */
	    offsetToRecZero = databaseFile.readInt();
	    
	    /* No of fields per record. 2 bytes */
	    short fieldsPerRec = databaseFile.readShort();

	    recordSize = 0; 

	    for ( int i = 0; i < fieldsPerRec;  ++i ) {
		short length = databaseFile.readShort();
		byte[] buffer = new byte[length];
		int num = databaseFile.read( buffer, 0, length );
		String fieldName = new String( buffer );
		short fieldLength = databaseFile.readShort();		
		dbMetaData.put( fieldName, fieldLength );
		recordSize += fieldLength;
	    }

	    logger.log(Level.INFO, "Finished caching database metadata" );

	    /* Add the length of the record flag */
	    recordSize += 2;
	    
	    byte [] buf = new byte[recordSize - 2];

	    while ( databaseFile.getFilePointer() < databaseFile.length() ) {

		short recFlag = databaseFile.readShort();
		int bytesRead = databaseFile.read(buf, 0, (recordSize - 2));

		if ( bytesRead < (recordSize - 2 ) ) {
		    
		    String msg = "Corrupted DB file, invalid length";
		    logger.severe( msg );
		    DBAccessException e = new DBAccessException( msg );
		    logger.throwing("Data", "Data()", e );
		    throw e;
		}

		int recNo = recordNumber.getAndIncrement();
		DBRecord rec = new DBRecord(buf, dbMetaData, recNo, recFlag);
		dbCache.put(recNo, new SoftReference<DBRecord>(rec));
	    }	    

	    logger.info("Read and cached a total of " + dbCache.size()
		                   + " records" );
	    
	    masterLock = new ReentrantReadWriteLock(true);
	    readLock = masterLock.readLock();
	    writeLock = masterLock.writeLock();

	    Runtime.getRuntime().addShutdownHook( new DatabaseCleanup(this) );
	    
	} catch( IOException exc ) {
	    logger.log( Level.SEVERE, "Database file error", exc );
	    DBAccessException e = new DBAccessException(exc.getMessage(), exc);
	    logger.throwing( "Data", "Data()", e );
	    throw e;
	}
    }

    /**
     * A method for creating a text message when a record has been deleted. 
     *
     * @param record A reference to a DBRecord of object. 
     *
     * @return A String object denoting the "record" has been delete message.
     */
    private String getDeletedRecordMsg( DBRecord record ) {

	return "Record with key - " + record.getName().trim() + "/"
	        + record.getLocation().trim() + " has already been deleted!! ";
    }

    /**
     * This is a class method for retrieving the refrence to the only instance
     * of this class.
     * 
     * @return A reference to a DBMain object that is the only instance of this
     *         class.
     *
     * @throws DBAccessException is thrown if errors were encountered in the 
     *         construction of this classes only instance.
     */
    public static DBMain getInstance() throws DBAccessException {

	if (instance == null) {
	    
	    instance = new Data();	    
	} 
	
	return instance;
    }

    /**
     * This method recaches a record that had been garbage collected. This could
     * be the case when the SoftReference to the object returns null which 
     * indicates that the record has been garbage collected.
     * 
     * @param  recNo An integer value denoting the unique identifier of the 
     *         record to be recached.
     *
     * @return A reference to a Record object.
     *
     * @throws IOException If an error was encountered while accessing the 
     *         physical database file.
     *
     * @see java.lang.ref.SoftReference#get
     */
    private DBRecord recacheRecord( int recNo ) throws IOException {

	writeLock.lock();
	
	try {
	    databaseFile.seek(offsetToRecZero + recNo * recordSize);
	    short recFlag = databaseFile.readShort();
	    byte[] buf = new byte[recordSize - 2];
	    DBRecord record = new DBRecord(buf, dbMetaData, recNo, recFlag);
	    dbCache.put(recNo, new SoftReference<DBRecord>(record));
	    logger.log( Level.WARNING, "Recached record - " + record.toString() );
	    return record;
	} finally {
	    writeLock.unlock();
	}
    }
    
    /**
     * This method reads from the database a record with the number in the input
     * parameter.
     *
     * @param recNo This is an integer denoting the unique identifier of the 
     *        record to be read.
     *
     * @return A reference to an array of String objects denoting the attributes
     *         of the record.
     *
     * @throws RecordNotFoundException is thrown if a record with the specified 
     *         record number does not exist or had been deleted.
     *
     * @throws DBAccessException If an error is encountered while accessing the 
     *         physical database file. 
     */
    public String [] read(int recNo) throws RecordNotFoundException,
                                            DBAccessException {

	readLock.lock();
		
	try {	    
	    SoftReference<DBRecord> recRef = dbCache.get( recNo );

	    if ( recRef == null ) {
		String msg = "Record number " + recNo + " does not exist";
		logger.warning( msg );
		RecordNotFoundException e = new RecordNotFoundException(msg);
		logger.throwing("Data", "read()", e );
		throw e;		
	    } else {

		DBRecord record = recRef.get();

		if ( record == null ) {
		    readLock.unlock();
		    record = recacheRecord( recNo );
		    readLock.lock();
		}
		
		if ( record.isDeleted() ) {
		    String msg = getDeletedRecordMsg(record);
		    logger.info( msg );
		    RecordNotFoundException e = new RecordNotFoundException(msg);
		    logger.throwing("Data", "read()", e );
		    throw e;
		    
		} else {
		    return record.getFieldsValues();
		}
	    }
	} catch( IOException exc ) {
	    logger.log(Level.SEVERE, "Database file error - " + exc );	    
	    DBAccessException e = new DBAccessException(exc.getMessage(), exc);
	    logger.throwing( "Data", "read()", e );
	    throw e;
	} finally {	    
	    readLock.unlock();
	}
    }

    /**
     * This method updates the record uniquely identified by the record number 
     * parameter with the values in the array of String object parameter.
     *
     * @param  recNo An integer uniquely identifying the record to be updated.
     *
     * @param  data An array of String objects denoting the new attributes for
     *         the record.
     * 
     * @throws RecordNotFoundException is thrown if a record with the specified 
     *         record number does not exist or had been deleted.
     * 
     * @throws RecordNotFoundException is thrown if a record with the specified 
     *         record number does not exist or had been deleted.
     *
     * @throws DBAccessException is thrown if the record being updated is not 
     *         currently locked. If the record is locked but the thread 
     *         attempting to do the update does not own the lock, this exception
     *         is also thrown. This exception is also thrown if an error is 
     *         encountered while accessing the physical database file. 
     */
    public void update(int recNo, String[] data) throws RecordNotFoundException,
                                                        DBAccessException {

	writeLock.lock();
    
	try {
	    SoftReference<DBRecord> recRef = dbCache.get( recNo );

	    if ( recRef == null ) {
		String msg = "Record number - " + recNo + " does not exist";
		logger.log(Level.WARNING, msg);
		RecordNotFoundException e = new RecordNotFoundException(msg);
		logger.throwing("Data", "update()", e );
		throw e;	    
	    } else {

		DatabaseLock lock = lockCache.get( recNo );

		if ( lock == null ) {
		    String msg = "Record - " + recNo
			           + " is not locked, cannot be updated";
		    logger.log(Level.WARNING, msg);
		    DBAccessException e = new DBAccessException(msg);
		    logger.throwing("Data", "update()", e );
		    throw e;
		    
		} else {
		    
		    Thread owner = lock.getOwner();
		    
		    if ( (owner != null) &&
			 ( owner.getId() == Thread.currentThread().getId())) {

			DBRecord record = recRef.get();

			if ( record == null ) {
			    /* Record has been garbage collected, recache it */
			    record = recacheRecord( recNo );
			}

			if ( record.isDeleted() ) {
			    String msg = getDeletedRecordMsg(record) +
				                    "It cannot be updated!";
			    logger.log(Level.INFO, msg );
			    RecordNotFoundException e = new RecordNotFoundException(msg);
			    logger.throwing("Data", "update()", e );
			    throw e;
			}
			
			record.setFieldsValues( data, dbMetaData );
			databaseFile.seek(offsetToRecZero + recNo * recordSize);
			record.writeToFile( databaseFile );
		    } else {
			String msg = "Attempt to update record number - "
			           + recNo + " failed, not owner of the lock "
			           + "on the record";
			logger.log(Level.WARNING, msg );
			DBAccessException e = new DBAccessException( msg );
			logger.throwing("Data", "update()", e );
			throw e;
		    }
		}
	    }	    
	} catch(IOException exc) {	    
	    logger.log(Level.SEVERE, "Database file error - " + exc );	    
	    DBAccessException e = new DBAccessException(exc.getMessage(), exc);
	    logger.throwing( "Data", "update()", e );
	    throw e;
	} finally {
	    writeLock.unlock();
	}
    }

    /**
     * This method logically deletes the record uniquely identified by the 
     * integer record number argument from the database. 
     *
     * @param  recNo An integer value that uniquely identifies the record to
     *         be deleted.
     *
     * @throws RecordNotFoundException is thrown if a record with the specified 
     *         record number does not exist or had been deleted.
     *
     * @throws DBAccessException is thrown if the record being deleted is not 
     *         locked. If the record is locked but the thread attempting to 
     *         deleted the record is not the owner of the lock. If an error is 
     *         encountered while accessing the physical database file. 
     */
    public void delete(int recNo) throws RecordNotFoundException,
                                         DBAccessException {
	
	writeLock.lock();

	try {
	    SoftReference<DBRecord> recRef = dbCache.get( recNo );

	    if ( recRef == null ) {		
		String msg = "Record number - " + recNo + " does not exist";
		logger.warning( msg );
		RecordNotFoundException e = new RecordNotFoundException(msg);
		logger.throwing("Data", "delete()", e );
		throw e;
	    } else {
		
		DBRecord record = recRef.get();
		
		if ( record == null ) {
		    record = recacheRecord( recNo );		    
		} 
		
		if ( record.isDeleted() ) {		    
		    String msg = getDeletedRecordMsg(record) +
			                    "It cannot be deleted again!!";
		    logger.info( msg );
		    RecordNotFoundException e = new RecordNotFoundException(msg);
		    logger.throwing("Data", "delete()", e );
		    throw e;	
		}
		
		DatabaseLock lock = lockCache.get( recNo );
		
		if ( lock == null ) {
		    String msg = "Record - " + recNo
			          + " is not locked, cannot be deleted";
		    logger.log(Level.WARNING, msg);
		    DBAccessException e = new DBAccessException(msg);
		    logger.throwing("Data", "delete()", e );
		    throw e;		    
		}

		Thread owner = lock.getOwner();
		
		if ( (owner != null) &&
			 ( owner.getId() == Thread.currentThread().getId())) {

		    record.delete();
		    databaseFile.seek( offsetToRecZero + recNo * recordSize );
		    record.writeToFile( databaseFile );
		} else {
		    String msg = "Attempt to delete record number - "
			           + recNo + " failed, not owner of the lock "
			           + "on the record";
			logger.log(Level.WARNING, msg );
			DBAccessException e = new DBAccessException( msg );
			logger.throwing("Data", "delete()", e );
			throw e;		    
		}
	    }
	} catch( IOException ex ) {
	    logger.log(Level.SEVERE, "Database file error - " + ex );	    
	    DBAccessException e = new DBAccessException( ex.getMessage(), ex );
	    logger.throwing( "Data", "delete()", e );
	    throw e;	    
	} finally {
	    writeLock.unlock();
	}
    }

    /**
     * This method does a search of the database for records matching the 
     * specified criteria. Following are the rules employed.
     *
     * <ul>
     * 
     *<li> For a record to be included in the search result, it must satisfy all 
     * the criteria specified.   
     *
     * <li> Each element of the input array of String objects is the criterion 
     * for the corresponding element in an array of record attributes. This means 
     * that element i in the criteria array is the criterion for element i in 
     * a record attributes array.
     *
     * <li> A null value or space in an element of the criteria array indicates
     * a wild card. It means all undeleted records will be selected regardless of 
     * what the value of the corresponding record attribute is. This suggests 
     * that if each of the elements of the search criteria equals null or white 
     * space or a zero length String, all undeleted records in the database will be 
     * returned.
     *
     * <li> For the rate field, the search will return all records with rate less
     * than or equal to the rate specified. 
     *
     * <li> For the size field, the search will return all records with size 
     * greater than or equal to the size specified.
     *
     * <li> For the name, location, and owner fields all records with a 
     * corresponding attribute that starts with the corresponding criterion will
     * be selected. For instance, the criterion "Andy" will match "Andy and Sons". 
     * 
     * <li> When the "name" and "location" values are specified in the criteria
     * array and each of the other criteria is equal to null or space or zero 
     * length String, the search is considered a "key" search. This means that 
     * only undeleted records that match both the "name" and "location" exactly 
     * will be returned.
     *
     * <li> When a "+" is placed in the owner criterion, only unbooked records 
     * will be included in the search result. When a "-" is placed in the owner
     * criterion, only booked records will be included in the search result.
     *
     *</ul>
     *
     * @param  criteria An array of String objects with each element indicating
     * the criterion for the corresponding attribute in an array of record 
     * attributes.
     *
     * @return An array of integers denoting the unique identifiers of the 
     *         records matching the criteria.
     *
     * @throws DBAccessException If an error is encountered while accessing 
     *         the physical database file.
     *
     * @throws RecordNotFoundException is thrown if no record matching the 
     *         criteria is found.
     *
     * @see DBRecord#matchesCriteria(String[])
     */
    public int [] find(String [] criteria) throws RecordNotFoundException,
                                                  DBAccessException {

	readLock.lock();
	
	try {
	    Set<Integer> recKeys = dbCache.keySet();
	    Set<Integer> result = new TreeSet<>();
	    
	    for( Integer key : recKeys ) {

		SoftReference<DBRecord> recRef = dbCache.get( key );
		DBRecord record = null;
			
		if ( (recRef == null) || ( (record = recRef.get()) == null) ) {
		    record = recacheRecord( key );		    
		}
				
		if ( ( ! record.isDeleted() ) &&
		     ( record.matchesCriteria(criteria) ) ) {

		    result.add( key );
		}		
	    }

	    if ( result.size() == 0 ) {
		
		String msg = "No record found for the specified criteria";
		logger.log(Level.INFO, msg);
		RecordNotFoundException e = new RecordNotFoundException(msg);
		logger.throwing("Data", "find()", e );
		throw e;
		
	    } else {
		int [] recNos = new int[ result.size() ];
		int idx = 0;
		for( Integer value : result) {
		    recNos[idx] = value;
		    ++idx;
		}

		return recNos;
	    }	    
	} catch( IOException ex ) {
	    logger.log(Level.SEVERE, "Database file error - " + ex );	    
	    DBAccessException e = new DBAccessException(ex.getMessage(), ex);
	    logger.throwing( "Data", "find()", e );
	    throw e;
	} finally {

	    readLock.unlock();
	}
    }

    /**
     * This method adds a new record to the database. 
     *
     * <p> If there is an inactive record with both name and location fields 
     * matching the new record, the record is reactivated and its attributes 
     * are replaced with the attributes of the new record. 
     *
     * <p> If there are no matching records, 
     *
     * @param data This is an array of String objects denoting the attributes 
     *        of the new record.
     * 
     * @return An integer value denoting the unique number of the created record.
     *
     * @throws DuplicateKeyException is thrown If there is an active record that 
     *         matches the new record in the both the name and location 
     *         attributes.
     *
     * @throws DBAccessException is thrown if an error was encountered while 
     *         accessing the physical database file.
     */
    public int create(String [] data) throws DuplicateKeyException,
                                             DBAccessException {

	writeLock.lock();
	
	try {	    
	    int recNo = recordNumber.getAndIncrement();

	    DBRecord newRecord = new DBRecord( data, dbMetaData, recNo );

	    Set<Integer> recKeys = dbCache.keySet();
	    boolean noDuplicate = true;
	    DBRecord record = null;
	    boolean foundDeleted = false;
	    
	    /* Search for a matching record in the cache */
	    for(Integer key : recKeys) {
		
		SoftReference<DBRecord> recRef = dbCache.get( key );
		
		if ( (recRef == null) || ( (record = recRef.get()) == null) ) {
		    record = recacheRecord( key );		    
		}
		
		if ( record.equals( newRecord ) ) {
		    noDuplicate = false;
		    recNo = key;
		    break;
		}

		if ( ! foundDeleted ) {		    
		    if ( record.isDeleted() ) {
			foundDeleted = true;
			recNo = key;
		    }		    
		}
	    }

	    if ( noDuplicate ) {
		if ( foundDeleted ) {
		    recordNumber.getAndDecrement();		    
		} 

		dbCache.put( recNo, new SoftReference<DBRecord>(newRecord) );
		databaseFile.seek( offsetToRecZero + recNo * recordSize );
		newRecord.writeToFile( databaseFile );
		logger.log( Level.INFO,
			    "Added new record to the database; values - "
			    + newRecord );
		return recNo;
		
	    } else {

		recordNumber.getAndDecrement();

		String msg = "The record already exists in the database; ";
		
		if ( record.isDeleted() ) {
		    
		    /* If duplicate record is deleted, undelete it */
		    record.undelete();
		    /* Set the values to the values for the new record */
		    record.setFieldsValues( data, dbMetaData );
		    /* Set the file pointer and Write it to the file */		    
		    databaseFile.seek(offsetToRecZero + recNo * recordSize);
		    record.writeFlagToFile( databaseFile );
		    logger.log(Level.INFO, msg + "undeleting record - "
			                       + record); 
		    return recNo;		    
		} else {
		    /* Duplicate record that is active */
		    logger.log(Level.WARNING, msg + "not adding record - "
			       + newRecord );
		    DuplicateKeyException e = new DuplicateKeyException( msg );
		    logger.throwing("Data", "create()", e );
		    throw e;
		}		
	    }
	} catch( IOException exc ) {		  
	    logger.log( Level.SEVERE, "Database file error", exc );
	    DBAccessException e = new DBAccessException(exc.getMessage(), exc);
	    logger.throwing( "Data", "create()", e );
	    throw e;	
	} finally {
	    writeLock.unlock();
	}	
    }

    /**
     * This method logically locks a record, a record must be locked before it 
     * can be updated or deleted. If the specified record is already locked the
     * thread making this call is blocked until the thread that currently owns 
     * the lock on the specified record releases it. 
     *
     * @param  recNo An integer value that uniquely identfies the record to be 
     *         locked.
     *
     * @throws RecordNotFoundException is thrown if a record with the specified 
     *         record number does not exist or had been deleted.
     *
     * @throws DBAccessException If an error is encountered while accessing the 
     *         physical database file. 
     */
    public void lock(int recNo) throws RecordNotFoundException,
                                       DBAccessException {

	writeLock.lock();
	
	DatabaseLock lock;
	
	try {	    	  	    
	    SoftReference<DBRecord> recRef = dbCache.get( recNo );
	    
	    if ( recRef == null ) {

		String msg = "Record number - " + recNo + " does not exist";
		logger.log(Level.WARNING, msg);
		RecordNotFoundException e = new RecordNotFoundException(msg);
		logger.throwing("Data", "lock()", e );
		throw e;
	    } 
	    
	    DBRecord record = recRef.get();
	    
	    if ( record == null ) {
		record = recacheRecord( recNo );
	    } 	
	    
	    if ( record.isDeleted() ) {		
		String msg = getDeletedRecordMsg(record) + "It cannot be locked";
		logger.log(Level.INFO, msg );
		RecordNotFoundException e = new RecordNotFoundException(msg);
		logger.throwing("Data", "lock()", e );
		throw e;		
	    } else {		    
		
		lock = lockCache.get( recNo );
		
		if ( lock == null ) {
		    lock = new DatabaseLock();
		    lockCache.put( recNo, lock );
		} 
	    }		
	    	   	      
	} catch(IOException exc) {
	    logger.log(Level.SEVERE, "Database file error - " + exc );	    
	    DBAccessException e = new DBAccessException(exc.getMessage(), exc);
	    logger.throwing( "Data", "lock()", e );
	    throw e;
	} finally {
	    writeLock.unlock();
	}

	lock.lock();		    
    }

    /**
     * This method logically unlocks a record.
     * 
     * @param recNo This is an integer value denoting the unique record number 
     * of the record to be unlocked.
     *
     * @throws RecordNotFoundException is thrown if a record with the specified 
     *         record number does not exist.
     * 
     * @throws DBAccessException is thrown if the specified record is not locked or
     * the thread requesting the unlock operation does not own the lock on the 
     * record. If there is a problem during the unlock operation or during access of 
     * the physical database file.
     */
    public void unlock(int recNo) throws RecordNotFoundException,
                                         DBAccessException {

	writeLock.lock();

	try {
	    SoftReference<DBRecord> recRef = dbCache.get(recNo);

	    if ( recRef == null ) {	
		String msg = "Record number - " + recNo + " does not exist";
		logger.log(Level.WARNING, msg);
		RecordNotFoundException e = new RecordNotFoundException(msg);
		logger.throwing("Data", "unlock()", e );
		throw e;
	    }

	    DBRecord record = recRef.get();

	    if ( record == null ) {
		record = recacheRecord( recNo );
	    }
	    
	    DatabaseLock lock = lockCache.get( recNo );
	    
	    if ( lock == null ) {
		String msg = "Record - " + recNo
		                + " is not locked, cannot be unlocked";
		logger.log(Level.WARNING, msg);
		DBAccessException e = new DBAccessException(msg);
		logger.throwing("Data", "unlock()", e );
		throw e;
		
	    } else {

		Thread owner = lock.getOwner();

		if ( ( owner != null) &&
		     ( owner.getId() == Thread.currentThread().getId() ) ) {

		    lock.unlock();
		    
		    if ( lock.getHoldCount() <= 0 ) {
			lockCache.remove( recNo );
		    }
		    
		} else {
		    String msg = "Attempt to unlock record number - "
			           + recNo + " failed, not owner of the lock "
			           + "or record is not locked";
		    logger.log(Level.WARNING, msg );
		    DBAccessException e = new DBAccessException( msg );
		    logger.throwing("Data", "unlock()", e );
		    throw e;
		}
	    }
	} catch(IOException exc) {	    
	    logger.log(Level.SEVERE, "Database file error - " + exc );	    
	    DBAccessException e = new DBAccessException(exc.getMessage(), exc);
	    logger.throwing( "Data", "unlock()", e );
	    throw e;

	} catch(IllegalMonitorStateException exc ) {
	    logger.log(Level.SEVERE, "System error - " + exc );	    
	    DBAccessException e = new DBAccessException(exc.getMessage(), exc);
	    logger.throwing( "Data", "unlock()", e );
	    throw e;	    
	} finally {
	    writeLock.unlock();
	}	
    }

    /**
     * This method indicates whether a record is locked or not.
     *
     * @param recNo An integer denoting the unique number of the record.
     *
     * @return A boolean true if the record is locked and false otherwise.
     *
     * @throws RecordNotFoundException is thrown if a record with the specified 
     *         record number does not exist or had been deleted.
     *
     * @throws DBAccessException If an error was encountered while accessing the 
     *         physical database file.
     */
    public boolean isLocked(int recNo) throws RecordNotFoundException,
                                              DBAccessException {
	
	readLock.lock();
	
	try {

	    SoftReference<DBRecord> recRef = dbCache.get(recNo);

	    if ( recRef == null ) {

		String msg = "Record number - " + recNo + " does not exist";
		logger.log(Level.WARNING, msg);
		RecordNotFoundException e = new RecordNotFoundException(msg);
		logger.throwing("Data", "isLocked()", e );
		throw e;
	    }

	    DBRecord record = recRef.get();

	    if ( record == null ) {
		readLock.unlock();
		record = recacheRecord( recNo );
		readLock.lock();
	    }

	    if ( record.isDeleted() ) {
		String msg = getDeletedRecordMsg(record); 
		logger.log(Level.WARNING, msg);
		RecordNotFoundException e = new RecordNotFoundException(msg);
		logger.throwing("Data", "isLocked()", e );
		throw e;
	    }

	    DatabaseLock lock = lockCache.get( recNo );

	    if ( lock == null ) {
		return false;
	    } else {
		return ( lock.getHoldCount() > 0 );
	    }
	    
	} catch( IOException exc) {
	    logger.log( Level.SEVERE, "Database file error", exc );
	    DBAccessException e = new DBAccessException(exc.getMessage(), exc);
	    logger.throwing( "Data", "isLocked()", e );
	    throw e;	    
	} finally {
	    
	    readLock.unlock();
	}
    }
    
    /**
     * This is the close method. It fulfills the AutoCloseable interface 
     * implementation. 
     */
    public void close() {
	
	writeLock.lock();	
	try {
	    logger.info( "Data.close() Closing the application database file!!" );
	    databaseFile.close();
	} catch( IOException ex ) {
	    logger.log( Level.SEVERE,
			"Caught an exception while closing the database file - "
			+ ex.getMessage() );			
	} finally {	    
	    writeLock.unlock();
	}
    }
}
