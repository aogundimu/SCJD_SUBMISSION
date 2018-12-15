/*
 * InvalidRecordStateException.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.server;

/**
 * The InvalidRecordStateException is the exception thrown in the application 
 * server to indicate when the state of a record is not suitable for the action
 * requested by the client. An example is when the record is currently locked by
 * another thread.
 *
 * @author Augustine Ogundimu
 * @version 1.0 
 * @since 1.0
 */
public class InvalidRecordStateException extends Exception  {

    /**
     *
     */
    private static final long serialVersionUID = 20129991125400L;
    
    /**
     * Default constructor.
     */
    public InvalidRecordStateException() {

    }

    /**
     * The reason constructor.
     *
     * @param message - The reason for the exception.
     */
    public InvalidRecordStateException(String message) {
	super(message);
    }
}
