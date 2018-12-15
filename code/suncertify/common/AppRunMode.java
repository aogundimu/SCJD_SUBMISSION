/*
 * AppRunMode.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.common;

/**
 * Application run modes. The values defined are correlated with the command 
 * line arguments.
 * 
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public enum AppRunMode {

    /**
     * The client and server run in the same JVM. This is the mode when the 
     * argument on the command line is "alone".
     */
    STAND_ALONE,

    /**
     * Only the remote server is started. This is the mode when the argument 
     * on the command line is "server".
     */
    SERVER,

    /**
     * Only the remote client is started. This is the mode when no argument is 
     * supplied on the command line.
     */
    NETWORK_CLIENT
}
