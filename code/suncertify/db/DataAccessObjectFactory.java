/*
 * DataAccessObjectFactory.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */
package suncertify.db;

/**
 * The DataAccessObjectFactory class is the factory for creating the data access 
 * object in this application.
 * 
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public abstract class DataAccessObjectFactory {

    /**
     * Method for obtaining the reference to the data access object.
     * 
     * @return A reference to a DBMain object.
     *
     * @throws DBAccessException is thrown if an error is encountered while 
     *         creating the data access object. 
     */    
    static public DBMain createDataAccessObject() throws DBAccessException { 
	
	return Data.getInstance();
    }
}
