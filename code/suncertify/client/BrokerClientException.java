/*
 * BrokerClientException.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */
package suncertify.client;

/**
 * The <code>BrokerClientException</code> is the exception thrown in the
 * application client components.
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerClientException extends Exception {

    /**
     * The default constructor
     */
    public BrokerClientException() {

    }
    
    /**
     * Constructor that takes a reference to a String object denoting the
     * message text.
     *
     * @param msg text denoting the reason for the exception.
     */
    public BrokerClientException(String msg) {
	super(msg);
    }

    /**
     * Constructor that takes both a text message and a reference to another
     * Throwable object.
     *
     * @param msg - text denoting the reason for the exception
     * @param t - Reference to a <code>Throwable</code> denoting an original
     * exception that resulting in the creation of this exception
     */
    public BrokerClientException(String msg, Throwable t ) {
	super(msg,t);
    }
}
