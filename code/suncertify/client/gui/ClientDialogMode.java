/*
 * ClientDialogMode.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client.gui;

/**
 * Client dialog modes. The modes determines the behavior of the dialog.
 */
public enum ClientDialogMode {

    /**
     * Addition of a new record. All the fields are modifiable in this mode, 
     * except the "Owner" field.
     */
    ADD,

    /**
     * Deletion of an existing record. None of the fields are modifiable in 
     * this mode.
     */
    DELETE,
    
    /**
     * Booking a record. Only the "Current Owner" field is modifiable in this
     * mode.
     */
    BOOK,

    /**
     * Releasing a record. Only the "Current Owner field is modifiable in this
     * mode.
     */
    RELEASE,

    /**
     * Record Search. All the fields are modifiable in this mode.
     */
    SEARCH,

    /**
     * Update a record. Only the "Specialities", "Size" and "Rate" fields are 
     * modifiable in this mode.
     */
    UPDATE
}
