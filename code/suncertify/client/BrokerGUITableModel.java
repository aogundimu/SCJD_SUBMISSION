/*
 * BrokerGUITableModel.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */
package suncertify.client;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;
import java.util.logging.Level;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import static suncertify.db.DatabaseMetaData.*;

/**
 * The <code>BrokerGUITableModel</code> is the model used in the 
 * <code>JTable</code> used in displaying the results of queries in the 
 * application main GUI. It extends <code>DefaultTableModel</code> class.
 * 
 * @see suncertify.client.gui.BrokerAppClientGUI
 * @see javax.swing.table.DefaultTableModel
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class BrokerGUITableModel extends DefaultTableModel {

    /**
     * This is a reference to a <code>Logger</code> object. The logger's name 
     * is the fully qualitified name for this class. 
     *
     * @see java.util.logging.Logger
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * This is an array of <code>String</code> objects denoting the column
     * names for the data displayed in the <code>JTable</code> in the 
     * application main GUI.
     */
    private String [] columnHeaders = { "NAME", "LOCATION",
	                              "SPECIALITIES", "SIZE", "RATE",
	                              "OWNER" };
    
    /**
     * The constructor. It creates a <code>TableColumn</code> instance for each
     * of the columns and adds it to this table model.
     * 
     */
    public BrokerGUITableModel() {

	for ( int i = 0; i < columnHeaders.length; ++i ) {	    
	    TableColumn tc = new TableColumn();	    
	    tc.setHeaderValue( columnHeaders[i] );
	    this.addColumn(tc);
	}
    }

    /**
     * This method returns a <code>String</code> denoting the name of the column
     * corresponding to the index parameter provided. 
     *
     * @see #columnHeaders
     *
     * @param col An integer value denoting the column index
     *
     * @return A reference to a String object denoting the column name
     */
    public String getColumnName( int col ) {

	return columnHeaders[ col ];
    }

    /**
     * This method overrides the same name method in the baseclass. It always
     * returns false ensuring that cells in the table are not modifiable.
     *
     * @param row The row index for the cell
     * @param column The column index for the cell
     *
     * @return A boolean value indicating whether the cell is editable or not.
     */
    @Override
    public boolean isCellEditable( int row, int column ) {
	return false;
    }
}
