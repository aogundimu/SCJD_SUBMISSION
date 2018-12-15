/*
 * ClientConfigDialog.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client.gui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.LayoutManager;
import java.awt.Color;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import suncertify.common.AppRunMode;
import suncertify.common.gui.AppConfigOptionsPanel;

/**
 * The ClientConfigDialog class displays the client configuration dialog during 
 * the application client startup process.
 *
 * <p> The ClientConfigDialog has a producer/consumer relationship with the 
 * the BrokerClient class and this relationship is established during client
 * startup time. The BrokerClient creates an instance of this class and waits 
 * until it gets notification from it to proceed with it's startup process. 
 * 
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class ClientConfigDialog extends JDialog {   

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * A reference to a JButton object serving as the "Exit" button. Activation
     * of this button will result in the termination of the client startup 
     * process.
     */
    JButton exitButton;

    /**
     * This is the text that serves as the label for the "Exit" button.
     */
    private final static String EXIT_BUTTON_LABEL = "Exit";

    /**
     * This is the text that is displayed when the "Exit" button has mouse 
     * focus.
     */
    private final static String EXIT_BUTTON_TIP =
	                      "This will terminate the application";
    /**
     * A reference to a JButton object serving as the "Okay" button. Activation
     * of this button will result in the startup of the client with the values 
     * shown in the dialog.
     */
    JButton okayButton;

    /**
     * This is the text serving as the label for the "Okay" button.
     */
    private final static String OKAY_BUTTON_LABEL = "Okay";

    /**
     * This is the text that is displayed when the "Okay" button has mouse 
     * focus.
     */
    private final static String OKAY_BUTTON_TIP =
	      "This will run the application with the entered parameters";

    /**
     * This is the text of the configuration dialog window title.
     */
    private final static String CONFIG_DIALOG_LABEL =
	                      "Application Client Configuration";

    /**
     * A reference to a ReentrantLock object used for synchronization between
     * this dialog and its client.
     * 
     * @see java.util.concurrent.locks.ReentrantLock
     */
    ReentrantLock dialogLock;
    
    /**
     * A reference to a Condition object used for synchronization between this
     * dialog and its client.
     * 
     * @see java.util.concurrent.locks.Condition
     */
    Condition dialogCondition;

    /**
     * This ClientConfigAction class implements the ActionListener interface.
     */
    private class ClientConfigAction implements ActionListener {

	/**
	 * This method implements the action for this dialog. The action depends 
	 * on the origin of the event.
	 * <ul>
	 * <li> When the origin of the event is the "Exit Button", it terminates 
	 * the entire application.
	 * <li> When the origin of the event is the "Okay Button", it disposes
	 *      the dialog window and notifies the creator of the dialog to 
	 *      proceed.
	 *</ul>
	 *
	 * @param event A reference to an ActionEvent object providing the event 
	 *              data.
	 *
	 * @see java.awt.event.ActionEvent
	 */
	public void actionPerformed( ActionEvent event ) {
	    
	    if ( event.getSource() == okayButton ) {
		setVisible( false );
		dispose();
		try {
		    dialogLock.lock();
		    dialogCondition.signal();
		    logger.info("The user has entered the configuration " +
				"the client startup can proceed");
		} finally {
		    dialogLock.unlock();
		}
		
	    } else if ( event.getSource() == exitButton ) {
		logger.info("The user chose to exit the application! Exiting!");
		System.exit(0);
	    }
	}

    }

    /**
     * The constructor. All the components constituting this dialog are created
     * here, this includes the AppConfigOptionsPanel and the button panel.
     *
     * @param mode The application run mode
     * 
     * @param lock A reference to a ReentrantLock object
     *
     * @param condition A reference to a Condition object
     *
     * @see suncertify.common.gui.AppConfigOptionsPanel
     * @see suncertify.common.AppRunMode
     * @see suncertify.client.BrokerClient#getClientConfigParams(AppRunMode)
     *
     * @see java.util.concurrent.locks.ReentrantLock
     * @see java.util.concurrent.locks.Condition
     */
    public ClientConfigDialog(AppRunMode mode, ReentrantLock lock,
			                        Condition condition) {
	dialogLock = lock;
	dialogCondition = condition;

	setTitle( CONFIG_DIALOG_LABEL );

	AppConfigOptionsPanel acop = new AppConfigOptionsPanel( mode );

	add( acop, BorderLayout.CENTER );
	
	JPanel buttonPanel = new JPanel();

	okayButton = new JButton(OKAY_BUTTON_LABEL);
	okayButton.setToolTipText( OKAY_BUTTON_TIP );
	okayButton.setOpaque( true );
	okayButton.setBackground( Color.GREEN );
	okayButton.addActionListener( new ClientConfigAction() );
	buttonPanel.add( okayButton );
	
	exitButton = new JButton( EXIT_BUTTON_LABEL);
	exitButton.setToolTipText( EXIT_BUTTON_TIP );
	exitButton.setOpaque( true );
	exitButton.setBackground( Color.RED );
	exitButton.addActionListener( new ClientConfigAction() );
	buttonPanel.add( exitButton );
	buttonPanel.setBorder(
		  BorderFactory.createEtchedBorder(EtchedBorder.RAISED)  );

	add( buttonPanel, BorderLayout.SOUTH );

	LayoutManager layout = this.getLayout();
	((BorderLayout)layout).setHgap( 50 );
	((BorderLayout)layout).setVgap( 50 );
	pack();
	
	setLocationRelativeTo( null );
	setResizable( false );

	setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	setVisible( true );
    }
}
