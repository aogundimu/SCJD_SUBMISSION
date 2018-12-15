/*
 * BrokerAppClientGUI.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client.gui;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingUtilities;

import java.util.List;
import java.util.ArrayList;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;

import suncertify.common.AppConfigManager;
import suncertify.common.AppConfigParam;
import suncertify.common.AppConfigException;

import suncertify.client.BrokerClientException;
import suncertify.client.ClientController;
import suncertify.client.BrokerGUITableModel;
import suncertify.client.BrokerClient;

import suncertify.server.BrokerServerException;
import suncertify.server.ContractorRecord;

import suncertify.db.RecordNotFoundException;

import static suncertify.db.DatabaseMetaData.*;

/**
 * The <code>BrokerAppClientGUI</code> implements the main GUI for the "Contractor Broker"
 * application. It is made up of three major components:
 * <ul>
 * <li> A menubar
 * <li> A record display 
 * <li> A button panel
 * </ul>
 * 
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerAppClientGUI {

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * This the initial width of the application main frame.
     */
    private static final int FRAME_WIDTH = 1000;

    /**
     * This is the initial height of the application main frame.
     */
    private static final int FRAME_HEIGHT = 600;

    /**
     * This is the maximum width of the application main frame.
     */
    private static final int MAX_FRAME_WIDTH = 1175;

    /**
     * This is the maximum height of the application main frame.
     */
    private static final int MAX_FRAME_HEIGHT = 1000;

    /**
     * This is the main frame of the application GUI.
     *
     * @see javax.swing.JFrame
     */
    private JFrame mainFrame;

    /** 
     * The "File" menu label text.
     */ 
    private static final String FILE_MENU_LABEL = "File";

    /**
     * The "File" menu description text.
     */
    private static final String FILE_MENU_DESC = "This is the file menu";

    /**
     * The "New" menu label text.
     */
    private static final String NEW_MENU_LABEL = "New ";

    /**
     * The "New" menu description text
     */
    private static final String NEW_MENU_DESC = 
                	"This will bring up the new record addition dialog";

    /**
     * The "Open" menu label text.
     */
    private static final String OPEN_MENU_LABEL = "Open";

    /**
     * The "Open" menu description text
     */
    private static final String OPEN_MENU_DESC =
	                  "This will open an existing record for updating";

    /**
     * The "Exit" menu label text.
     */
    private static final String EXIT_MENU_LABEL = "Exit";

    /**
     * The "Exit" menu description text.
     */
    private static final String EXIT_MENU_DESC = "This exits from application";

    /**
     * The "Edit" menu label text.
     */
    private static final String EDIT_MENU_LABEL = "Edit";

    /**
     * The "Edit" menu description text.
     */
    private static final String EDIT_MENU_DESC = "This is the edit menu";

    /**
     * The "Search" menu label text.
     */
    private static final String SEARCH_MENU_LABEL = "Search ";

    /**
     * The "Search" menu description text.
     */
    private static final String SEARCH_MENU_DESC =
	                              "This will bring up the search dialog";

    /**
     * The "Delete" menu label text.
     */
    private static final String DELETE_MENU_LABEL = "Delete";
    
    /**
     * The "Delete" menu description text.
     */
    private static final String DELETE_MENU_DESC = "Delete the selected record";

    /**
     * The "Book" menu label text.
     */
    private static final String BOOK_MENU_LABEL = "Book";

    /**
     * The "Book" menu description text.
     */
    private static final String BOOK_MENU_DESC = "Book the selected contractor";

    /**
     * The "Release" menu label text.
     */
    private static final String RELEASE_MENU_LABEL = "Release";

    /**
     * The "Release" menu description text.
     */
    private static final String RELEASE_MENU_DESC =
	                           "Release a currently booked contractor";
    
    /**
     * This is the menubar for the application main GUI.
     * 
     * @see javax.swing.JMenuBar
     * @see #addMenuBar()
     */
    private JMenuBar menuBar;

    /**
     * The "New" menu item. The menu choice for invoking the new record dialog.
     * 
     * @see #addMenuBar()
     */
    JMenuItem newMenuItem;

    /**
     * The "Open" menu item. The menu choice for invoking the record update
     * dialog.
     *
     * @see #addMenuBar()
     */
    JMenuItem openMenuItem;

    /**
     * The "Search" menu item. The menu choice for invoking the record search 
     * dialog.
     * 
     * @see #addMenuBar()
     */
    JMenuItem searchMenuItem;

    /**
     * The "Delete" menu item. The menu choice for invoking the record deletion
     * dialog.
     *
     * @see #addMenuBar()
     */
    JMenuItem deleteMenuItem;

    /**
     * The "Book" menu item. The menu choice for invoking the record booking 
     * dialog.
     *
     * @see #addMenuBar()
     */
    JMenuItem bookMenuItem;

    /**
     * The "Release" menu item. The menu choice for invoking the record release
     * dialog.
     *
     * @see #addMenuBar()
     */
    JMenuItem releaseMenuItem;
    
    /**
     * The "Exit" menu item. The menu choice for exiting the application.
     * 
     * @see #addMenuBar()
     */
    JMenuItem exitMenuItem;

    /**
     * This is the table that displays the results of database searches
     *
     * @see javax.swing.JTable
     */
    private JTable mainTable;

    /**
     * This is the panel that serves as an enclosure for all the push buttons in the 
     * application main GUI.
     *
     * @see javax.swing.JPanel
     */
    private JPanel buttonPanel;

    /**
     * This is the button that initiates the process of adding a new contratctor 
     * to the application database. Pushing this button results in the display of
     * the <code> BrokerClientDialog </code>. 
     *
     * @see suncertify.client.gui.BrokerClientDialog
     * @see javax.swing.JButton
     */
    JButton addButton;

    /**
     * Reference to a <code>String</code> that serves as the label for the "ADD" 
     * button.
     *
     * @see #addButton
     */
    private static final String ADD_BUTTON_LABEL = "New ";

    /**
     * Reference to a <code>String</code> object denoting the text that is 
     * displayed when the "ADD" button gains focus.
     *
     * @see javax.swing.JButton
     */
    private static final String ADD_BUTTON_TIP = "Add a new contractor data";

    /**
     * Reference to a <code>String </code> object denoting the text displayed
     * when the addition of a record is successful.
     */
    private final static String ADD_SUCCESS_MSG = " was added successfully!";

    /**
     * Reference to a <code>String</code> object denoting the text displayed
     * when the addition of a record fails.
     *
     */
    private final static String ADD_FAILURE_MSG =
	                                "Failed to add record, reason: \n";  
    /**
     * This is reference to a <code>JButton</code> representing the push button
     * that is displayed on in the main GUI for initiating contractor record 
     * searches.
     * 
     * @see javax.swing.JButton
     */
    JButton searchButton;
    
    /**
     * This is a reference to a <code>String</code> object denoting the text 
     * displayed on the "SEARCH" button.
     *
     * see #searchButton
     */
    private static final String SEARCH_BUTTON_LABEL = "Search";

    /**
     * Refenrence to a <code>String</code> object denoting the text displayed
     * when the "SEARCH" button has the mouse focus.
     *
     * see #searchButton
     */
    private static final String SEARCH_BUTTON_TIP = "Search for contractors";

    /**
     * Reference to a <code>String</code> object denoting the text that is 
     * displayed when a database search is successful.
     */
    private final static String SEARCH_RESULT_MSG =
	                "Total records returned from search = ";

    /**
     * Reference to a <code>String</code> object denoting the text that is 
     * displayed when a search failed.
     */
    private final static String SEARCH_FAILURE_MSG =
	                "The records search failed, reason is : \n";

    /**
     * Reference to a <code>JButton</code> object representing the pushbutton
     * that is displayed in the main GUI for initiating the deletion of a 
     * contractor record.
     *
     * @see javax.swing.JButton
     */
    JButton deleteButton;

    /**
     * Reference to a <code>String</code> object denoting the label for the
     * "DELETE" button shown in the application main GUI.
     *
     * @see #deleteButton
     */
    private static final String DEL_BUTTON_LABEL = "Delete";

    /**
     * Referece to a <code>String</code> object denoting the text that is 
     * displayed when the "DELETE" button has the mouse focus.
     *
     * @see #deleteButton
     */
    private static final String DEL_BUTTON_TIP = "Delete selected contractor";

    /**
     * Reference to a <code>String</code> object denoting the text that is 
     * displayed after the sucessful deletion of a contractor record.
     *
     */
    private final static String DELETE_SUCCESS_MSG =
	                         " deleted successfully!";

    /**
     * Reference to a <code>String</code> object denoting the text displayed
     * when the deletion of a record is unsuccessful.
     */
    private final static String DELETE_FAILURE_MSG =
	                        "Record could not be deleted, reason: \n";
  
    /**
     * Reference to a <code>JButton</code> object denoting the push button that 
     * is displayed in the APP main GUI for initiating the booking of a record.
     *
     * @see javax.swing.JButton
     */
    JButton bookButton;

    /**
     * Reference to a <code>String</code> denoting the label for the "UPDATE" 
     * button.
     *
     * @see #bookButton
     */ 
    private static final String BOOK_BUTTON_LABEL = "Book";

    /**
     * The text displayed when the "Book" button has focus.
     *
     * @see #bookButton
     */
    private static final String BOOK_BUTTON_TIP =
	                          "Book the selected contractor";

    /**
     * The text displayed when an attempt to book a record fails.
     */
    private final static String BOOK_FAILURE_MSG =
	                 "Unable to book the selected record, reason:  \n";

    /**
     * The text displayed when an attempt to book a record was successful.
     */
    private final static String BOOK_SUCCESS_MSG =
	                       " was successfully booked!";

    /**
     * Reference to a <code>JButton</code> object denoting the push button that 
     * is displayed in the APP main GUI for initiating the update of a record.
     *
     * @see javax.swing.JButton
     */
    JButton updateButton;

    /**
     * The "Update" button label text;
     */
    private static final String UPDATE_BUTTON_LABEL = "Update";

    /**
     * The "Update" button tip - the text displayed when the button has the
     * mouse focus.
     */
    private static final String UPDATE_BUTTON_TIP =
                        	"Update the selected contractor data";

    /**
     * The text displayed after a record has been updated successfully.
     */
    private final static String UPDATE_SUCCESS_MSG =
                        	" was updated successfully!!";

    /**
     * The text displayed after an unsuccessful attempt to update a record.
     */
    private final static String UPDATE_FAILURE_MSG =
	                        "Failed to update record, reason: \n";

    /**
     * Reference to a <code>JButton</code> object denoting the push button that 
     * is displayed in the APP main GUI for initiating the release of a 
     * currently booked record.
     *
     * @see javax.swing.JButton
     */
    JButton releaseButton;

    /**
     * The "Release" button label text.
     */
    private static final String RELEASE_BUTTON_LABEL = "Release";

    /**
     * The "Release" button tip - text displayed when the button has the mouse
     * focus.
     */
    private static final String RELEASE_BUTTON_TIP =
	                        "Release currently booked contractor";
    
    /**
     * The text displayed after a booked record is successfully released.
     */
    private static final String RELEASE_SUCCESS_MSG =
	                        " was released successfully!";   

    /**
     * The text display after an unsuccessful attempt to release a record.
     */
    private static final String RELEASE_FAILURE_MSG =
	                  "Failed to release record, reason: \n";
                    
    /**
     * The text displayed for server related issues.
     */
    private static final String SERVER_ERROR_MSG =
	                   "Error communicating with the server: ";
    
    /**
     * The ClientController object used for executing DB related client actions.
     *
     * @see suncertify.client.ClientController 
     */
    private ClientController controller;

    /**
     * This is client object that owns this GUI object. This reference is needed
     * for shutting down the client when the user exits the application.
     * 
     * @see ExitAction 
     */
    private BrokerClient clientOwner;

    /**
     * An array of Strings denoting the criteria for each attribute of a record
     * corresponding to the array index.
     */
    private ContractorRecord lastSearchCriteria =
	new ContractorRecord(new String[] {null, null, null, null, null, null});

    /**
     * This defines the value returned when the call to JTable.getSelectedRow() 
     * is made when there is not row selected.
     * 
     * @see GUITableSelectionListener
     * @see javax.swing.JTable#getSelectedRow
     */
    private static final int NO_ROW_SELECTED = -1;

    /**
     * This is the list of record numbers for the all the records currently 
     * displayed in the application GUI table. 
     *
     * @see #setTableModel(ContractorRecord [])
     */
    private List<Integer> displayedRecNos;

    /**
     * Application exit confirmation question text.
     */
    private static final String APP_EXIT_CONF_QUESTION =
	             "Are you sure you want to exit the application?";

    /**
     * Application exit confirmation dialog title text.
     */
    private static final String APP_EXIT_DIALOG_TITLE =
	                               "Application Exit Confirmation";

    /**
     * The ExitAction class implements the ActionListener interface. This is the 
     * call back for the "Exit" menu item.
     * 
     * @see java.awt.event.ActionListener
     */
    private class ExitAction implements ActionListener {

	/**
	 * This method implements the action for the activation of the "Exit"
	 * menu item. It receives confirmation of the requested action from the
	 * user and then initiates the client shutdown and exits the application.
	 * 
	 * @param event An ActionEvent object with information on the event.
	 *
	 * @see java.awt.event.ActionEvent
	 */
	public void actionPerformed( ActionEvent event ) {

	    int opt = JOptionPane.showConfirmDialog(mainFrame,
						    APP_EXIT_CONF_QUESTION,
						    APP_EXIT_DIALOG_TITLE,
						    JOptionPane.YES_NO_OPTION);

	    if ( opt == JOptionPane.YES_OPTION ) {
		try {
		    clientOwner.stopClient();
		    
		} catch( BrokerClientException ex ) {
		    logger.log( Level.SEVERE,
				"Error when shutting down the client " +
				ex.getMessage() );
		}
	    
		System.exit(0);
	    } 
	}
    }
    
    /**
     * The AddAction class implements the ActionListener interface. This is the 
     * callback for the "ADD" button.
     *
     * @see #addButton
     * @see java.awt.event.ActionListener
     */
    private class AddAction implements ActionListener {

	BrokerClientDialog dialog;

	/**
	 * This method implements the action for the activation of the "Add"
	 * button. It creates the client dialog for acquiring record data from
	 * the user and then uses the controller to forward the record creation 
	 * request to the database server.
	 * 
	 * @param event An ActionEvent object with information on the event.
	 *
	 * @see suncertify.client.gui.BrokerClientDialog
	 *
	 * @see java.awt.event.ActionEvent
	 */
	public void actionPerformed( ActionEvent event ) {
	    
	    ContractorRecord record = new ContractorRecord(		
			   new String[]{null, null, null, null, null, null} );
		
	    dialog = new BrokerClientDialog( ClientDialogMode.ADD,
					     mainFrame,
					     record );

	    if ( dialog.canProceedWithAction() ) {		    

		    try {
			controller.addRecord( record );
			JOptionPane.showMessageDialog( mainFrame,
						       record.getName() + "/" +
						       record.getLocation() +
						       ADD_SUCCESS_MSG );
		    } catch( Exception ex) {
			JOptionPane.showMessageDialog( mainFrame,
						       ADD_FAILURE_MSG
						       + ex.getMessage() );
		    }					
	    }
	    
	    dialog.dispose();
	}	
    }

    /**
     * The SearchAction class implements the ActionListener interface. This is the 
     * callback for the "SEARCH" button.
     *
     * @see #searchButton
     * @see java.awt.event.ActionListener
     */
    private class SearchAction implements ActionListener {

	BrokerClientDialog dialog;
	
	/**
	 * This method implements the action for the activation of the "Search"
	 * button. It creates the client dialog for acquiring the search criteria
	 * from the user and then uses the controller to send the search request 
	 * to the database server.
	 * 
	 * @param event An ActionEvent object with information on the event.
	 *
	 * @see suncertify.client.gui.BrokerClientDialog
	 *
	 * @see java.awt.event.ActionEvent
	 */
	public void actionPerformed( ActionEvent event ) {

	    ContractorRecord record =
		new ContractorRecord( new String[]{null, null, null, null, null, null} );
		
	    dialog = new BrokerClientDialog( ClientDialogMode.SEARCH,
					     mainFrame,
					     record );
	    
	    if ( dialog.canProceedWithAction() ) {

		lastSearchCriteria = record;		
		ContractorRecord[] recsFound = new ContractorRecord[0];
		boolean success = false;
		    
		try {
		    recsFound = controller.findRecords(lastSearchCriteria);
		    success = true;
		} catch( RecordNotFoundException ex ) {
		    success = true;
		} catch( BrokerServerException ex ) {
		    JOptionPane.showMessageDialog( mainFrame,
						   SEARCH_FAILURE_MSG +
						   ex.getMessage() );
		}

		if ( success ) {
		    setTableModel( recsFound );			
		    JOptionPane.showMessageDialog( mainFrame,
						   SEARCH_RESULT_MSG +
						   recsFound.length );
		}		
	    }
	    
	    dialog.dispose();
	}
    }

    /**
     * This <code>DeleteAction</code> class implements the <code>ActionListener
     * </code> interface. It implements the action for the activation of the 
     * "DELETE" button. 
      * 
     * @see #deleteButton 
     * @see java.awt.event.ActionListener
     */
    private class DeleteAction extends MainAction implements ActionListener {

	/**
	 * This method implements the action for the activation of the 
	 * "Delete" button. It verifies that the record can be deleted and
	 * then creates the client dialog for record deletion. It then 
	 * uses the controller to send the record deletion request to the 
	 * database server.
	 * 
	 * @param event An ActionEvent object with information on the event.
	 *
	 * @see suncertify.client.gui.BrokerClientDialog
	 *
	 * @see java.awt.event.ActionEvent
	 */
	public void actionPerformed( ActionEvent event ) {

	    super.actionPerformed();

	    if (! record.isBooked() ) { 
		
		dialog = new BrokerClientDialog( ClientDialogMode.DELETE,
						 mainFrame,
						 record );

		if ( dialog.canProceedWithAction() ) {

		    try {
			controller.deleteRecord( record );			
			JOptionPane.showMessageDialog( mainFrame,
						       record.getName() + "/" +
						       record.getLocation() +
						       DELETE_SUCCESS_MSG );
		    } catch( Exception ex ) {
			JOptionPane.showMessageDialog( mainFrame,
						       DELETE_FAILURE_MSG
						       + ex.getMessage() );
		    }		    
		}

		dialog.dispose();
		
	    } else {
		JOptionPane.showMessageDialog( mainFrame, record.getReason() );
	    }
	}
    }

    /** 
     * The BookAction class implements the ActionListener interface. It is the
     * callback for the "BOOK" button.
     * 
     * @see #bookButton
     * @see java.awt.event.ActionListener
     */
    private class BookAction extends MainAction implements ActionListener {

	/**
	 * This method implements the action for the activation of the "Book"
	 * button. It verifies that the record can be booked and then creates 
	 * the client dialog for record booking. It then uses the controller 
	 * to send the record booking request to the database server.
	 * 
	 * @param event An ActionEvent object with information on the event.
	 *
	 * @see suncertify.client.gui.BrokerClientDialog
	 *
	 * @see java.awt.event.ActionEvent
	 */
	public void actionPerformed( ActionEvent event ) {

	    super.actionPerformed();

	    if ( ! record.isBooked() ) { 		    
		dialog = new BrokerClientDialog( ClientDialogMode.BOOK,
						 mainFrame,
						 record);
		if ( dialog.canProceedWithAction() ) {

		    try {
			controller.bookRecord( record );			
			JOptionPane.showMessageDialog( mainFrame,
						       record.getName() + "/" +
						       record.getLocation() +
							   BOOK_SUCCESS_MSG );
			} catch( Exception ex ) {
			    JOptionPane.showMessageDialog( mainFrame,
							   BOOK_FAILURE_MSG
							   + ex.getMessage() );
			}
		}

		dialog.dispose();
		
	    } else {
		
		JOptionPane.showMessageDialog( mainFrame, record.getReason() );
	    }	    
	}
    }

    /**
     * The ReleaseAction class implements the ActionListener interface. It is the 
     * callback for the "RELEASE" button.
     * 
     * @see #releaseButton
     * @see java.awt.event.ActionListener
     */
    private class ReleaseAction extends MainAction implements ActionListener {
	
	/**
	 * This method implements the action for the activation of the "Release"
	 * button. It verifies that the record can be released and then creates 
	 * the client dialog for releasing a record. It then uses the controller 
	 * to send the record release request to the database server.
	 * 
	 * @param event An ActionEvent object with information on the event.
	 *
	 * @see suncertify.client.gui.BrokerClientDialog
	 *
	 * @see java.awt.event.ActionEvent
	 */
	public void actionPerformed( ActionEvent event ) {

	    super.actionPerformed();
	    
	    if ( record.isBooked() ) {
		
		dialog = new BrokerClientDialog( ClientDialogMode.RELEASE,
						 mainFrame,
						 record );

		if ( dialog.canProceedWithAction() ) {

		    try {
			controller.releaseRecord( record );
			
			JOptionPane.showMessageDialog( mainFrame,
						       record.getName() + "/" +
						       record.getLocation() +
						       RELEASE_SUCCESS_MSG );
		    } catch( Exception ex ) {
			JOptionPane.showMessageDialog( mainFrame,
						       RELEASE_FAILURE_MSG
						       + ex.getMessage() );
		    }
		} 
		
		dialog.dispose();
		
	    } else {
			
		JOptionPane.showMessageDialog( mainFrame, record.getReason() );
	    }
	}
    }
    
    /**
     * The UpdateAction class implements the ActionListener interface. It is the 
     * callback for the "UPDATE" button.
     *
     * @see #updateButton
     * @see java.awt.event.ActionListener
     */
    private class UpdateAction extends MainAction implements ActionListener {

	/**
	 * This method implements the action for the activation of the 
	 * "Update" button. It verifies that the record can be updated and
	 * then creates the client dialog for record update. It then 
	 * uses the controller to send the record update request to the 
	 * database server.
	 * 
	 * @param event An ActionEvent object with information on the event.
	 *
	 * @see suncertify.client.gui.BrokerClientDialog
	 *
	 * @see java.awt.event.ActionEvent
	 */
	public void actionPerformed( ActionEvent event ) {

	    super.actionPerformed();

	    if ( ! record.isBooked() ) {
		
		ContractorRecord oldRecord = new ContractorRecord(record);
	    
		dialog = new BrokerClientDialog( ClientDialogMode.UPDATE,
						 mainFrame,
						 record );
	    
		if ( dialog.canProceedWithAction() ) {
		    
		    try {
			controller.updateRecord(oldRecord, record);		    
			JOptionPane.showMessageDialog( mainFrame,
						       record.getName() + "/" +
						       record.getLocation() + 
						       UPDATE_SUCCESS_MSG );
		    } catch( Exception ex ) {
			JOptionPane.showMessageDialog( mainFrame,
						       UPDATE_FAILURE_MSG
						       + ex.getMessage() );
		    }
		}

		dialog.dispose();
		
	    } else {

		JOptionPane.showMessageDialog( mainFrame, record.getReason() );
	    }	    
	}
    }
    
    /**
     * The MainAction class is the baseclass for some of the classes that 
     * implement the callback actions in the application.
     * 
     * @see DeleteAction
     * @see UpdateAction
     * @see BookAction
     * @see ReleaseAction
     */
    private class MainAction {

	/**
	 * A reference to the BrokerClientDialog object that is being used in the 
	 * current action.
	 */
	BrokerClientDialog dialog;

	/**
	 * 
	 * action is being done.
	 */
	ContractorRecord record;

	/**
	 * This method performs the actions common to all the callbacks. These
	 * actions include:
	 * <ul>
	 * <li> Getting the currently selected row in the table in the main GUI.
	 * <li> Setting the record number for the currently selected record.
	 * <li> Clearing the selection in the table. 
	 * <li> Getting the data for the selected record.
	 * <li> Disabling all the buttons and menu items that are enabled when a 
	 *      row is selected in the APP GUI table.
	 * </ul>
	 */
	void actionPerformed() {
	    int currSelRow = mainTable.getSelectedRow();
	    int selectedRecNo = displayedRecNos.get(currSelRow);
	    mainTable.clearSelection();		
	    String [] data = getSelectedRowData( currSelRow );
	    record = new ContractorRecord( selectedRecNo, data );	    
	    deleteButton.setEnabled( false );
	    bookButton.setEnabled( false );
	    updateButton.setEnabled( false );
	    releaseButton.setEnabled( false );
	    deleteMenuItem.setEnabled( false );
	    bookMenuItem.setEnabled(false );
	    releaseMenuItem.setEnabled(false );
	    openMenuItem.setEnabled(false);	
	}
    }
        
    /**
     * The GUITableSelectionListener implements the ListSelectionListener
     * interface and its main responsibility is the enabling and disabling of 
     * buttons and menuitems in the APP main GUI whenever a row selection or 
     * deselection is done in the APP GUI table.
     *
     * @see javax.swing.event.ListSelectionListener
     */
    private class GUITableSelectionListener implements ListSelectionListener {

	/**
	 * This method satisfies the ListSelectionListener interface 
	 * implementation for this class. It enables all the appropriate buttons
	 * and menu items when a row is selection is done and it disables the 
	 * same buttons and menu items when a deselection is done.
	 *
	 * @param event A reference to a ListSelectionEvent object providing 
	 *        information about the event being reported. 
	 *
	 * @see javax.swing.event.ListSelectionEvent
	 */
	public void valueChanged( ListSelectionEvent event ) {

	    if ( mainTable.getSelectedRow() != NO_ROW_SELECTED ) {
		deleteButton.setEnabled( true );
		updateButton.setEnabled( true );
		bookButton.setEnabled( true );
		releaseButton.setEnabled( true );
		deleteMenuItem.setEnabled( true );
		bookMenuItem.setEnabled( true );
		releaseMenuItem.setEnabled( true );
		openMenuItem.setEnabled( true );	
	    } else {
		deleteButton.setEnabled( false );
		updateButton.setEnabled( false );
		bookButton.setEnabled( false );
		releaseButton.setEnabled( false );
		deleteMenuItem.setEnabled( false );
		bookMenuItem.setEnabled( false );
		releaseMenuItem.setEnabled( false );
		openMenuItem.setEnabled( false );	
	    }
	}
    }

    /**
     * This is the constructor for the application main GUI. It does the 
     * following:
     *
     * <ul>
     * <li> Setting the GUI look and feel.
     * <li> Creation of the main GUI menubar.
     * <li> Creation and population of the APP main GUI table.
     * <li> Creation of the main GUI button panel.
     * </ul>
     *
     * @param ctl This is reference to a ClientController object that will be 
     *        facilitate the forwarding of database requests by this GUI.
     *
     * @param owner This is the reference to a BrokerClient object that is
     *        creating this GUI        
     * 
     * @throws BrokerClientException If an error is encountered during the GUI
     *         construction process. 
     *
     * @throws BrokerServerException If an error is encountered in communication
     *         with the server during the GUI construction process.
     */
    public BrokerAppClientGUI(ClientController ctl, BrokerClient owner) throws
	                                                BrokerServerException,
	                                                BrokerClientException {
	
	controller = ctl;
	clientOwner = owner;
	
	try {
	    
	    AppConfigManager configMgr = AppConfigManager.getInstance();
	    
	    setLookAndFeel( configMgr.get(AppConfigParam.GUI_LOOK_AND_FEEL ) );
	    
	    String title = configMgr.get(AppConfigParam.CLIENT_GUI_TITLE);
	    
	    mainFrame = new JFrame(title);
	    
	} catch( AppConfigException ex ) {
	    
	    logger.log( Level.SEVERE,
			"Caught IOException in BrokerAppClientGUI constructor - "
			+ ex.getMessage() );

	    BrokerClientException e =
		new BrokerClientException( "I/O issues - " + ex.getMessage(), ex );

	    logger.throwing("BrokerAppClientGUI",
			    "BrokerAppClientGUI(ClientController, BrokerClient)",
			    e );
	    throw e;
	}
	
	addMenuBar();
	addTable();
	addButtonPanel();
	
	mainFrame.pack();
	mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
	mainFrame.setMaximumSize(new Dimension(MAX_FRAME_WIDTH,MAX_FRAME_HEIGHT));
	mainFrame.setVisible( true );
	mainFrame.setLocationRelativeTo( null );
	mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    /**
     * This method adds the JTable component to the APP main GUI. Is is called 
     * during the main GUI construction process. 
     * 
     * @see #BrokerAppClientGUI(ClientController,BrokerClient)
     *
     * @throws BrokerServerException If a problem is encountered when 
     *         communicating with the server during the construction process.
     */
    private void addTable() throws BrokerServerException {

	mainTable = new JTable();

	mainFrame.getContentPane().add(
			new JScrollPane(mainTable), BorderLayout.CENTER );

	ContractorRecord [] records = new ContractorRecord[0];
	
	try {
	    records = controller.findRecords( lastSearchCriteria );	
	    setTableModel( records );
	} catch( RecordNotFoundException ex ) {

	}
		
	mainTable.setRowSelectionAllowed( true );	
	mainTable.getSelectionModel().addListSelectionListener(
				     new GUITableSelectionListener() );	
	mainTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

	mainTable.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );

	mainTable.setShowVerticalLines( true );
	mainTable.setIntercellSpacing( new Dimension(5,0) );
	mainTable.getTableHeader().setReorderingAllowed( false );
    }

    /**
     * This method creates the model for the main GUI table using the array 
     * of records provided.
     *
     * @param records An array of ContractorRecords 
     *
     * @see #addTable
     */
    private void setTableModel(ContractorRecord [] records) { 

	BrokerGUITableModel tableModel = new BrokerGUITableModel();
	
	displayedRecNos = new ArrayList<>();

	for ( ContractorRecord record : records ) {
	    tableModel.addRow( record.getAttributes() );
	    displayedRecNos.add( record.getRecordNumber() );
	}
	
	mainTable.setModel( tableModel );
		
	TableColumnModel tcm = mainTable.getColumnModel();

	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	dtcr.setBackground( Color.LIGHT_GRAY );

	DefaultTableCellRenderer hdRen1 = new DefaultTableCellRenderer();
	hdRen1.setBackground( Color.ORANGE );
	hdRen1.setFont( new Font( Font.DIALOG, Font.BOLD, 12 ) );

	DefaultTableCellRenderer hdRen2 = new DefaultTableCellRenderer();
	hdRen2.setBackground( Color.ORANGE );
	hdRen2.setFont( new Font( Font.DIALOG, Font.BOLD, 12 ) );

	TableColumn nameColumn = tcm.getColumn(NAME_IDX);
	nameColumn.setPreferredWidth( MIN_NAME_FIELD_SIZE );
	nameColumn.setMinWidth(  MAX_NAME_FIELD_SIZE );
	nameColumn.setMaxWidth(  MAX_NAME_FIELD_SIZE );
	nameColumn.setCellRenderer( dtcr );
	nameColumn.setHeaderRenderer( hdRen1 );

	TableColumn locationColumn = tcm.getColumn(LOCATION_IDX);
	locationColumn.setPreferredWidth(MIN_LOCATION_FIELD_SIZE );
	locationColumn.setMinWidth(  MIN_LOCATION_FIELD_SIZE );
	locationColumn.setMaxWidth(  MAX_LOCATION_FIELD_SIZE );
	locationColumn.setHeaderRenderer( hdRen2 );

	TableColumn specialitiesColumn = tcm.getColumn(SPECIALITIES_IDX);
	specialitiesColumn.setPreferredWidth(MIN_SPECIALITIES_FIELD_SIZE );
	specialitiesColumn.setMinWidth(MIN_SPECIALITIES_FIELD_SIZE);
	specialitiesColumn.setMaxWidth(MAX_SPECIALITIES_FIELD_SIZE);
	specialitiesColumn.setCellRenderer( dtcr );
	specialitiesColumn.setHeaderRenderer( hdRen1 );

	TableColumn sizeColumn = tcm.getColumn(SIZE_IDX);
	sizeColumn.setPreferredWidth(MAX_SIZE_FIELD_SIZE);
	sizeColumn.setMinWidth(MAX_SIZE_FIELD_SIZE );
	sizeColumn.setMaxWidth(MAX_SIZE_FIELD_SIZE );
	sizeColumn.setHeaderRenderer( hdRen2 );

	TableColumn rateColumn = tcm.getColumn(RATE_IDX); 
	rateColumn.setPreferredWidth(  MAX_RATE_FIELD_SIZE );
	rateColumn.setMinWidth(  MAX_RATE_FIELD_SIZE );
	rateColumn.setMaxWidth(  MAX_RATE_FIELD_SIZE );
	rateColumn.setCellRenderer( dtcr );
	rateColumn.setHeaderRenderer( hdRen1 );

	TableColumn ownerColumn = tcm.getColumn(OWNER_IDX);
	ownerColumn.setPreferredWidth( MAX_OWNER_FIELD_SIZE );
	ownerColumn.setMinWidth( MAX_OWNER_FIELD_SIZE );
	ownerColumn.setMaxWidth( MAX_OWNER_FIELD_SIZE );
	ownerColumn.setHeaderRenderer( hdRen2 );
    }

    /**
     * This method by the client owner of the GUI object to update the displayed
     * data in the APP main GUI table. Since the update is being done from a 
     * another thread, it uses the SwingUtilities.invokeLater to ensure APP table
     * model coherence since the event delivery thread might be in the process 
     * of modifying the table model.  
     * 
     * 
     * @see suncertify.client.BrokerClient#notifyUpdate
     * @see javax.swing.SwingUtilities#invokeLater
     */
    public void update( ) {

	SwingUtilities.invokeLater( new Runnable() {
		public void run() {
		    try {
		       ContractorRecord [] records =
			     controller.findRecords(lastSearchCriteria);
		       setTableModel( records );
		    } catch ( BrokerServerException ex ) {			
			logger.log( Level.SEVERE,
				    "Caught exception in " +
				    "BrokerAppClientGUI.update - " +
				    ex.getMessage() );
		    } catch ( RecordNotFoundException ex ) {
			logger.info("Database query in BrokerAppClientGUI.update"
				    + " returned zero records!" );

			setTableModel( new ContractorRecord[0] );
		    }
		}
	    } );	
    }

    
    /**
     * This method constructs the application main GUI button panel. It is one
     * of the methods called from the constructor.
     * 
     * @see #BrokerAppClientGUI(ClientController,BrokerClient)
     */
    private void addButtonPanel() {

	buttonPanel = new JPanel();
	
	addButton = new JButton(ADD_BUTTON_LABEL);
	addButton.setToolTipText(ADD_BUTTON_TIP);
	addButton.addActionListener( new AddAction() );
	addButton.setOpaque( true );
	addButton.setBackground( Color.GREEN );
	buttonPanel.add( addButton );

	searchButton = new JButton(SEARCH_BUTTON_LABEL);
	searchButton.setToolTipText(SEARCH_BUTTON_TIP );
	searchButton.addActionListener(new SearchAction() );
	searchButton.setOpaque( true );
	searchButton.setBackground( Color.YELLOW );
	buttonPanel.add( searchButton );
	
	deleteButton = new JButton(DEL_BUTTON_LABEL);
	deleteButton.setToolTipText(DEL_BUTTON_TIP );
	deleteButton.addActionListener(new DeleteAction() );
	deleteButton.setOpaque( true );
	deleteButton.setBackground( Color.RED );
	deleteButton.setEnabled( false );
	buttonPanel.add( deleteButton );

	bookButton = new JButton(BOOK_BUTTON_LABEL);
	bookButton.setToolTipText(BOOK_BUTTON_TIP);
	bookButton.addActionListener(new BookAction() );
	bookButton.setOpaque( true );
	bookButton.setBackground( Color.PINK );
	bookButton.setEnabled( false );
	buttonPanel.add( bookButton );
	
	releaseButton = new JButton(RELEASE_BUTTON_LABEL);
	releaseButton.setToolTipText(RELEASE_BUTTON_TIP);
	releaseButton.addActionListener( new ReleaseAction() );
	releaseButton.setOpaque( true );
	releaseButton.setBackground( Color.CYAN );
	releaseButton.setEnabled( false );
	buttonPanel.add( releaseButton );
	
	updateButton = new JButton(UPDATE_BUTTON_LABEL);
	updateButton.setToolTipText(UPDATE_BUTTON_TIP);
	updateButton.addActionListener( new UpdateAction() );
	updateButton.setOpaque( true );
	updateButton.setBackground( Color.MAGENTA );
	updateButton.setEnabled( false );
	buttonPanel.add( updateButton );

	LayoutManager layout = buttonPanel.getLayout();
	((FlowLayout)layout).setHgap( 20 );
	((FlowLayout)layout).setVgap( 15 );
	buttonPanel.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
	mainFrame.getContentPane().add( buttonPanel, BorderLayout.SOUTH );
    }

    
    /**
     * This method creates and configures the application GUI main menubar. It
     * is called from the constructor.
     *
     * @see #BrokerAppClientGUI(ClientController,BrokerClient)
     */
    private void addMenuBar() {

	menuBar = new JMenuBar();
	
	JMenu fileMenu = new JMenu( FILE_MENU_LABEL );

	fileMenu.setMnemonic( KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(FILE_MENU_DESC);
	menuBar.add( fileMenu );

	newMenuItem = new JMenuItem(NEW_MENU_LABEL, KeyEvent.VK_N);
        newMenuItem.setAccelerator( KeyStroke.getKeyStroke( 
                                    KeyEvent.VK_N, ActionEvent.ALT_MASK ) );
        newMenuItem.getAccessibleContext().setAccessibleDescription(
							    NEW_MENU_DESC );
	newMenuItem.addActionListener( new AddAction() );
        fileMenu.add( newMenuItem );

	
	openMenuItem = new JMenuItem(OPEN_MENU_LABEL, KeyEvent.VK_O);
        openMenuItem.setAccelerator( KeyStroke.getKeyStroke( 
                                     KeyEvent.VK_O, ActionEvent.ALT_MASK ) );
	openMenuItem.addActionListener( new UpdateAction() );
	openMenuItem.setEnabled( false );
        openMenuItem.getAccessibleContext().setAccessibleDescription(
							    OPEN_MENU_DESC );
	fileMenu.add( openMenuItem );
        fileMenu.addSeparator();

	exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_X, 
                                                 ActionEvent.ALT_MASK ) );
	exitMenuItem.addActionListener( new ExitAction() );
        exitMenuItem.getAccessibleContext().setAccessibleDescription( 
							 EXIT_MENU_DESC );
        fileMenu.add( exitMenuItem );

	menuBar.add( Box.createHorizontalStrut( 20 ) );
	

        JMenu editMenu;
        editMenu = new JMenu( EDIT_MENU_LABEL );
        editMenu.setMnemonic( KeyEvent.VK_E );  
        editMenu.getAccessibleContext().setAccessibleDescription(EDIT_MENU_DESC);
    	menuBar.add( editMenu );

	searchMenuItem = new JMenuItem(SEARCH_MENU_LABEL, KeyEvent.VK_S);
        searchMenuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_S, 
                                                   ActionEvent.ALT_MASK ) );
	searchMenuItem.addActionListener( new SearchAction() );
        searchMenuItem.getAccessibleContext().setAccessibleDescription( 
						         SEARCH_MENU_DESC );
        editMenu.add( searchMenuItem );

	deleteMenuItem = new JMenuItem(DELETE_MENU_LABEL, KeyEvent.VK_D);
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_D, 
                                                   ActionEvent.ALT_MASK ) );
	deleteMenuItem.addActionListener( new DeleteAction() );
	deleteMenuItem.setEnabled( false );
	deleteMenuItem.getAccessibleContext().setAccessibleDescription( 
							 DELETE_MENU_DESC );
        editMenu.add( deleteMenuItem );	

	bookMenuItem = new JMenuItem(BOOK_MENU_LABEL, KeyEvent.VK_B);
        bookMenuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_B, 
                                                   ActionEvent.ALT_MASK ) );
	bookMenuItem.addActionListener( new BookAction() );
	bookMenuItem.setEnabled( false );
	bookMenuItem.getAccessibleContext().setAccessibleDescription( 
						       BOOK_MENU_DESC );
        editMenu.add( bookMenuItem );	

	releaseMenuItem = new JMenuItem(RELEASE_MENU_LABEL, KeyEvent.VK_R);
        releaseMenuItem.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_R, 
                                                   ActionEvent.ALT_MASK ) );
	releaseMenuItem.addActionListener( new ReleaseAction() );
	releaseMenuItem.setEnabled( false );
        releaseMenuItem.getAccessibleContext().setAccessibleDescription( 
							RELEASE_MENU_DESC );
        editMenu.add( releaseMenuItem );
	
	mainFrame.setJMenuBar( menuBar );
    }

    /**
     * This method fetches currently selected row from the APP GUI table.
     *
     * @param row An integer value specifying the row.
     *
     * @return An array of String objects denoting the attributes of the record
     *         displayed in the row specified in the parameter.   
     */
    private String [] getSelectedRowData(int row) {

	int totColumns = mainTable.getColumnCount();
	
	String [] values = new String[totColumns ];

	for (int i = 0; i < totColumns; ++ i ) {
	    values[i] = (String)mainTable.getValueAt( row, i );
	}

	return values;
    }
    
    /**
     * This method sets the look and feel for the APP main GUI. 
     *
     * @param lookNfeel A String object denoting the look and feel. 
     * 
     * @throws BrokerClientException If an error is encountered in the call 
     *         to UIManager.setLookAndFeel
     *
     * @see javax.swing.UIManager#setLookAndFeel(String)
     */
    private void setLookAndFeel(String lookNfeel) throws BrokerClientException {

	String lfClassName = null;

	UIManager.LookAndFeelInfo [] lfi = UIManager.getInstalledLookAndFeels();

	if ( lookNfeel != null ) { 
	    for ( UIManager.LookAndFeelInfo lf : lfi ) {
		if ( lf.getName().equals( lookNfeel.trim() ) ) {
		    lfClassName = lf.getClassName();		    
		    break;
		}
	    }
	} else {
	    lfClassName = UIManager.getCrossPlatformLookAndFeelClassName();
	}

	if ( lfClassName != null ) {
	    try {
		
		UIManager.setLookAndFeel( lfClassName );
		
	    } catch( Exception ex ) {

		logger.log( Level.SEVERE,
			    "Exception caught setting look and feel - " +
			    ex.getMessage() );
		
		BrokerClientException e =
		    new BrokerClientException( "Look and Feel", ex );

		logger.throwing("BrokerAppClientGUI","setLookAndFeel(String)",e);
		
		throw e;
	    }
	}
    }
}
