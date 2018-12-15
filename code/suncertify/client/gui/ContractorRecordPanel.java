/*
 * ContractorRecordPanel.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.client.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JComponent;
import javax.swing.InputVerifier;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.DecimalFormat;

import java.math.RoundingMode;

import javax.swing.text.NumberFormatter;
import javax.swing.text.DefaultFormatterFactory;

import java.util.Locale;

import java.util.logging.Logger;
import java.util.logging.Level;

import suncertify.server.ContractorRecord;

import static suncertify.db.DatabaseMetaData.*;

/**
 * The ContractorRecordPanel class is used by the BrokerClientDialog for 
 * displaying the attributes of a record that is the subject of the dialog.
 *
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class ContractorRecordPanel extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 20121212127775L;

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * The text denoting the name field label.
     */
    private final static String NAME_LABEL = "Name";

    /**
     * A reference to a JLabel object labelling the "name" field;
     */
    private JLabel nameLabel;

    /**
     * A reference to a JTextField object for editing the "name" field.
     */
    private JTextField nameField;

    /**
     * The text that is displayed when the "name" JTextField has the mouse focus.
     */
    private static final String NAME_TIP = "Enter the exact name of the contractor";

    /**
     * The text denoting the location field label.
     */
    private final static String LOCATION_LABEL = "Location";

    /**
     * A reference to a JLabel object labelling the "location" field;
     */
    private JLabel locationLabel;

    /**
     * A reference to a JTextField object for editing the "Location" field.
     */
    private JTextField locationField;

    /**
     * The text that is displayed when the "location" JTextField has the mouse 
     * focus.
     */
    private static final String LOCATION_TIP =
	                  "Enter the location exactly as it is spelled";
 
    /**
     * The text denoting the specialities field label.
     */
    private final static String SPECIALITIES_LABEL = "Specialities";

    /**
     * A reference to a JLabel object labelling the "specialities" field;
     */
    private JLabel specialitiesLabel;
    
    /**
     * The text denoting the size field label.
     */
    private final static String SIZE_LABEL = "Size";

    /**
     * A reference to a JLabel object labelling the "size" field;
     */
    private JLabel sizeLabel;

    /**
     * A reference to a JFormattedTextField object for editing the "Location" 
     * field.
     */
    private JFormattedTextField sizeField;

    /**
     * The text that is displayed when the "size" JFormattedTextField has the 
     * mouse focus.
     */
    private static final String SIZE_TIP = "Enter the size for this contractor";

    /**
     * The text denoting the rate field label.
     */
    private final static String RATE_LABEL = "Rate";
      
    /**
     * A reference to a JLabel object labelling the "rate" field;
     */
    private JLabel rateLabel;

    /**
     * A reference to a JFormattedTextField object for editing the "rate" field.
     */
    private JFormattedTextField rateField;

    /**
     * The text that is displayed when the "rate" JFormattedTextField has the 
     * mouse focus.
     */
    private static final String RATE_TIP =
	                     "Enter the hourly rate for this contractor";

    /**
     * The text denoting the owner field label.
     */
    private final static String OWNER_LABEL = "Owner";

    /**
     * A reference to a JLabel object labelling the "owner" field;
     */
    private JLabel ownerLabel;

    /**
     * A reference to a JTextField object for editing the "owner" field.
     */
    private JTextField ownerField;

    /**
     * The text that is displayed when the "owner" JTextField has the 
     * mouse focus.
     */
    private static final String OWNER_TIP = "Enter the 8 digit customer number";

    /**
     * A reference to an array of String objects denoting the attributes of the
     * record being displayed in this panel.
     */
    private ContractorRecord inputRecord;

    /**
     * The BrokerInputVerifier class provides the input verification
     * functionality for fields that use the JFormattedTextField for text 
     * editing in this panel.
     *
     * @see #addRateComponents(GridBagLayout)
     * @see #addSizeComponents(GridBagLayout)
     */
    private class BrokerInputVerifier extends InputVerifier {

	/**
	 * Indicates whether the JFormattedText field argument should yield
	 * focus or not.  
	 *
	 * @param txtField A reference to a JFormattedTextField
	 * 
	 * @return boolean value true if the content of the text field argument
	 *         is valid, false otherwise.
	 */
	public boolean shouldYieldFocus(JComponent txtField) {
	    boolean valid = verify( txtField );
            if( valid) {
                return true;
            } else {
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
        }

	/**
	 * Performs the verification of the value in a JFormattedTextField
	 * depending on the formatter object used in its construction.
	 *
	 * @param txtField A reference to a JFormattedTextField object.
	 *
	 * @return true if the value in the input text field or false otherwise.        
	 */
        public boolean verify(JComponent txtField) {
	    
            if ( ((JFormattedTextField)txtField).isEditValid() ) {
                return true;
            } else {
                return false;
            }
        }
    }
    
    /**
     * The DataEntryAction class implements the action for focus events for the
     * JTextField and JFormattedTextField on this panel. It implements the 
     * FocusListener interface.
     *
     * @see java.awt.event.FocusListener
     */
    private class DataEntryAction implements FocusListener {

	/**
	 * Performs the extraction of the value from the text field that has just
	 * lost focus and assigns the value to the contractor record.
	 * 
	 * @param event A reference to a FocusEvent object denoting the event
	 *              data.
	 */
	public void focusLost(FocusEvent event) {

	    String componentName = event.getComponent().getName();	    
	
	    if ( componentName.equals( NAME_LABEL ) ) {

		inputRecord.setName(nameField.getText());
		
	    } else if ( componentName.equals( LOCATION_LABEL ) ) {

		inputRecord.setLocation(locationField.getText());
				
	    } else if ( componentName.equals( SIZE_LABEL ) ) {

		inputRecord.setSize(sizeField.getText());
		
	    } else if ( componentName.equals( RATE_LABEL ) ) {

		String dispText = rateField.getText();

		if ( ( dispText != null ) && (dispText.trim().length() != 0)) {
		    DefaultFormatterFactory formatFac =
		      (DefaultFormatterFactory)rateField.getFormatterFactory();
		    JFormattedTextField.AbstractFormatter formatter =
			                       formatFac.getDisplayFormatter();
		    
		    try {
			inputRecord.setRate( formatter.valueToString( 
					   Float.parseFloat(dispText)) );
		    		    
		    } catch( ParseException ex ) {
			logger.log( Level.WARNING,
				    "Invalid value entered in the rate field, " +
				    "value = " + dispText );
		    }
		}		
	    } else if ( componentName.equals( OWNER_LABEL ) ) {
		inputRecord.setOwner( ownerField.getText() );		
	    }	    
	}

	/**
	 * No action is taken on "Focus Gained" events. This method was 
	 * implemented to satisfy the implementation of the FocusListener
	 * interface.
	 *
	 * @param event A reference to a FocusEvent object.
	 */
	public void focusGained( FocusEvent event ) {

	}
    }    
    
    /**
     * The constructor. All the components constituting this panel are 
     * constructed and configured in this constructor. It also enables the 
     * appropriate components depending on the client dialog mode.
     *
     * @param mode The mode for the parent BrokerClientDialog.
     *
     * @param record The reference to the ContractorRecord object that is the
     *        subject of this panel.
     * 
     * @see suncertify.client.gui.ClientDialogMode
     */
    ContractorRecordPanel( ClientDialogMode mode, ContractorRecord record ) {
		
	inputRecord = record;
	
	GridBagLayout gridBag = new GridBagLayout();

	this.setLayout( gridBag );
	
	addNameComponents( gridBag );

	addLocationComponents( gridBag );

	addSpecialitiesComponents( gridBag, mode );

	addSizeComponents( gridBag );

	addRateComponents( gridBag );

	addOwnerComponents( gridBag );

	switch( mode ) {
	case ADD:
	    ownerField.setEnabled( false );
	    break;
	case DELETE:
	    nameField.setEnabled( false );
	    ownerField.setEnabled( false );
	    locationField.setEnabled(false);
	    sizeField.setEnabled( false );
	    rateField.setEnabled( false );
	    break;
	case BOOK:
	case RELEASE:
	    nameField.setEnabled( false );
	    locationField.setEnabled(false);
	    sizeField.setEnabled( false );
	    rateField.setEnabled( false );
	    break;
	case SEARCH:
	    break;
	case UPDATE:
	    nameField.setEnabled( false );	    
	    locationField.setEnabled(false);
	    ownerField.setEnabled(false);
	}	
    }    

    /**
     * This method adds the label and text field components for the name
     * attribute to the component.
     *
     * @param gridBag A reference to a GridBagLayout object used by component.
     */
    private void addNameComponents(GridBagLayout gridBag) {
	
	nameLabel = new JLabel( NAME_LABEL );
	GridBagConstraints cons = new GridBagConstraints();
	cons.insets = new Insets(20, 10, 15, 5);
	cons.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( nameLabel, cons);
	this.add( nameLabel );

	nameField = new JTextField( 30 );
	nameField.setToolTipText( NAME_TIP );
	nameField.addFocusListener( new DataEntryAction() );
	nameField.setText( inputRecord.getName() );
	nameField.setName( NAME_LABEL );

	cons = new GridBagConstraints();
	cons.insets = new Insets(20, 0, 15, 15);
	cons.anchor = GridBagConstraints.WEST;
	cons.fill = GridBagConstraints.NONE;
	cons.gridwidth = GridBagConstraints.REMAINDER;
	gridBag.setConstraints( nameField, cons );
	this.add( nameField );
    }

    /**
     * This method adds the label and text field components for the location
     * attribute to the component.
     *
     * @param gridBag A reference to a GridBagLayout object used by component.
     */
    private void addLocationComponents(GridBagLayout gridBag) {

	locationLabel = new JLabel( LOCATION_LABEL );
	GridBagConstraints cons = new GridBagConstraints();
	cons.insets = new Insets(15, 10, 15, 5);
	cons.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( locationLabel, cons);
	this.add( locationLabel );

	locationField = new JTextField( 30 );
	locationField.setToolTipText( LOCATION_TIP );
	locationField.addFocusListener( new DataEntryAction() );
	locationField.setText( inputRecord.getLocation() );
	locationField.setName( LOCATION_LABEL );

	cons = new GridBagConstraints();
	cons.anchor = GridBagConstraints.WEST;
	cons.insets = new Insets(15, 0, 15, 15);
	cons.fill = GridBagConstraints.NONE;
	cons.gridwidth = GridBagConstraints.REMAINDER; 
	gridBag.setConstraints( locationField, cons );
	this.add( locationField );
    }


    /**
     * This method adds the label and text field components for the specialities
     * attribute to the component.
     *
     * @param gridBag A reference to a GridBagLayout object used by component.
     *
     * @param mode A reference to a ClientDialogMode object indicating the dialog
     *             mode.
     * 
     * @see suncertify.client.gui.ClientDialogMode
     */
    private void addSpecialitiesComponents( GridBagLayout gridBag,
					          ClientDialogMode mode ) {
	
	specialitiesLabel = new JLabel( SPECIALITIES_LABEL );
	GridBagConstraints cons = new GridBagConstraints();
	cons.insets = new Insets(15, 10, 15, 5);
	cons.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( specialitiesLabel, cons);
	this.add( specialitiesLabel );
	 	
	BrokerSpecPanel bsp = new BrokerSpecPanel(inputRecord,mode);
						 
	cons = new GridBagConstraints();
	cons.insets = new Insets(15, 0, 15, 15);
	cons.fill = GridBagConstraints.NONE;
	cons.anchor = GridBagConstraints.WEST;
	cons.gridwidth = GridBagConstraints.REMAINDER;
	gridBag.setConstraints( bsp, cons );
	this.add( bsp );
    }

    /**
     * This method adds the label and text field components for the
     * contractor size attribute to the component.
     *
     * @param gridBag A reference to a GridBagLayout object used by component.
     */
    private void addSizeComponents(GridBagLayout gridBag) {
	
	sizeLabel = new JLabel( SIZE_LABEL );
	GridBagConstraints cons = new GridBagConstraints();
	cons.insets = new Insets(15, 10, 10, 5);
	cons.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( sizeLabel, cons);
	this.add( sizeLabel );

	NumberFormat format = NumberFormat.getNumberInstance();
	format.setMaximumFractionDigits( 0 );
	format.setMaximumIntegerDigits(MAX_SIZE_FIELD_LEN);
	format.setMinimumIntegerDigits(MIN_SIZE_FIELD_LEN);

	NumberFormatter formatter = new NumberFormatter( format );
	formatter.setMinimum(MIN_SIZE_VAL);
	formatter.setMaximum(MAX_SIZE_VAL);
	
	sizeField = new JFormattedTextField(
			     new DefaultFormatterFactory( formatter,
							  formatter,
							  formatter,
							  formatter)
					    );
		
	sizeField.setFocusLostBehavior( JFormattedTextField.COMMIT_OR_REVERT );
	sizeField.setColumns( 10 );
	
	sizeField.setToolTipText( SIZE_TIP );
	sizeField.addFocusListener( new DataEntryAction() );

	if ( inputRecord.getSize() != null ) {
	    sizeField.setValue( Integer.parseInt(inputRecord.getSize() ) );
	}

	sizeField.setInputVerifier( new BrokerInputVerifier() );
	sizeField.setName( SIZE_LABEL );

	cons = new GridBagConstraints();
	cons.insets = new Insets(15, 0, 10, 15);
	cons.anchor = GridBagConstraints.WEST;
	cons.fill = GridBagConstraints.NONE;
	cons.gridwidth = GridBagConstraints.REMAINDER;
	gridBag.setConstraints( sizeField, cons );
	this.add( sizeField );
    }

    /**
     * This method adds the label and text field components for the rate 
     * attribute to the component.
     *
     * @param gridBag A reference to a GridBagLayout object used by component.
     */
    private void addRateComponents(GridBagLayout gridBag) {

	rateLabel = new JLabel( RATE_LABEL );
	GridBagConstraints cons = new GridBagConstraints();
	cons.insets = new Insets(5, 10, 15, 5);
	cons.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( rateLabel, cons);
	this.add( rateLabel );

	NumberFormat dispFormat = NumberFormat.getCurrencyInstance(Locale.US);
	dispFormat.setMaximumFractionDigits( 2 );
	dispFormat.setMinimumFractionDigits( 2 );
	dispFormat.setRoundingMode( RoundingMode.DOWN );
	NumberFormatter dispFormatter = new NumberFormatter( dispFormat );
	dispFormatter.setMinimum( MIN_RATE_VAL );
	dispFormatter.setMaximum( MAX_RATE_VAL );
	
	NumberFormat editFormat = NumberFormat.getNumberInstance();
	editFormat.setMaximumFractionDigits(2);
	editFormat.setMinimumFractionDigits(2);
	editFormat.setRoundingMode( RoundingMode.DOWN );
	NumberFormatter editFormatter = new NumberFormatter( editFormat );
	editFormatter.setMinimum( MIN_RATE_VAL );
	editFormatter.setMaximum( MAX_RATE_VAL );

	rateField = new JFormattedTextField(
		      new DefaultFormatterFactory ( editFormatter,
						    dispFormatter,
						    editFormatter,
						    editFormatter )
					    );

	rateField.setFocusLostBehavior( JFormattedTextField.COMMIT_OR_REVERT );
	rateField.setColumns( 7 );
	rateField.setToolTipText( RATE_TIP );
	rateField.addFocusListener( new DataEntryAction() );	
	rateField.setName( RATE_LABEL );
	
	if ( inputRecord.getRate() != null ) {
	    rateField.setValue(
	      Float.parseFloat( inputRecord.getRate().substring(1) ) );
	}
	
	rateField.setInputVerifier( new BrokerInputVerifier() );
	
	cons = new GridBagConstraints();
	cons.insets = new Insets(5, 0, 15, 15);
	cons.anchor = GridBagConstraints.WEST;
	cons.fill = GridBagConstraints.NONE;
	cons.gridwidth = GridBagConstraints.REMAINDER;
	gridBag.setConstraints( rateField, cons );
	this.add( rateField );
    }

    /**
     * This method adds the label and text field components for the owner 
     * attribute to the component.
     *
     * @param gridBag A reference to a GridBagLayout object used by component.
     */
    private void addOwnerComponents(GridBagLayout gridBag) {
	
	ownerLabel = new JLabel( OWNER_LABEL );
	GridBagConstraints cons = new GridBagConstraints();
	cons.insets = new Insets(0, 10, 10, 5);
	cons.anchor = GridBagConstraints.EAST;
	gridBag.setConstraints( ownerLabel, cons);
	this.add( ownerLabel );

	ownerField = new JTextField( 15 );
	ownerField.setToolTipText( OWNER_TIP );
	ownerField.addFocusListener( new DataEntryAction() );
	ownerField.setText( inputRecord.getOwner() );
	ownerField.setName( OWNER_LABEL );

	cons = new GridBagConstraints();
	cons.insets = new Insets( 0, 0, 10, 15);
	cons.anchor = GridBagConstraints.WEST;
	cons.fill = GridBagConstraints.NONE;
	cons.gridwidth = GridBagConstraints.REMAINDER;
	gridBag.setConstraints( ownerField, cons );
	this.add( ownerField );
    }
}



