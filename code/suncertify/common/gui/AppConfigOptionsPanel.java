/*
 * AppConfigOptionsPanel.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.common.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.Box;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.io.File;
import java.io.IOException;

import java.util.logging.Logger;
import java.util.logging.Level;


import suncertify.common.AppConfigManager;
import suncertify.common.AppConfigParam;
import suncertify.common.AppConfigException;
import suncertify.common.AppRunMode;

/**
 * The AppConfigOptionsPanel class implements the configurable application 
 * configuration data display panel. The components displayed on the panel 
 * depends on the run mode specified in the constructor for the class.
 * 
 * @author Augustine Ogundimu
 * @since 1.0
 * @version 1.0
 */
public class AppConfigOptionsPanel extends JPanel {

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Reference to a AppConfigManager object.
     *
     * @see suncertify.common.AppConfigManager
     */
    private AppConfigManager configMgr;

    /**
     * Label for the "database file name" component in this panel.
     *
     * @see javax.swing.JLabel
     */
    private JLabel dbFileNameLabel;

    /**
     * The text field used in displaying the database file name in the panel.
     *
     * @see javax.swing.JTextField
     */
    private JTextField dbFileNameField;

    /**
     *  The text of the label for the database name field. 
     */
    private static final String DB_FILE_NAME_LABEL = "Database File Name: ";

    /**
     * The text that is displayed when the database name text field has mouse
     * input focus.
     */
    private static final String DB_FILE_NAME_TIP =
	                   "This is the name of the database file";

    /**
     * A push button that results in the display of the JFileChooser
     * dialog for the database file name.
     */
    private JButton fileSelButton;

    /**
     * A String object denoting the database file name. It is either the value
     * read from the configuration file or the latest value entered in the 
     * database file name text field.
     */
    private String dbFileName;
    
    /**
     * The label for the server host name text field.
     */
    private JLabel serverHostNameLabel;

    /**
     * The text field for the server host name field.
     * @see javax.swing.JTextField
     */
    private JTextField serverHostNameField;

    /**
     * The text for the server host name label.
     */
    private static final String SERVER_HOST_NAME_LABEL = "Server Host Name: ";

    /**
     * The text displayed when the server host name text field has mouse input
     * focus.
     */
    private static final String SERVER_HOST_NAME_TIP =
	"This is the hostname or IP address of the application server machine";

    /**
     * The server host name value. It is either the value
     * read from the configuration file or the latest value entered in the 
     * server host name text field.
     */
    private String serverHostName;

    /**
     * The label for the server name text field.
     * @see javax.swing.JLabel
     */
    private JLabel serverNameLabel;

    /**
     * The text field for the server name.
     * 
     * @see javax.swing.JTextField 
     */
    private JTextField serverNameField;

    /**
     * The text for the server name label.
     */
    private static final String SERVER_NAME_LABEL = "Server Name: ";

    /**
     * The text that is displayed when the server name text field has mouse 
     * input focus.
     */
    private static final String SERVER_NAME_TIP =
	                     "This is the name of the remote server";

    /**
     * The server name. It is either the value read from the configuration file 
     * or the latest value entered in the server name text field.
     */
    private String serverName;
    
    /**
     * The server port field label.
     *
     * @see javax.swing.JLabel
     */
    private JLabel serverPortLabel;

    /**
     * The server port number text field.
     *
     * @see javax.swing.JTextField
     */
    private JTextField serverPortField;

    /**
     * The server port number label text.
     */
    private static final String SERVER_PORT_LABEL = "Server Port Number:";

    /**
     * The text displayed when the server port text field has mouse input focus.
     */
    private static final String SERVER_PORT_TIP =
	     "This is port number at which the server listens for requests";

    /**
     * The server port number value. It is either the value read from the 
     * configuration file or the latest value entered in the server port number 
     * text field.
     */
    private String serverPortNumber;

    /**
     * This is the label for the supported specialities field.
     * @see javax.swing.JLabel
     */
    private JLabel specLabel;
    
    /**
     * The text field for the supported specialities.
     */
    private JTextArea specField;

    /**
     * The text for the specialities field label.
     */
    private static final String SPEC_LABEL = "Specialities: ";

