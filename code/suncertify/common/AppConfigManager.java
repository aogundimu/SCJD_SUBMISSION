/*
 * AppConfigManager.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Properties;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The AppConfigManager class is a singleton that serves as the sole manager
 * of all configuration parameters. It is a wrapper for a Properties object.
 *
 * @see java.util.Properties
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class AppConfigManager {

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * Reference to only instance of this class. Access to this is through a call
     * to the getInstance() method.
     *
     * @see #getInstance
     */
    private static AppConfigManager instance = null;


    /**
     *  A reference to the Properties object.
     *
     * @see java.util.Properties 
     */
    private Properties properties = null;


    /**
     * A private default constructor to ensure that the only instance of this 
     * class is accessed through the getInstance() method.
     *
     * @see #getInstance
     *
     *  @throws AppConfigException is thrown if an error is encountered while
     *          accessing the properties file.
     */
    private AppConfigManager() throws AppConfigException {

	properties = new Properties();

	try {
	    FileInputStream f =
		new FileInputStream(AppConfigParam.APP_CONFIG_FILENAME ); 
	   	
	    properties.load( f );

	    f.close();
	    
	} catch( IOException ex ) {
	    
	    logger.log( Level.SEVERE,
			"Exception while creating configuration manager - " +
			ex.getMessage() );
	    
	    AppConfigException e =
		new AppConfigException(
			       "Exception creating Configuration manager" +
			       ex.getMessage(),
			       ex );
	    
	    logger.throwing("AppConfigManager","AppConfigManager", e);

	    throw e;
	}
    }

    /**
     * This method provides access to the single instance of this class. 
     *
     * @return A reference to the single instance of this class. 
     *
     * @throws AppConfigException If an error is encountered during the 
     *          construction of the instance.
     */
    public static AppConfigManager getInstance() throws AppConfigException {

        if ( instance == null ) {

           instance = new AppConfigManager();
        }

        return instance;
    }

    /**
     * This method returns A String denoting the configuration parameter with 
     * the specified key.
     *
     *  @param key The key associated with the configuration parameter.
     *
     *  @return A String denoting the value of the parameter or null if a 
     *          configuration parameter with the key was not found.                
     */
    public synchronized String get( String key ) {
	
        return properties.getProperty( key );
    }

    /**
     * This method sets the configuration parameter associated with the 
     * specified key to the provided value
     *  
     *  @param key The key associated with the configuration parameter.
     *
     *  @param value The new value for the configuration parameter.
     *
     *  @throws AppConfigException If there was an error accessing the 
     *          the configuration properties file.
     */
    public synchronized void set( String key, String value ) throws
	                                             AppConfigException {

        Object o = properties.setProperty( key, value );

	try {
	    FileOutputStream file =
		new FileOutputStream( AppConfigParam.APP_CONFIG_FILENAME );
	
	    String oldValue = properties.getProperty(key);

	    
	    String cmt = "Property " + key
		+ " was changed from " + oldValue + " to " + value;

	    properties.store( file, cmt );

	    logger.info( cmt );
	    
	    file.close();
	    
	} catch( IOException ex ) {

	    logger.log( Level.SEVERE,
			"Exception caught in configuration manager - " +
			ex.getMessage() );
	    
	    AppConfigException e =
		new AppConfigException(
			  "Exception setting a value Configuration manager" +
			       ex.getMessage(),
				       ex );
	    
	    logger.throwing("AppConfigManager","set(String,String)", e);

	    throw e;
	}
    }
}
