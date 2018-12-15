/*
 * DBAccessException.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.db;

/**
 * The DBAccessException class is the runtime exception thrown when system
 * related or database operational mode violation issues are encountered in the 
 * the data access object. 
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class DBAccessException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 20120881125400L;
    
    /**
     * The no argument constructor.
     */
    public DBAccessException() {

    }
    
    /**
     * A constructor that take a reason text.
     * 
     * @param msg The reason for the exception
     */
    public DBAccessException(String msg) {
	super( msg );
    }

    /**
     * A constructor that takes both a reason text and a reference to a 
     * Throwable.
     *
     * @param msg The reason for the exception
     *
     * @param t A lower level exception that is the reason for the exception.
     */
    public DBAccessException(String msg, Throwable t) {
	super(msg, t);
    }
}
