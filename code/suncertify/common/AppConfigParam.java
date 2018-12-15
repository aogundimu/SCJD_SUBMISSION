/*
 * AppConfigParam.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.common;

/**
 * This AppConfigParam interface defines the values used as keys in requesting
 * application configuration parameters from the AppConfigManager.
 *
 * @see suncertify.common.AppConfigManager
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public interface AppConfigParam {
    
    /** 
     * The application configuration file name.
     */
    String APP_CONFIG_FILENAME = "suncertify.properties";

    /**
     * The key for the application client GUI title string.
     */
    String CLIENT_GUI_TITLE = "CLIENT_GUI_TITLE";

    /**
     * The key for the application GUI look and feel setting.
     */
    String GUI_LOOK_AND_FEEL = "GUI_LOOK_AND_FEEL";

    /**
     * The key for the application server GUI title text.
     */
    String SERVER_GUI_TITLE = "SERVER_GUI_TITLE";
    
    /** 
     * The key for the server name string.
     */
    String SERVER_NAME = "SERVER_NAME";
    
    /** 
     * The key for the application server host name string.
     */
    String SERVER_HOST_NAME = "SERVER_HOST_NAME";
    
    /** 
     * The key for the application server listening port number. 
     */
    String SERVER_PORT_NUMBER = "SERVER_PORT_NUMBER";
    
    /** 
     * The key for the application database file name.
     */
    String DB_FILE_NAME = "DB_FILE_NAME";

    /** 
     * The key for the database component logger name.
     */
    String DB_LOGGER_NAME = "DB_LOGGER_NAME";
    
    /** 
     * The key for the server component logger name.
     */
    String SERVER_LOGGER_NAME = "SERVER_LOGGER_NAME";

    /** 
     * The key for the client component logger name.
     */
    String CLIENT_LOGGER_NAME = "CLIENT_LOGGER_NAME";

    /**
     * The key for the common component logger name.
     */
    String COMMON_LOGGER_NAME = "COMMON_LOGGER_NAME";

    /**
     * The key for the application log file name.
     */  
    String LOG_FILE_NAME = "LOG_FILE_NAME";
    
    /**
     * The key for the "stand alone" command line paramter value.
     */
    String STAND_ALONE = "STAND_ALONE";

    /**
     * The key for the "server only" command line parameter value.
     */
    String SERVER_ONLY = "SERVER_ONLY";

    /**
     * The key for the "server type" parameter value.
     */
    String SERVER_TYPE = "SERVER_TYPE";

    /**
     * The key for the application supported specialities string.
     */
    String SPECIALITIES = "SPECIALITIES";

    /**
     *
     */
    String RMI_RESPONSE_TIME_OUT = "RMI_RESPONSE_TIME_OUT";
}
