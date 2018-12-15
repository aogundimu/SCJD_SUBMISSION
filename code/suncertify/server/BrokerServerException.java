/*
 * BrokerServerException.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.server;

/**
 * The BrokerServerException class is used in the application server for 
 * reporting server related errors.
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerServerException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 20166661125400L;
    
    /**
     * The default constructor.
     */
    public BrokerServerException() {

    }

    /**
     * The reason argument constructor.
     *
     * @param msg - The reason for the exception.
     */
    public BrokerServerException(String msg) {
	super(msg);
    }

    /**
     * The reason argument and Throwable constructor.
     *
     * @param msg - The reason for the exception.
     *
     * @param t - A reference to a Throwable object that might have been the 
     *        reason why the exception was thrown.
     */
    public BrokerServerException(String msg, Throwable t) {
	super(msg, t);
    }					    
}
