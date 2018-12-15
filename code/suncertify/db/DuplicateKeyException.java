/*
 * DuplicateKeyException.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.db;

/**
 * The DuplicateKeyException class is thrown to indicate an attempt to add
 * a record with a duplicate key to the application database. 
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class DuplicateKeyException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 20198761125400L;

    /**
     * The no-argument constructor.
     */
    public DuplicateKeyException() {

    }

    /**
     * Reason parameter constructor.
     *
     * @param reason The reason for the exception.
     */
    public DuplicateKeyException(String reason) {	
	super( reason );
    }
}
