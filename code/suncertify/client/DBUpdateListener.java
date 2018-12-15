/*
 * DBUpdateListener.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The DBUpdateListener interface is implemented by any class that wants to be 
 * notified of updates from the the database server. An object that implements
 * this interface and registers for DB update with the database server will be 
 * notified when -
 * <ul>
 * <li> A new record is added to the database.
 * <li> A record is deleted from the database.
 * <li> The attributes of a record are modified.
 * <li> A previously booked record is released.
 * <li> A record is booked.
 * </ul>
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public interface DBUpdateListener extends Remote {

    /**
     * Callback method for database updates from the database server.
     *
     * @throws RemoteException - when there is a communication problem during
     *         the call to this method.
     */
    public void notifyUpdate() throws RemoteException ;
}
