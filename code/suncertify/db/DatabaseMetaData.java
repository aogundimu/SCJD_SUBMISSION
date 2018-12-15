/*
 * DatabaseMetaData.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.db;

/**
 * The <code>DatabaseMetaData</code> interface defines the variables for indexing 
 * an array of Strings denoting a single record representing a contractor in the 
 * application database.  
 * 
 * <p> It also defines some values providing the scope for the validation and 
 * verification of some fields of a contractor record.
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public interface DatabaseMetaData {

    /**
     * This is the index for accessing the NAME attribute from an array of String 
     * objects denoting a contractor record.
     *
     * <p>This NAME attribute represents the official name of the contractor 
     * denoted in a record.
     */
    int NAME_IDX = 0;

    /**
     * This is the minimum width of the name display field in the application 
     * main GUI.
     */
    int MIN_NAME_FIELD_SIZE = 100;

    /**
     * This is the maximum width of the name display field in the application 
     * main GUI.
     */
    int MAX_NAME_FIELD_SIZE = 175;

    /**
     * This is the index for accessing the LOCATION attribute from an array of 
     * String objects denoting a contractor record.
     *
     * <p>This LOCATION attribute represents the locale of the contractor denoted 
     * by a record.
     */
    int LOCATION_IDX = 1;

    /**
     *  This is the minimum width of the location display field in the 
     *  application main GUI.
     */
    int MIN_LOCATION_FIELD_SIZE = 100;

    /**
     * This is the maximum width of the location display field in the application 
     *  main GUI.
     */
    int MAX_LOCATION_FIELD_SIZE = 350;

    /**
     * This is the index for accessing the SPECIALITIES attribute from an array 
     * of String objects denoting a contractor record.
     *
     * <p>The SPECIALITIES attribute represents areas of specialities of the 
     * contractor denoted by a record.
     */
    int SPECIALITIES_IDX = 2;

    /**
     * This is the minimum width of the specialities display field in the 
     * application  main GUI.
     */
    int MIN_SPECIALITIES_FIELD_SIZE = 150;

    /**
     * This is the maximum width of the specialities display field in the 
     * application  main GUI.
     */
    int MAX_SPECIALITIES_FIELD_SIZE = 350;

    /**
     * This is the index for accessing the SIZE attribute from an array of String 
     * objects denoting a contractor record.
     *
     * <p>The SIZE attribute represents the personnel size of the contractor 
     * denoted by a record.
     */
    int SIZE_IDX = 3;

    /**
     * This is the minimum width of the size display field in the 
     * application  main GUI.
     */
    int MIN_SIZE_FIELD_SIZE = 80;

    /**
     * This is the maximum width of the size display field in the 
     * application  main GUI.
     */
    int MAX_SIZE_FIELD_SIZE = 100;
    
    /**
     * This is the index for accessing the RATE attribute from an array of String 
     * objects denoting a contractor record.
     *
     * <p>The RATE attribute denotings the hourly rate charged by the contrator 
     * denoted by a record.
     */
    int RATE_IDX = 4;

    /**
     * This is the minimum width of the rate display field in the application 
     * main GUI.
     */
    int MIN_RATE_FIELD_SIZE = 80;

    /**
     * This is the maximum width of the rate display field in the application
     * main GUI.
     */
    int MAX_RATE_FIELD_SIZE = 100;

    /**
     * This is the index for accessing the OWNER attribute from an array of String 
     * objects denoting a contractor record.
     *
     * <p>The OWNER attribute is the 8 digit numerical value denoting the identification
     * number of the customer currently employing the contractor represented by a 
     * record. This attribute is blank when the contractor is not currently employed 
     * by any customer. 
     */
    int OWNER_IDX = 5;

    /**
     * This is the minimum width of the owner display field in the application 
     * main GUI.
     */
    int MIN_OWNER_FIELD_SIZE = 80;

    /**
     * This is the maximum width of the owner display field in the application 
     * main GUI.
     */
    int MAX_OWNER_FIELD_SIZE = 100;

    /**
     * This is the maximum number of digits allowed in the size field of the 
     * application main GUI. 
     */
    int MAX_SIZE_FIELD_LEN = 4;

    /**
     * This is the minimum number of digist allowed in the size field of the 
     * application main GUI.
     */
    int MIN_SIZE_FIELD_LEN = 1;

    /**
     * This is the minimum value allowed in the size field entry in the 
     * application main GUI.
     */
    int MIN_SIZE_VAL = 1;

    /**
     * This is the maximum value of allowed in the size field entry in the
     * application main GUI.
     */
    int MAX_SIZE_VAL = 9999;

    /**
     * This the maximum number of characters allowed in the rate field entry
     * in the application main GUI.
     */
    int MAX_RATE_FIELD_LEN = 6;

    /**
     * This is the minimum value allowed in the rate field entry in the
     * application main GUI.
     */
    float MIN_RATE_VAL = 10.00F;

    /**
     * This is the maximum value allowed in the rate field entry in the
     * application main GUI.
     */
    float MAX_RATE_VAL = 999.99F;
}
