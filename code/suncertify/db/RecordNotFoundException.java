/*
 * RecordNotFoundException.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.db;

/**
 * The RecordNotFoundException to indicate database search and read attempts
 * that yield zero results.
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class RecordNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 20134531125400L;

    /**
     * The no-argument constructor.
     */
    public RecordNotFoundException() {

    }

    /**
     * Reason parameter constructor.
     *
     * @param reason The reason for the exception.
     */
    public RecordNotFoundException(String reason) {
	super( reason );
    }
}
