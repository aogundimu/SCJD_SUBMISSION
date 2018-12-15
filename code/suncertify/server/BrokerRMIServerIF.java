/*
 * BrokerRMIServerIF.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.server;

import java.rmi.Remote;

/**
 * The BrokerRMIServerIF interface defines the interface that the RMI server
 * must implement in order to satisfy the RMI remote object requirement. It 
 * extends the BrokerServerIF. 
 *
 * @see BrokerServerIF
 * @see BrokerRMIServer 
 * @see java.rmi.Remote
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public interface BrokerRMIServerIF extends BrokerServerIF, Remote {

}
