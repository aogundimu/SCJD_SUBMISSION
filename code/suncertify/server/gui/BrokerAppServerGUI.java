/*
 * BrokerAppServerGUI.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.server.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.IOException;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.common.AppConfigManager;
import suncertify.common.AppConfigParam;
import suncertify.common.AppRunMode;
import suncertify.common.AppConfigException;

import suncertify.common.gui.AppConfigOptionsPanel;

import suncertify.server.BrokerServer;
import suncertify.server.BrokerServerException;

/**
 * The BrokerAppServerGUI class implements the GUI interface for the remote
 * application server in the contractor broker application. This is the GUI 
 * that is displayed when the application run mode is "server only".
 * 
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerAppServerGUI {

    /**
     *
     */
    private static final long serialVersionUID = 20120678907775L;


    /**
     * This is a reference to a Logger object. The logger's name is the fully 
     * qualified name for this class. 
     *
     * @see java.util.logging.Logger
     */
    private final Logger logger = Logger.getLogger( this.getClass().getName() );
    
    /**
     * The server APP GUI menubar
     *
     * @see javax.swing.JMenuBar
     */
    JMenuBar menuBar;
    
    /**
     * The server APP GUI File Menu.
     * @see javax.swing.JMenu
     */
    JMenu fileMenu;
    
    /**
     * The server APP GUI Quit Menu.
     *
     * @see javax.swing.JMenu
     */
    JMenuItem quitMenu;
    
    /**
     * The "Start Server" button.
     *
     * @see javax.swing.JButton
     */
    private JButton startServerButton;    

    /**
     * The label for the "Start Server" button.
     */
    private static final String START_SERVER_BUTTON_TXT = "Start Server";

    /**
     * The tip for the "Start Server" button. Text displayed when the 
     * button has the mouse input focus. 
     */
    private static final String START_SERVER_TOOL_TIP =
	        "Starts the server using configuration parameters entered";

    /**
     * The "Stop Server" button.
     * 
     * @see javax.swing.JButton
     */
    private JButton stopServerButton;

    /**
     * The label for the "Stop Server" button.
     */
    private static final String STOP_SERVER_BUTTON_TXT = "Stop Server";

    /**
     * The tip for the "Stop Server" button. Text displayed when the 
     * button has the mouse input focus. 
     */
    private static final String STOP_SERVER_TOOL_TIP =
	"Stops the server if it is running and keeps the server window up";

    /**
     * The "Exit" button.
     *
     * @see javax.swing.JButton
     */
    private JButton exitButton;

    /**
     * The "Exit" button label text.
     */
    private static final String EXIT_BUTTON_TXT = "Exit";

    /**
     * The tip for the "Exit" button. Text displayed when the 
     * button has the mouse input focus. 
     */
    private static final String EXIT_TOOL_TIP =
	"Stops the server if it is running and dismisses the server window";

    /**
     * The server APP GUI status label. 
     * @see #addStatusPanel
     */
    private JLabel statusLabel;

    /**
     * The text displayed when APP server GUI is up and the server is not in the
     * running state.
     */
    private static final String START_SERVER_INSTR =
	"Enter the requested parameters and push Start Server to run the " +
	"server or Exit to terminate";

    /**
     * The text displayed when the APP server GUI is up and the server is 
     * running.
     */
    private static final String SERVER_RUNNING_STATUS =
	 "The server is now running, push Stop Server to stop the server" +
	             " or Exit to terminate";

    /**
     * The text displaye when there is an error starting the server.
     */
    private static final String SERVER_ERROR_MSG =
	              "Error starting the server!! Check the APP log file.";

    /**
     *
     */
    private static final String START_CONF_QUESTION =
       "Are you sure you want to start the server with the entered parameters?";

    /**
     *
     */
    private static final String START_CONF_DIALOG_TITLE = "Start The Server";

    /**
     *
     */
    private static final String STOP_CONF_QUESTION =
                               "Are you sure you want to stop the server?";

    /**
     *
     */
    private static final String STOP_CONF_DIALOG_TITLE = "Stop The Server";

    /**
     *
     */
    private static final String EXIT_CONF_QUESTION =
       "Are you sure you want to stop the server and exit the application?";

    /**
     *
     */
    private static final String EXIT_CONF_DIALOG_TITLE =
	                         "Exit The Server Application";
	

    /**
     * A boolean value used for keeping track of the server APP status.
     */
    private boolean serverStarted = false;

    /**
     * The server application mainframe.
     */
    private JFrame mainFrame;

    /**
     * A reference to the APP server object. 
     */
    private BrokerServer appServer;

    /**
     * A reference to the APP configuration options panel.
     * 
     * @see suncertify.common.gui.AppConfigOptionsPanel
     */
    AppConfigOptionsPanel optionsPanel;
	
    /**
     * The AppServerAction class implements the action for the three buttons
     * and menu items in the APP server GUI.
     *
     * @see java.awt.event.ActionListener
     */
    private class AppServerAction implements ActionListener {

	/**
	 * This method implements the action for the buttons and menuitems. It 
	 * determines what should be done by getting the origin of the event 
	 * from the ActionEvent object argument. 
	 * 
	 * @param event A reference to a ActionEvent object providing data about
	 *        the event.
	 */
	public void actionPerformed( ActionEvent event ) {
	    
	    try {
		if ( event.getSource() == startServerButton ) {
		    
		    if (JOptionPane.showConfirmDialog(mainFrame,
						      START_CONF_QUESTION,
						      START_CONF_DIALOG_TITLE,
						      JOptionPane.YES_NO_OPTION)
			 == JOptionPane.YES_OPTION ) {
			
			appServer = BrokerServer.getServer(AppRunMode.SERVER);
			
			appServer.startServer();
			
			/* 
			 * Disable the component displaying the server 
			 * configuration values, no changes allowed after 
			 * starting the server.
			 */
			optionsPanel.disableComponents();

			startServerButton.setEnabled( false );
		    
			stopServerButton.setEnabled( true );

			serverStarted = true;

			statusLabel.setText( SERVER_RUNNING_STATUS );
		    }
		
		} else if ( event.getSource() == stopServerButton ) {

		    if (JOptionPane.showConfirmDialog(mainFrame,
						      STOP_CONF_QUESTION,
						      STOP_CONF_DIALOG_TITLE,
						      JOptionPane.YES_NO_OPTION)
			== JOptionPane.YES_OPTION ) { 
			appServer.stopServer();
			
			optionsPanel.enableComponents();
		    
			startServerButton.setEnabled( true );
			
			stopServerButton.setEnabled( false );
			
			serverStarted = false;
			
			statusLabel.setText( START_SERVER_INSTR );
		   }
		    
		} else if ( ( event.getSource() == exitButton )
			    ||
			    ( event.getSource() == quitMenu ) ) {


		    if (JOptionPane.showConfirmDialog(mainFrame,
						      EXIT_CONF_QUESTION,
						      EXIT_CONF_DIALOG_TITLE,
						      JOptionPane.YES_NO_OPTION)
			== JOptionPane.YES_OPTION ) {    

			if ( serverStarted ) {
			    appServer.stopServer();
			}
			
			System.exit(0);
		    }		    
		}		
	    } catch( BrokerServerException exc ) {
		String msg = "Caught exception in the APP server - " +
		                                         exc.getMessage();
		logger.log( Level.SEVERE, msg, exc );
		
		statusLabel.setText( SERVER_ERROR_MSG );
	    }
	}
    }

    
    /** 
     * The BrokerAppServerGUI constructs the components constituting the 
     * application server main GUI and makes the GUI visible at the end.
     *
     * @see #addMenuBar
     * @see #addConfigOptionsPanel
     * @see #addStatusPanel
     * @see #addCommandOptionsPanel
     * 
     * @throws BrokerServerException is thrown if there were errors in 
     *         contructing the GUI. A possibility is when there were issues with
     *         the application configuration file.
     */
    public BrokerAppServerGUI( ) throws BrokerServerException {

	try {
	    AppConfigManager configMgr = AppConfigManager.getInstance();
	    
	    mainFrame = new JFrame(configMgr.get(AppConfigParam.SERVER_GUI_TITLE));
	    
	    mainFrame.setSize( 600, 300 );
	    mainFrame.setResizable( false );
	    mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	    addMenuBar();

	    addConfigOptionsPanel();

	    addStatusPanel();

	    addCommandOptionsPanel();
	    
	    mainFrame.setLocationRelativeTo( null );

	    mainFrame.pack();
	    
	    mainFrame.setVisible(true);
	    
	} catch( AppConfigException ex ) {
	    String msg = "Caught an exception in APP server GUI contruction - " +
		             ex.getClass().getName() + " - " + ex.getMessage();
	    logger.log( Level.SEVERE, msg );
	    BrokerServerException e = new BrokerServerException(msg, ex );

	    logger.throwing("BrokerAppServerGUI", "BrokerAppServerGUI()", e );
	    throw e;
	}
    }

    /**
     * This method adds the menubar to the APP server main GUI.
     */
    private void addMenuBar() {	
	menuBar = new JMenuBar();
	fileMenu = new JMenu("File" );
	fileMenu.setMnemonic( KeyEvent.VK_F );
	quitMenu = new JMenuItem("Quit");
	quitMenu.addActionListener( new AppServerAction() );
	quitMenu.setMnemonic( KeyEvent.VK_Q );
	fileMenu.add( quitMenu );	
	menuBar.add( fileMenu );	
	mainFrame.setJMenuBar( menuBar );
    }

    /**
     * This method adds the configuration options panel to the APP server main
     * GUI.
     * 
     * @see suncertify.common.gui.AppConfigOptionsPanel
     */
    private void addConfigOptionsPanel() {

	optionsPanel = new AppConfigOptionsPanel(AppRunMode.SERVER);
	
	mainFrame.add( optionsPanel, BorderLayout.NORTH );
    }

    /**
     * This method adds the command options panel to the APP server main 
     * GUI. This panel is made up of the "Start Server", "Stop Server" and 
     * "Exit" buttons. 
     */
    private void addCommandOptionsPanel() {

	JPanel panel = new JPanel();

	panel.setLayout( new FlowLayout(FlowLayout.CENTER) );
	
	/* Start Server */
	startServerButton = new JButton( START_SERVER_BUTTON_TXT );
	startServerButton.setToolTipText( START_SERVER_TOOL_TIP );
	startServerButton.setName(  START_SERVER_BUTTON_TXT );
	startServerButton.setOpaque( true );
	startServerButton.setBackground( Color.GREEN );
	startServerButton.addActionListener( new AppServerAction() );
	panel.add(startServerButton);
	
	/* Stop Server */
	stopServerButton = new JButton( STOP_SERVER_BUTTON_TXT );
	stopServerButton.setToolTipText( STOP_SERVER_TOOL_TIP );
	stopServerButton.setName(  STOP_SERVER_BUTTON_TXT );
	stopServerButton.setOpaque( true );
	stopServerButton.setBackground( Color.YELLOW );
	stopServerButton.addActionListener( new AppServerAction() );
	stopServerButton.setEnabled( false );
	panel.add(stopServerButton);

	/* Exit */
	exitButton = new JButton( EXIT_BUTTON_TXT );
	exitButton.setToolTipText( EXIT_TOOL_TIP );
	exitButton.setName(EXIT_BUTTON_TXT);
	exitButton.setOpaque( true );
	exitButton.setBackground( Color.RED );
	exitButton.addActionListener( new AppServerAction() );
	panel.add( exitButton );
	panel.setBorder( BorderFactory.createLineBorder( Color.BLUE ) );
	mainFrame.add( panel, BorderLayout.SOUTH );
    }

    /**
     * This method adds the status panel to the application server GUI. The
     * panel displays the status of the server and instructions on what actions
     * could be taken based on the current status of the server.
     */
    private void addStatusPanel() {
	JPanel panel = new JPanel(new BorderLayout());
	statusLabel = new JLabel();
	statusLabel.setHorizontalAlignment( SwingConstants.CENTER );
	statusLabel.setBorder(
	       BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	statusLabel.setFont( new Font(Font.DIALOG, Font.ITALIC, 12) );
	statusLabel.setBackground( Color.ORANGE );
	statusLabel.setText(  START_SERVER_INSTR  );
	panel.add( statusLabel, BorderLayout.CENTER );
	mainFrame.add( panel, BorderLayout.CENTER );
    }
}