    /**
     * The text that is displayed when the specialities text area has mouse
     * input focus.
     */
    private static final String SPEC_TIP =
	       "This is the list of specialites offered by contractors";

    /** 
     * The application supported specialities. It is either the value read 
     * from the configuration file or the latest value entered in the 
     * application supported specialities text field.
     */
    private String specialities;

    /**
     * This text is displayed on the instructions label in the panel.
     */
    private final static String START_CLIENT_INSTR =
	"Enter the requested parameters and push OKAY"+
	             " to run the application or EXIT to terminate";
    
    /**
     * The DBFileFilter class is used as the filter in the JFileChooser component
     * for the selection of file names for the database file name configuration 
     * parameter.
     *
     * @see javax.swing.filechooser.FileFilter
     */
    private class DBFileFilter extends FileFilter {

	/**
	 * This method provides verification of the acceptability of the 
	 * parameter object as a file or not. 
	 *
	 * @param file A reference to a File object
	 * 
	 * @return boolean value true or false depending on whether the input
	 *         argument is a file or not.
	 */
	@Override
	public boolean accept( File file ) {
	    return file.isFile();
	}

	/**
	 * Method to get the description of this filter.
	 * @return The description of this filter.
	 */
	@Override
	public String getDescription() {
	    return "";
	}
    }

    /**
     * The ConfigActions class implements the callback for all the actions in 
     * the components used in this panel.
     *
     * @see java.awt.event.ActionListener
     * @see java.awt.event.FocusListener
     */
    private class ConfigActions implements ActionListener, FocusListener {

	/**
	 * This is the action for the activation of the "fileSelButton" in 
	 * this panel. It presents a JFileChooser dialog for file name selection
	 * for the 
	 *
	 * @param ev A reference to an ActionEvent object which provides the
	 *           event data.
	 */
	public void actionPerformed(ActionEvent ev) throws AppConfigException {
	    
	    JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));

	    fc.addChoosableFileFilter( new DBFileFilter() );
	    
