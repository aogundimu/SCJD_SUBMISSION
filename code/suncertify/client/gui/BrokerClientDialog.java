/*
 * BrokerClientDialog.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.LayoutManager;
import java.awt.Color;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.server.ContractorRecord;


/**
 * The BrokerClientDialog class is the modal dialog employed by the main 
 * application GUI for communicating with the user for all the operations 
 * allowed in the application. It is a subclass of JDialog.
 *
 * @see suncertify.client.gui.BrokerAppClientGUI
 * @see javax.swing.JDialog
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerClientDialog extends JDialog {

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * The text displayed as the title of the "Add Record" dialog window.
     */
    private final static String NEW_RECORD_TITLE = "Add A New Contractor";
    
    /**
     * The text displayed as the title of the "Add Record" confirmation 
     * dialog window.
     */
    private final static String ADD_CONF_DIALOG_TITLE = "Add A Record";

    /**
     * The text displayed as the "Add Record" cofirmation question.
     */
    private final static String ADD_CONF_QUESTION =
	                     "Are you sure you want to add this record?";

    /**
     * The text displayed as the "Add Record" operation dismissal message.
     */
    private final static String ADD_DISMISS_MSG = "The record was not added.";

    /**
     * The text displayed when the "action button" gains mouse focus and 
     * the current operation is "Add Record".
     */
    private final static String ADD_RECORD_TIP =
	                      "Click to add the new contrator record";

    /**
     * This is the label of the "action button" when the current operation is 
     * "Add Record".
     */
    private final static String ADD_RECORD_LABEL = " Add  ";

    
    /**
     * The text displayed as the "Update Record" dialog window title.
     */
    private final static String UPDATE_RECORD_TITLE =
	                               "Update Contractor's Record";
    
    /**
     * This is the text of the "action button" label when the current operation
     * is "Update Record".
     */
    private final static String UPDATE_RECORD_LABEL = "Update";

    /**
     * This text displayed when the "action button" gains mouse focus and the 
     * current operation is "Update Record".
     */
    private final static String UPDATE_RECORD_TIP =
	                     "Click to update the contractor's record";

    /**
     * This is the text displayed as the "Update Record" confirmation question.
     */
    private final static String UPDATE_CONF_QUESTION =
           "Are you sure you want to update this record with these values?";

    /**
     * This is the text displayed as the "Update Record" dismissal message.
     */
    private final static String UPDATE_DISMISS_MSG =
	                        "The record was not updated.";

    /**
     * This is the text displayed in the "Update Record" confirmation dialog 
     * window.
     */
    private final static String UPDATE_CONF_DIALOG_TITLE = "Update Record";


    /**
     * The text that servers as the title of the "Delete Record" dialog window.
     */
    private final static String DELETE_RECORD_TITLE =
	                         "Delete Contractor's Record";

    /**
     * The text that serves as the "Delete Record" operation dismissal message.
     */
    private final static String DELETE_DISMISS_MSG =
	                         "The record was not deleted";

    /**
     * The text that serves as the "Delete Record" confirmation question.
     */
    private final static String DELETE_CONF_QUESTION =
                 	"Are you sure you want to delete this record?";

    /**
     * The text that serves as the title of the "Delete Record" confirmation 
     * dialog window.
     */
    private final static String DELETE_CONF_DIALOG_TITLE = "Delete A Record";

    /**
     * The text displayed when the "action button" gains mouse focus and 
     * the current operation is "Delete Record".
     */
    private final static String DELETE_RECORD_TIP =
	                      "Click to delete the displayed contractor";

    /**
     * This is the label of the "action button" when the current operation is 
     * "Delete Record".
     */
    private final static String DELETE_RECORD_LABEL = "Delete";
 
    /**
     * This is the text for the "Search" dialog window title.
     */
    private final static String SEARCH_TITLE = "Search For Contractors";

    /**
     * This is the text for the "Search" operation confirmation dialog window
     * title.
     */
    private final static String SEARCH_CONF_DIALOG_TITLE = "Search For Records";    

    /**
     * This is the text for the "Search" operation cofirmation dialog window 
     * question.
     */
    private final static String SEARCH_CONF_QUESTION =
	                   "Are you sure you want to search with these values?";  

    /**
     * This is the text displayed in the "Search" operation dismissal dialog 
     * window.
     */
    private final static String SEARCH_DISMISS_MSG = "The search was canceled";

    /**
     * This is the label of the "action button" when the current operation is 
     * "Search".
     */
    private final static String SEARCH_LABEL = "Search";

    /**
     * The text displayed when the "action button" gains mouse focus and 
     * the current operation is "Search".
     */
    private final static String SEARCH_TIP =
	  "Click to search the database based on values entered";

    /**
     * This is the text of the "Book" operation dialog window title.
     */
    private final static String BOOK_RECORD_TITLE = "Book The Contractor";

    /**
     * This is the text of the "action button" label when the operation is 
     * "Book".
     */
    private final static String BOOK_RECORD_LABEL = "Book";

    /**
     * This is the text of the "Book" operation confirmation dialog window 
     * title.
     */
    private final static String BOOK_CONF_DIALOG_TITLE = "Book A Record";

    /**
     * This is the text of the "Book" operation confirmation dialog question.
     */
    private final static String BOOK_CONF_QUESTION =
	                         "Are you sure you want to book this record?";
    
    /**
     * This is the text displayed in the "Book" operation dismissal dialog 
     * window.
     */
    private final static String BOOK_DISMISS_MSG = "The record was not booked.";

    /**
     * The text displayed when the "action button" gains mouse focus and 
     * the current operation is "Book".
     */
    private final static String BOOK_RECORD_TIP =
	                       "Click to book the contractor";

    /**
     * This is the text of the "Release" operation dialog window title.
     */
    private final static String RELEASE_RECORD_TITLE = "Release The Contractor";

    /** 
     * This is the text of the "action button" label when the operation is 
     * "Release".
     */
    private final static String RELEASE_RECORD_LABEL = "Release";

    /**
     * This is the text of the "Release" operation confirmation dialog window 
     * title.
     */
    private final static String RELEASE_CONF_DIALOG_TITLE = "Release Record";

    /**
     * This is the text of the "Release" operation confirmation dialog question.
     */
    private final static String RELEASE_CONF_QUESTION =
	                      "Are you sure you want to release this record?";

    /**
     * This is the text displayed in the "Release" operation dismissal dialog 
     * window.
     */
    private static final String RELEASE_DISMISS_MSG =
	                                 "The record was not released.";
    /**
     * The text displayed when the "action button" gains mouse focus and 
     * the current operation is "Release".
     */
    private final static String RELEASE_RECORD_TIP =
	                       "Click to release the contractor";

    /**
     * This is the "action button" for all the different operations supported
     * by this dialog. The action and the text label of the button are determined
     * during the construction of this dialog window.
     * 
     * @see #BrokerClientDialog(ClientDialogMode, JFrame, ContractorRecord)
     * @see #addButtonPanel(ClientDialogMode)
     */
    JButton actionButton;

    /**
     * This is the "cancel button" for terminating any of the operations 
     * supported by this dialog window. 
     *
     * @see #BrokerClientDialog(ClientDialogMode, JFrame, ContractorRecord)
     * @see #addButtonPanel(ClientDialogMode)
     */
    private JButton cancelButton;

    /**
     * This is the text of the label of the "cancel button".
     */
    private final static String CANCEL_BUTTON_LABEL = "Cancel";

    /**
     * This is the text displayed when the "cancel button" has mouse 
     * focus.
     */
    private final static String CANCEL_BUTTON_TIP =
	                              "Cancel the requested operation";

    /**
     * This is a reference to a ContractorRecordPanel object that is used to 
     * display the contractor record in this dialog. It is created as part of 
     * the construction process for this dialog.
     *
     * @see #BrokerClientDialog(ClientDialogMode, JFrame, ContractorRecord)
     * @see suncertify.client.gui.ContractorRecordPanel
     */
    private ContractorRecordPanel dataEntryPanel;

    /**
     * This is the reference to an array of String objects denoting the record
     * that is the subject of this dialog. This array is created as a backup copy
     * of the String array passed into the constructor of this dialog.
     *
     * @see #BrokerClientDialog(ClientDialogMode, JFrame, ContractorRecord)
     */
    //private String [] oldRec = null;
    private ContractorRecord oldRecord = null;
  
    /**
     * This is the reference to an array of String objects denoting the 
     * attributes of the record that is the current subject of this dialog.
     */
    //private String [] newRec = null;
    private ContractorRecord newRecord = null;

    /**
     * A reference to a ClientDialogMode. This indicates which one of the six
     * display modes the dialog is using.
     *
     * @see suncertify.client.gui.ClientDialogMode 
     */
    private ClientDialogMode dialogMode;

    /**
     * This is a boolean value indicating whether the creator of this dialog 
     * can proceed with the action for which the dialog was displayed.
     */
    private boolean proceedWithAction = false;
       
    /**
     * The ClientDialogAction class implements the ActionListener interface and
     * provides the implementation for the six different actions supported by
     * this dialog.
     *
     * @see #addButtonPanel(ClientDialogMode)
     * @see java.awt.event.ActionListener
     */
    private class ClientDialogAction implements ActionListener {

	/**
	 * A reference to a JDialog object which is the dialog using this 
	 * action. This is the enclosing instance of the BrokerClientDialog.
	 */
	JDialog parent;

	/**
	 * The only constructor.
	 *
	 * @param par A reference to an instance of the BrokerClientDialog class.
	 */
	ClientDialogAction(JDialog par ) {
	    parent = par;
	}
	
	/**
	 * This method displays the appropriate confirmation dialog depending
	 * on the mode parameter.
	 *
	 * @param mode This specifies the mode of this BrokerClientDialog.
	 *
	 * @return It returns the value JOptionPane.YES_OPTION or 
	 *         JOptionPane.NO_OPTION depending on which whether the user
	 *         user pushes the "YES" or the "NO" button in the dialog.
	 *
	 * @see suncertify.client.gui.ClientDialogMode
	 * @see javax.swing.JOptionPane
	 */
	private int canProceed( ClientDialogMode mode ) {
	    
	    switch( mode ) {
	    case ADD:
		return JOptionPane.showConfirmDialog( parent,
						      ADD_CONF_QUESTION, 
						      ADD_CONF_DIALOG_TITLE,
						      JOptionPane.YES_NO_OPTION
						      );
	    case UPDATE:
		return JOptionPane.showConfirmDialog( parent,
						      UPDATE_CONF_QUESTION,
						      UPDATE_CONF_DIALOG_TITLE,
						      JOptionPane.YES_NO_OPTION
						      );		
	    case RELEASE:
		return JOptionPane.showConfirmDialog( parent,
						      RELEASE_CONF_QUESTION,
						      RELEASE_CONF_DIALOG_TITLE,
						      JOptionPane.YES_NO_OPTION
						      );
	    case BOOK:
		return JOptionPane.showConfirmDialog( parent,
						      BOOK_CONF_QUESTION,
						      BOOK_CONF_DIALOG_TITLE,
						      JOptionPane.YES_NO_OPTION
						      );		    
	    case DELETE:
		return JOptionPane.showConfirmDialog( parent,
						      DELETE_CONF_QUESTION, 
						      DELETE_CONF_DIALOG_TITLE,
						      JOptionPane.YES_NO_OPTION
						      );
	    case SEARCH:
		return JOptionPane.showConfirmDialog( parent,
						      SEARCH_CONF_QUESTION, 
						      SEARCH_CONF_DIALOG_TITLE,
						      JOptionPane.YES_NO_OPTION
						      );
	    default:
		return JOptionPane.NO_OPTION;
	    }
	}

	/**
	 * This method displays the appropriate dismissal dialog depending on the
	 * mode parameter.
	 * 
	 * @param mode This specifies the mode of this BrokerClientDialog.
	 * 
	 * @see suncertify.client.gui.ClientDialogMode
	 * @see javax.swing.JOptionPane
	 */
	private void showDismissMsgDialog( ClientDialogMode mode ) {

	    switch( mode ) {
	    case ADD:
		JOptionPane.showMessageDialog(parent, ADD_DISMISS_MSG );
		break;
	    case UPDATE:
		JOptionPane.showMessageDialog(parent, UPDATE_DISMISS_MSG );
		break;
	    case BOOK:
		JOptionPane.showMessageDialog(parent, BOOK_DISMISS_MSG );
		break;
	    case DELETE:
		JOptionPane.showMessageDialog(parent, DELETE_DISMISS_MSG );
		break;
	    case SEARCH:
		JOptionPane.showMessageDialog(parent, SEARCH_DISMISS_MSG );
		break;
	    case RELEASE:
		JOptionPane.showMessageDialog(parent, RELEASE_DISMISS_MSG );
		break;
	    default:
	    }
	}
	
	/**
	 * This method implements the action for the activation of both the 
	 * "action button" and "cancel button" for this enclosing dialog. 
	 *
	 * <p> When the origin of the event is the "cancel button", it dismisses
	 *     the window and sets the proceedWithAction variable to false.
	 *
	 * <p> When the origin of the event is the "action button", the actions 
	 * taken depends on what the dialog mode is.
	 * <ul>
	 * <li> When the mode is "Update" it verifies that actual modification 
	 *      of the record had been done and displays the approriate dialog
	 *      if that is not the case.
         *      
	 * <li> If the mode is "Update" and actual modification of the record 
	 *      had been done or if the mode is one of the other modes, it does 
	 *      validation of the record depending on the mode and then displays
	 *      the appropriate dialog to complete the action.
	 * </ul>
	 *
	 * @param event A reference to an ActionEvent object providing data about
	 *        the event being processed.
	 *
	 * @see java.awt.event.ActionEvent
	 */
	public void actionPerformed( ActionEvent event ) {

	    if ( event.getSource() == actionButton ) {
		
		if ( (actionButton.getName().equals(UPDATE_RECORD_LABEL)) &&
		     ( ! oldRecord.differsFrom(newRecord) ) ) {

		    proceedWithAction = false;
		    JOptionPane.showMessageDialog(parent,
				      "No attribute has been modified!!" );
		} else {
		
		    if ( newRecord.isValid(dialogMode) ) {
			
			if (canProceed(dialogMode) == JOptionPane.YES_OPTION) {
			    proceedWithAction = true;		    
			    setVisible(false);
			} else {
			    proceedWithAction = false;
			    showDismissMsgDialog( dialogMode );
			}		    		  		    
		    } else {
			proceedWithAction = false;
			JOptionPane.showMessageDialog( parent,
						       newRecord.getReason() );
					      
		    }
		}		
	    } else if ( event.getSource() == cancelButton ) {
		proceedWithAction = false;
		setVisible(false);
	    }
	    
	}
    }
    
    /**
     * This the constructor. The creation of all the components displayed in the 
     * BrokerClientDialog class is done in this constructor. At the end of the 
     * construction, the dialog is displayed.
     *
     * @param mode This is the mode for this dialog. It is one of the values 
     *             specified in the ClientDialogMode class.
     *
     * @param parent This is the parent of this dialog.
     *
     * @param data This is a reference to a ContractorRecord object. 
     *
     * @see suncertify.server.ContractorRecord
     * @see suncertify.client.gui.ClientDialogMode
     */
    BrokerClientDialog(ClientDialogMode mode, JFrame parent,
		                                       ContractorRecord data) {
	
	super( parent, true );
	
	newRecord = data;
	
	oldRecord = new ContractorRecord(data);	
	
	dialogMode = mode;
	dataEntryPanel = new ContractorRecordPanel(mode, newRecord);
	
	add( dataEntryPanel, BorderLayout.CENTER );
	
	addButtonPanel( mode );
	pack();
	setLocationRelativeTo( parent );	
	setResizable( false );
	setVisible( true );
    }
    
    /**
     * This method creates the button panel for the BrokerClientDialog
     * class. The button panel consists of two buttons - the "cancel" button and
     * the "action" button. The type of action button created will depend on the
     * input parameter to the method.
     * 
     * @param mode This specifies the type of dialog being created.
     *
     * @see suncertify.client.gui.ClientDialogMode
     */
    private void addButtonPanel( ClientDialogMode mode ) {
	
	JPanel buttonPanel = new JPanel();
	buttonPanel.setBorder(
	      BorderFactory.createEtchedBorder(EtchedBorder.RAISED) );
	
	switch( mode ) {

	case ADD:
	    setTitle( NEW_RECORD_TITLE );
	    actionButton = new JButton(ADD_RECORD_LABEL);
	    actionButton.setToolTipText(ADD_RECORD_TIP);
	    actionButton.setName( ADD_RECORD_LABEL);
	    break;
	case DELETE:
	    setTitle( DELETE_RECORD_TITLE );
	    actionButton = new JButton(DELETE_RECORD_LABEL);
	    actionButton.setToolTipText(DELETE_RECORD_TIP);
	    actionButton.setName( DELETE_RECORD_LABEL );
	    break;
	case BOOK:
	    setTitle( BOOK_RECORD_TITLE );
	    actionButton = new JButton(BOOK_RECORD_LABEL);
	    actionButton.setToolTipText(BOOK_RECORD_TIP);
	    actionButton.setName( BOOK_RECORD_LABEL );
	    break;
	case RELEASE:
	    setTitle( RELEASE_RECORD_TITLE );
	    actionButton = new JButton(RELEASE_RECORD_LABEL);
	    actionButton.setToolTipText(RELEASE_RECORD_TIP);	    
	    actionButton.setName(RELEASE_RECORD_LABEL);
	    break;	    
	case SEARCH:
	    setTitle( SEARCH_TITLE );
	    actionButton = new JButton(SEARCH_LABEL);
	    actionButton.setToolTipText(SEARCH_TIP);
	    actionButton.setName( SEARCH_LABEL );
	    break;
	case UPDATE:	    
	    setTitle( UPDATE_RECORD_TITLE );
	    actionButton = new JButton(UPDATE_RECORD_LABEL);
	    actionButton.setToolTipText(UPDATE_RECORD_TIP);
	    actionButton.setName(UPDATE_RECORD_LABEL);
	    break;
	default:
	}

	actionButton.addActionListener( new ClientDialogAction(this) );
	actionButton.setOpaque( true );
	actionButton.setBackground( Color.GREEN );
	buttonPanel.add( actionButton );
	
	cancelButton = new JButton( CANCEL_BUTTON_LABEL );
	cancelButton.setToolTipText( CANCEL_BUTTON_TIP );
	cancelButton.setOpaque( true );
	cancelButton.setBackground( Color.RED );
	cancelButton.addActionListener( new ClientDialogAction(this) );
	buttonPanel.add( cancelButton);

	LayoutManager layout = this.getLayout();
	((BorderLayout)layout).setHgap( 100 );
	((BorderLayout)layout).setVgap( 50 );
	add( buttonPanel, BorderLayout.SOUTH );
    }

    /**
     * The creator of this dialog can call this method to determine whether
     * it can proceed with the operation that was the subject of this dialog.
     * The value returned depends on whether the user canceled the operation 
     * in this dialog or not.
     *
     * @return A boolean value indicating whether the operation requested
     *         on this dialog can proceed or not. 
     */
    public boolean canProceedWithAction() {
	
	return proceedWithAction;
    }
}
