/*
 * BrokerSpecPanel.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client.gui;

import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.common.AppConfigManager;
import suncertify.common.AppConfigParam;
import suncertify.common.AppConfigException;

import suncertify.server.ContractorRecord;

import static suncertify.db.DatabaseMetaData.*;

/**
 * The <code>BrokerSpecPanel</code> is a subclass of <code>JPanel</code> and it 
 * displays the specialities values in checkboxes in the <code> 
 * BrokerClientDialog</code> class. 
 * 
 * @see javax.swing.JPanel
 * @see suncertify.client.gui.BrokerClientDialog
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerSpecPanel extends JPanel {

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * This value specifies the number of columns each row of <code>JCheckBox
     * </code> created in the constructor for this class. 
     *
     * @see #BrokerSpecPanel(ContractorRecord, ClientDialogMode)
     */
    private static final int NUM_OF_COLS = 3;
    
    /**
     * This is an array of <code>String</code> objects with each element 
     * denoting a speciality value. This array is constructed in the static
     * initializer.
     */
    private static final String [] SPEC_VALUES;

    /**
     * The static initializer reads the string denoting all the speciality
     * values supported by the application from the properties file.
     * It then creates an array of <code>String</code> with each element of the
     * array representing a speciality.  
     *
     * @see #SPEC_VALUES
     * @see suncertify.common.AppConfigParam
     * @see suncertify.common.AppConfigManager
     */
     static {
	 String allSpecs = null;
	 try {
	     AppConfigManager configMgr = AppConfigManager.getInstance();		
	     allSpecs = configMgr.get( AppConfigParam.SPECIALITIES );
	 } catch( AppConfigException exc ) {
	     Logger logger =
		 Logger.getLogger("suncertify.client.gui.BrokerSpecPanel");
	     logger.log( Level.SEVERE,
			 "There was an error reading the specialities values " +
			 "from the properties file - " + exc.getMessage() );
	 }

	 SPEC_VALUES = allSpecs.split(",");
     }

    /** 
     * This is a reference to an array of <code>String</code> objects denoting
     * a contractor record. The array element denoting the SPECIALITIES 
     * attribute are the values displayed in this panel. 
     *
     * @see #BrokerSpecPanel(ContractorRecord, ClientDialogMode)
     */
    ContractorRecord inputRecord;
    
    /**
     * This is a reference to a two dimensional array of <code>JCheckBox</code>
     * with each element denoting a speciality value.
     * 
     * @see javax.swing.JCheckBox
     * @see #BrokerSpecPanel(ContractorRecord, ClientDialogMode)
     */	
    private JCheckBox [][] checkBoxes;

    /**
     * The <code>SpecItemListener</code> class implements the ItemListener 
     * interface. This is the callback for the selection and deselection in 
     * the JCheckBoxes used in displaying the "specialities" attribute.
     */
    private class SpecItemListener implements ItemListener {

	/**
	 * This method determines which of the JCheckBox items were selected 
	 * and creates a ", " delimited list of the Strings associated with each
	 * item to create a single String. The created String is then assigned to
	 * the "specialities" attribute of the record.
	 *  
	 * @param event This is a reference to an ItemEvent object providing
	 *              the event data.
	 */
	public void itemStateChanged( ItemEvent event ) {
	    
	    String values = "";
	    
	    for ( JCheckBox[] boxArr : checkBoxes ) {
		for( JCheckBox cBox : boxArr ) {
		    if ( cBox != null ) {
			if ( cBox.isSelected() ) {
			    
			    if ( values.length() > 0 ) {
				values += ", ";
			    }
			    values += cBox.getText();   
			}
		    }
		}	    
	    }

	    inputRecord.setSpecialities(values);
	}
    }
    
    /**
     * The constructor for the <code>BrokerSpecPanel</code> class constructs
     * a <code>JCheckBox</code> component for each speciality available in the
     * application. These checkboxes are arranged in rows with each row having 
     * the number of columns defined in the constant <code>NUM_OF_COLS</code>.
     * The number of rows is dynamically calculated based on the number of 
     * columns specified.
     *
     * <p> The value associated with each checkbox is defined in the application 
     * properties file and these values are modifiable when the client 
     * application is started. 
     * 
     * @see #NUM_OF_COLS
     *
     * @param inputData The reference to the ContractorRecord object.
     * 
     * @param mode This specifies the dialog mode, the value specifies whether 
     * the checkboxes created should be activated or not. 
     *
     * @see suncertify.client.gui.ClientDialogMode
     * @see javax.swing.JCheckBox
     */
    public BrokerSpecPanel(ContractorRecord inputData, ClientDialogMode mode) {

	inputRecord = inputData;

	String chosenValues = inputData.getSpecialities();
	
	int rows = SPEC_VALUES.length / NUM_OF_COLS;

	if ( (SPEC_VALUES.length % NUM_OF_COLS) > 0 ) {
	    rows++;
	}
	
	this.setLayout( new GridLayout( rows, NUM_OF_COLS ) );

	checkBoxes = new JCheckBox[rows][NUM_OF_COLS]; 

	int currChoice = 0;

	outerLoop:
	for ( int i = 0; i < rows; ++i ) {
	    for (int j = 0; j < NUM_OF_COLS; ++j ) {
		if ( currChoice < SPEC_VALUES.length ) {
		    String currVal = SPEC_VALUES[currChoice].trim();
		    checkBoxes[i][j] = new JCheckBox( currVal );

		    this.add( checkBoxes[i][j] );

		    if ( ( chosenValues != null ) &&
			 ( chosenValues.contains( currVal ) ) ) {
			checkBoxes[i][j].setSelected( true );
		    }

		    switch( mode ) {
		    case BOOK:
		    case DELETE:
		    case RELEASE:
			checkBoxes[i][j].setEnabled(false);
		    }

		    checkBoxes[i][j].addItemListener( new SpecItemListener() );
		    ++currChoice;
		    
		} else {		    
		    break outerLoop;
		}
	    }
	}

	this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }
}