	    if ( JFileChooser.APPROVE_OPTION == fc.showOpenDialog(null) ) {

		String text = fc.getSelectedFile().toString().trim();

		if ( ! text.equals( dbFileName ) ) {

		    dbFileName = text;
		    
		    dbFileNameField.setText( dbFileName );
		
		    configMgr.set(AppConfigParam.DB_FILE_NAME, dbFileName);
		}
	    }
	}

	/**
	 * This is the callback for focus lost events for all the text components
	 * used in this panel. The method determines the origin of the event and 
	 * depending on whether a new value is present or not, it saves the new 
	 * value.
	 * 
	 * @param event A reference to a FocusEvent object providing data about
	 *              the event being reported.
	 *
	 * @see java.awt.event.FocusEvent
	 */
	public void focusLost( FocusEvent event ) throws AppConfigException {

	    String compName = event.getComponent().getName();

	    if ( compName.equals( DB_FILE_NAME_LABEL ) ) {
		
		String text = dbFileNameField.getText().trim();
		
		if (! text.equals( dbFileName ) ) {
		    
		    dbFileName = text;
		    
		    configMgr.set(AppConfigParam.DB_FILE_NAME, dbFileName);
		}
		
	    } else if ( compName.equals( SERVER_NAME_LABEL ) ) {
		
		String text = serverNameField.getText().trim();
		
		if (! text.equals( serverName ) ) {
		    
		    serverName = text;
		    
		    configMgr.set(AppConfigParam.SERVER_NAME, serverName);
		}
		
	    } else if ( compName.equals( SERVER_HOST_NAME_LABEL ) ) {
		
		String text = serverHostNameField.getText().trim();
		
		if (! text.equals( serverHostName ) ) {
		    
		    serverHostName = text;
		    
		    configMgr.set( AppConfigParam.SERVER_HOST_NAME,
				   serverHostName );
		}
		
	    } else if ( compName.equals( SERVER_PORT_LABEL ) ) {
		
		String text = serverPortField.getText().trim();
		
		if (! text.equals( serverPortNumber ) ) {
		    		    
		    serverPortNumber = text;
			
		    configMgr.set(AppConfigParam.SERVER_PORT_NUMBER,
				  serverPortNumber);
			
		    }
	    } else if ( compName.equals( SPEC_LABEL ) ) {

		String text = specField.getText().trim();

		if ( !text.equals( specialities ) ) {
		    
		    specialities = text;
		    
		    configMgr.set(AppConfigParam.SPECIALITIES,
				  specialities);
		}
	    }
	}
	
	/**
	 * Call back for focus gained events. No action is being taken for this 
	 * event, the method is added to satisfy the FocusListener interface 
	 * implementation.
	 * 
	 * @param event Reference to a FocusEvent object.
	 */
	public void focusGained( FocusEvent event) {
	    
	}
    }


    /**
     * All the components that constitute this panel are constructed here. The
     * components constructed depend on the application mode specified in the 
     * argument to the constructor.
     *
     * <ul>
     * <li> When the run mode is AppRunMode.STAND_ALONE - The "Database Name", 
     * "Specialities", and Instruction components are displayed.
     * 
     * <li> When the run mode is AppRunMode.SERVER - The "Database Name", "Server
     * Name", and "Server Port Number" components are displayed.
     *
     * <li> When the run mode is AppRunMode.NETWORK_CLIENT - The "Server Host
     * name", "Server Name", "Specialities" and Instruction components are 
     * displayed.
     *</ul>
     *
     * @param mode The application run mode.
     *
     * @throws AppConfigException If an error is encountered during the 
     *         construction process. 
     *
     * @see suncertify.common.AppRunMode
     */    
    public AppConfigOptionsPanel( AppRunMode mode ) throws AppConfigException { 

	configMgr = AppConfigManager.getInstance();
	
	dbFileName = configMgr.get( AppConfigParam.DB_FILE_NAME );
	
	serverHostName = configMgr.get( AppConfigParam.SERVER_HOST_NAME );
	
	serverName = configMgr.get( AppConfigParam.SERVER_NAME );
	
	serverPortNumber = configMgr.get(AppConfigParam.SERVER_PORT_NUMBER);
	
	specialities = configMgr.get(AppConfigParam.SPECIALITIES );	    
	
	GridBagLayout gridBag = new GridBagLayout();
	
	this.setLayout( gridBag );

	switch( mode ) {
	case STAND_ALONE:
	    addDBNameComponents( gridBag );
	    addSpecialitiesComponents( gridBag );	    
	    addInstructionPanel( gridBag );
	    break;
	case SERVER:
	    addDBNameComponents( gridBag );
	    addServerNameComponents( gridBag );
	    addServerPortComponents( gridBag );
	    break;
	case NETWORK_CLIENT:
	    addServerHostComponents( gridBag );
	    addServerNameComponents(gridBag );
	    addSpecialitiesComponents( gridBag );
	    addInstructionPanel( gridBag );
	}	
    }

    /**
     * This method adds the "database name" components to the panel. This 
     * includes the database name label and text field.
     *
     * @param gridBag A reference to the GridBagLayout object used in this
     * panel as layout manager.
     * 
     * @see #AppConfigOptionsPanel(AppRunMode)
     */
    private void addDBNameComponents(GridBagLayout gridBag) {

	GridBagConstraints constraints = new GridBagConstraints();
	constraints.insets = new Insets(20, 5, 20, 5);
	constraints.anchor = GridBagConstraints.EAST;
	dbFileNameLabel = new JLabel( DB_FILE_NAME_LABEL );
	gridBag.setConstraints( dbFileNameLabel, constraints );
	this.add( dbFileNameLabel );
	
	dbFileNameField = new JTextField( 30 );
	dbFileNameField.setToolTipText( DB_FILE_NAME_TIP );
	dbFileNameField.addFocusListener( new ConfigActions() );
	dbFileNameField.setName( DB_FILE_NAME_LABEL );

	constraints = new GridBagConstraints();
	constraints.insets = new Insets(20, 0, 20, 5);
	
	dbFileNameField.setText( dbFileName );

	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.weightx = 1;
	constraints.gridwidth = GridBagConstraints.RELATIVE;
	gridBag.setConstraints( dbFileNameField, constraints );
	this.add( dbFileNameField );     		

	constraints = new GridBagConstraints();
	constraints.insets = new Insets(20, 0, 20, 5);
	constraints.weightx = 0;
	fileSelButton = new JButton("...");
	fileSelButton.addActionListener( new ConfigActions() );
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	gridBag.setConstraints( fileSelButton, constraints );
	this.add( fileSelButton );
    }

    /**
     * This method adds the "server host name" components to the panel. This
     * includes the server host name label and text field.
     *
     * @param gridBag ridBag A reference to the GridBagLayout object used in this
     * panel as layout manager.
     * 
     * @see #AppConfigOptionsPanel(AppRunMode)
     */
    private void addServerHostComponents(GridBagLayout gridBag ) {
	GridBagConstraints constraints = new GridBagConstraints();
	constraints.insets = new Insets(10, 5, 10, 5);
	serverHostNameLabel = new JLabel( SERVER_HOST_NAME_LABEL );
	constraints.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( serverHostNameLabel, constraints );
	this.add( serverHostNameLabel );

	serverHostNameField = new JTextField( 20 );
	serverHostNameField.setToolTipText( SERVER_HOST_NAME_TIP );
	serverHostNameField.setText( serverHostName );
	serverHostNameField.addFocusListener( new ConfigActions() );
	serverHostNameField.setName( SERVER_HOST_NAME_LABEL );

	constraints = new GridBagConstraints();
	constraints.insets = new Insets(10, 0, 10, 5);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.WEST;
	constraints.gridwidth = GridBagConstraints.RELATIVE;
	gridBag.setConstraints( serverHostNameField, constraints );
	this.add( serverHostNameField );

	constraints = new GridBagConstraints();
	constraints.insets = new Insets(20, 0, 20, 5);
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.weightx = 1;
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	Component glue = Box.createGlue();
	gridBag.setConstraints( glue, constraints );
	this.add( glue );
    }

    /**
     * This adds the "server name" components to the panel. This includes
     * the server name label and text field.
     *
     * @param gridBag A reference to the GridBagLayout object used in this
     * panel as layout manager.
     * 
     * @see #AppConfigOptionsPanel(AppRunMode)
     */
    private void addServerNameComponents(GridBagLayout gridBag ) {

	GridBagConstraints constraints = new GridBagConstraints();
	constraints.insets = new Insets(10, 5, 10, 5);
	serverNameLabel = new JLabel( SERVER_NAME_LABEL);
	constraints.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( serverNameLabel, constraints );
	this.add( serverNameLabel );

	serverNameField = new JTextField( 20 );
	serverNameField.setToolTipText( SERVER_NAME_TIP );
	serverNameField.setText( serverName );
	serverNameField.addFocusListener( new ConfigActions() );
	serverNameField.setName( SERVER_NAME_LABEL );

	constraints = new GridBagConstraints();
	constraints.insets = new Insets(10, 0, 10, 5);
	constraints.fill = GridBagConstraints.NONE;
	constraints.anchor = GridBagConstraints.WEST;
	constraints.gridwidth = GridBagConstraints.RELATIVE;
	gridBag.setConstraints( serverNameField, constraints );
	this.add( serverNameField );
	
	constraints = new GridBagConstraints();
	constraints.insets =  new Insets(20, 0, 20, 5);
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.weightx = 1;
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	Component glue = Box.createGlue();
	gridBag.setConstraints( glue, constraints );
	this.add( glue );	
    }

    /**
     * This method adds the "server port number" components to this panel. This
     * includes the server port number label and text field.
     *
     * @param gridBag A reference to the GridBagLayout object used in this
     * panel as layout manager.
     * 
     * @see #AppConfigOptionsPanel(AppRunMode)
     */
    private void addServerPortComponents(GridBagLayout gridBag ) {

	GridBagConstraints constraints = new GridBagConstraints();
	constraints.insets = new Insets(10, 5, 10, 5);
	serverPortLabel = new JLabel( SERVER_PORT_LABEL );
	constraints.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( serverPortLabel, constraints );
	this.add( serverPortLabel );
	
	serverPortField = new JTextField( 6 );
	serverPortField.setToolTipText( SERVER_PORT_TIP );
	serverPortField.setText( serverPortNumber );
	serverPortField.addFocusListener( new ConfigActions() );
	serverPortField.setName( SERVER_PORT_LABEL );

	constraints = new GridBagConstraints();
	constraints.insets = new Insets(10, 0, 10,5);
	constraints.anchor = GridBagConstraints.WEST;
	constraints.fill = GridBagConstraints.NONE;
	constraints.weightx = 0;
	constraints.gridwidth = GridBagConstraints.RELATIVE;
	gridBag.setConstraints( serverPortField, constraints );
	this.add( serverPortField );

	constraints = new GridBagConstraints();
	constraints.insets = new Insets(20, 0, 20, 5);
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.weightx = 1;
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	Component glue = Box.createGlue();
	gridBag.setConstraints( glue, constraints );
	this.add( glue );
    }

    /**
     * This method adds the specialities component to the panel. This includes
     * the specialities label and the text area.
     *
     * @param gridBag A reference to the GridBagLayout object used in this
     * panel as layout manager.
     * 
     * @see #AppConfigOptionsPanel(AppRunMode)
     */
    private void addSpecialitiesComponents(GridBagLayout gridBag ) {

	GridBagConstraints constraints = new GridBagConstraints();
	constraints.insets = new Insets(10, 5, 10, 5);
	specLabel = new JLabel( SPEC_LABEL );
	constraints.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( specLabel, constraints );
	this.add( specLabel );
	
	specField = new JTextArea( specialities, 3, 1 );
	specField.setWrapStyleWord( true );
	specField.setLineWrap( true );

	specField.setBorder( BorderFactory.createEtchedBorder() );
	
	specField.setToolTipText( SPEC_TIP );
	specField.addFocusListener( new ConfigActions() );
	specField.setName( SPEC_LABEL );

	constraints = new GridBagConstraints();
	constraints.insets = new Insets(10, 0, 30, 50);
	constraints.anchor = GridBagConstraints.WEST;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.weightx = 0.5;
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	gridBag.setConstraints( specField, constraints );

	this.add( specField );

    }

    /**
     * This method adds the instructions panel to the panel. This is just
     * a label giving directions on what to do.
     * 
     * @param gridBag A reference to the GridBagLayout object used in this
     * panel as layout manager.
     * 
     * @see #AppConfigOptionsPanel(AppRunMode)
     */
    private void addInstructionPanel(GridBagLayout gridBag) {

	JPanel panel = new JPanel(new BorderLayout());
	JLabel statusLabel = new JLabel();
	statusLabel.setBorder(
	       BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	statusLabel.setFont( new Font(Font.DIALOG, Font.ITALIC, 12) );
	statusLabel.setBackground( Color.ORANGE );
	statusLabel.setText(  START_CLIENT_INSTR  );
	panel.add( statusLabel, BorderLayout.CENTER );	

	GridBagConstraints constraints = new GridBagConstraints();
	constraints.insets = new Insets(0, 0, 20, 0);			   
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	gridBag.setConstraints( panel, constraints );
	this.add( panel );
    }
    
    /**
     * This method disables all the components displayed on this panel. It
     * prevents modification of the values displayed in the components.
     */
    public void disableComponents() {

	if( dbFileNameField != null ) {
	    dbFileNameField.setEnabled(false);
	}
	
	if ( serverHostNameField != null ) {
	    serverHostNameField.setEnabled(false);
	}

	if ( serverNameField != null ) {
	    serverNameField.setEnabled(false);
	}

	if ( serverPortField != null ) {
	    serverPortField.setEnabled(false);
	}

	if ( fileSelButton != null ) {
	    fileSelButton.setEnabled(false);
	}
	
	if ( specField != null ) {
	    specField.setEnabled(false);
	}
    }

    /**
     * This method enables all the components displayed on this panel. It
     * enables the modification of the values displayed in the components.
     */
    public void enableComponents() {

	if ( dbFileNameField != null ) {
	    dbFileNameField.setEnabled( true );
	}

	if ( serverHostNameField != null ) {
	    serverHostNameField.setEnabled( true );
	}

	if ( serverNameField != null ) {
	    serverNameField.setEnabled( true );
	}

	if ( serverPortField != null ) {
	    serverPortField.setEnabled( true );
	}

	if ( fileSelButton != null ) {
	    fileSelButton.setEnabled( true );
	}

	if ( specField != null ) {
	    specField.setEnabled( true );
	}
    }
}
