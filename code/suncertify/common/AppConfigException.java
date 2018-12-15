/*
 * AppConfigException.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.common;

/**
 * The AppConfigException is the exception used in reporting configuration
 * issues encountered in the application. 
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class AppConfigException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 20120811115400L;
    
    /**
     * Default contructor
     */
    public AppConfigException() {
    }

    /**
     * Constructor that takes a String object denoting the reason for the 
     * exception.
     *
     * @param msg - the reason for the exception. This is the String returned
     *        when a call to getMessage() is made.
     */
    public AppConfigException(String msg) {
	super(msg);
    }

    /**
     * Constructor that takes both a reason and a Throwable which might be 
     * the original exception caught leading to the creation of this exception.
     *
     * @param msg The reason for the exception.
     * @param t   A reference to a Throwable object that might be the reason why
     *            this exception is thrown.
     */
    public AppConfigException(String msg, Throwable t) {
	super(msg, t);
    }
} 
