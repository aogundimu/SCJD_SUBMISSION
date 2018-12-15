/*
 * DBRecord.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.db;

import java.io.RandomAccessFile;
import java.io.IOException;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;

import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.server.ContractorRecord;

import static suncertify.db.DatabaseMetaData.*;

/**
 * The DBRecord class encapsulates a contractor record, it extends the
 * ContractorRecord.
 *
 * @see suncertify.server.ContractorRecord
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
class DBRecord extends ContractorRecord {

	/**
	 *
	 */
	private static final long serialVersionUID = 20120645454775L;

	/**
	 * This is a reference to a Logger object. The logger's name is the fully
	 * qualitified name for this class.
	 */
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * This is the value to which the flag of a record is set when the record is
	 * deleted.
	 */
	private final static short DELETED_REC_FLAG_VALUE = Short.MIN_VALUE;

	/**
	 * This is the value to which the flag of a record is set when the record is
	 * valid.
	 */
	private final static short VALID_REC_FLAG_VALUE = 0;

	/**
	 * An array of String objects denoting the attributes of this record.
	 */
	private String[] fieldsValues = null;

	/**
	 * This value indicates whether this record is deleted or not.
	 *
	 * @see #DELETED_REC_FLAG_VALUE
	 * @see #VALID_REC_FLAG_VALUE
	 */
	private short statusFlag;

	/**
	 * The constructor.
	 *
	 * @param fields     An array of String objects denoting the attributes of the
	 *                   record.
	 *
	 * @param dbMetaData A reference to a Map object denoting the schema data for a
	 *                   record in the database.
	 *
	 * @param recNo      The unique identifier for this record.
	 */
	DBRecord(String[] fields, Map<String, Short> dbMetaData, int recNo) {

		fieldsValues = new String[fields.length];
		statusFlag = VALID_REC_FLAG_VALUE;
		recordNumber = recNo;

		Collection<Short> fieldLengths = dbMetaData.values();

		Iterator<Short> iter = fieldLengths.iterator();

		for (int i = 0; i < fields.length; ++i) {

			String formatStr = "%" + "-" + iter.next().intValue() + "s";

			if (fields[i] == null) {
				fields[i] = "";
			}
			String value = fields[i].trim();

			fieldsValues[i] = String.format(formatStr, value);

			switch (i) {
			case NAME_IDX:
				name = fieldsValues[i];
				break;
			case LOCATION_IDX:
				location = fieldsValues[i];
				break;
			case SPECIALITIES_IDX:
				specialities = fieldsValues[i];
				break;
			case SIZE_IDX:
				size = fieldsValues[i];
				break;
			case RATE_IDX:
				rate = fieldsValues[i];
				break;
			case OWNER_IDX:
				owner = fieldsValues[i];
				break;
			}
		}
	}

	/**
	 * The constructor.
	 *
	 * @param buf      An array of bytes denoting the attributes of the new record.
	 *
	 * @param metaData A reference to a Map object denoting the schema data for a
	 *                 record in the database.
	 * 
	 * @param recNo    The unique identifier for this record.
	 *
	 * @param recFlag  The status flag for the record.
	 */
	DBRecord(byte[] buf, Map<String, Short> metaData, int recNo, short recFlag) {

		statusFlag = recFlag;
		recordNumber = recNo;

		int totalFields = metaData.size();

		fieldsValues = new String[totalFields];

		Collection<Short> values = metaData.values();

		int offset = 0;

		int i = 0;

		for (Short length : values) {

			fieldsValues[i] = new String(buf, offset, length.intValue());

			switch (i) {
			case NAME_IDX:
				name = fieldsValues[i];
				break;
			case LOCATION_IDX:
				location = fieldsValues[i];
				break;
			case SPECIALITIES_IDX:
				specialities = fieldsValues[i];
				break;
			case SIZE_IDX:
				size = fieldsValues[i];
				break;
			case RATE_IDX:
				rate = fieldsValues[i];
				break;
			case OWNER_IDX:
				owner = fieldsValues[i];
				break;
			default:

			}

			offset += length;
			++i;
		}
	}

	/**
	 * This method changes the status flag on the record to delete. It does not
	 * delete the record from the data file.
	 */
	void delete() {

		statusFlag = DELETED_REC_FLAG_VALUE;
	}

	/**
	 * This method undeletes a record by changing the status flag on the record to
	 * valid.
	 */
	void undelete() {

		statusFlag = VALID_REC_FLAG_VALUE;
	}

	/**
	 * This method assigns the attributes in the input parameter to the attributes
	 * of this record. The assigned values are padded with space so the attribute
	 * lengths match what is specified in the schema for each respective attribute.
	 *
	 * @param values     An array of String objects denoting the attributes of the
	 *                   record.
	 *
	 * @param dbMetaData A reference to a Map object providing schema information.
	 */
	void setFieldsValues(String[] values, Map<String, Short> dbMetaData) {

		Collection<Short> fieldSizes = dbMetaData.values();
		Object[] fieldSizeArray = fieldSizes.toArray();

		for (int i = SPECIALITIES_IDX; i < fieldsValues.length; ++i) {

			if (values[i] == null) {
				values[i] = "";
			}

			String storedValue = values[i].trim();

			String formatStr = "%" + "-" + ((Short) fieldSizeArray[i]).intValue() + "s";

			fieldsValues[i] = String.format(formatStr, storedValue);

			switch (i) {
			case SPECIALITIES_IDX:
				specialities = fieldsValues[i];
				break;
			case SIZE_IDX:
				size = fieldsValues[i];
				break;
			case RATE_IDX:
				rate = fieldsValues[i];
				break;
			case OWNER_IDX:
				owner = fieldsValues[i];
				break;
			}
		}
	}

	/**
	 * This method returns an array of String objects denoting the attributes of
	 * this record.
	 *
	 * @return A reference to an array of String objects.
	 */
	String[] getFieldsValues() {

		String[] values = new String[fieldsValues.length];

		for (int i = 0; i < fieldsValues.length; ++i) {

			values[i] = fieldsValues[i].trim();
		}

		return values;
	}

	/**
	 * This method writes the status flag of this record to the database file
	 * argument provided. The method assumes that the file pointer is at the right
	 * location in the data file for the status flag attribute of this record.
	 * 
	 * @param file A reference to a RandomAccessFile object to which the flag will
	 *             be written.
	 *
	 * @throws IOException If an error is encountered while writing to the data
	 *                     file.
	 */
	void writeFlagToFile(RandomAccessFile file) throws IOException {

		file.writeShort(statusFlag);
	}

	/**
	 * This method writes the attributes of this record to the database file
	 * argument provided. The method assumes that the file pointer is at the right
	 * location in the data file for this record.
	 *
	 * @param file A reference to a valid DataOutputStream object.
	 *
	 * @throws IOException If an error is encountered while writing to the data
	 *                     file.
	 */
	void writeToFile(RandomAccessFile file) throws IOException {

		file.writeShort(statusFlag);

		for (String val : fieldsValues) {

			file.writeBytes(val);
		}
	}

	/**
	 * This determines if the parameter String matches the name attribute of this
	 * record.
	 * 
	 * <ul>
	 * <li>A null value or spaces or zero length string in the criterion is
	 * considered a wild card.
	 * <li>The match ignores case. "John" will match "john".
	 * <li>It is considered a match if this record's name attribute starts with the
	 * text of the parameter.
	 * </ul>
	 * 
	 * @param value A reference to a String object denoting the criterion.
	 * 
	 * @return A boolean value true if the name matches and false otherwise.
	 */
	private boolean matchesName(String value) {

		if ((value == null) || (value.trim().length() == 0)) {
			return true;
		} else {
			return name.toUpperCase().startsWith(value.toUpperCase());
		}
	}

	/**
	 * This determines if the parameter String matches the location attribute of
	 * this record.
	 * 
	 * <ul>
	 * <li>A null value or spaces or zero length string in the criterion is
	 * considered a wild card.
	 * <li>The match ignores case. "BOSTON" will match "boston".
	 * <li>It is considered a match if this record's location attribute starts with
	 * the text of the parameter.
	 * </ul>
	 *
	 * @param value A String denoting the location criterion.
	 *
	 * @return boolean value true if there is a match, false otherwise.
	 */
	private boolean matchesLocation(String value) {

		if ((value == null) || (value.trim().length() == 0)) {
			return true;
		} else {
			return location.toUpperCase().startsWith(value.toUpperCase());
		}
	}

	/**
	 * This method determines if all the values in the parameter String has a match
	 * in the list of specialities for this record.
	 * 
	 * <ul>
	 * <li>A null value or spaces or zero length string in the criterion is
	 * considered a wild card.
	 * <li>There could be one or more criterion in the argument, for this to be
	 * considered a match, each criterion must have a match in this record list of
	 * specialities.
	 * <li>It is considered a match if one of this record's specialities starts with
	 * the text of the criterion.
	 * </ul>
	 * 
	 * @param value A reference to a String object denoting the specialities
	 *              criteria. There could be one or more speciality in this
	 *              argument, which means the criteria will be coma delimited.
	 *
	 * @return boolean value true if there is a match, false otherwise.
	 */
	private boolean matchesSpecialities(String value) {

		if ((value == null) || (value.trim().length() == 0)) {
			return true;
		} else {

			String[] vals = value.split(",");
			String[] specs = specialities.split(",");

			int matches = 0;

			for (String v : vals) {
				for (String s : specs) {
					if (s.trim().startsWith(v.trim())) {
						++matches;
						break;
					}
				}
			}

			return (matches == vals.length);
		}
	}

	/**
	 * This determines if the parameter String matches the size attribute of this
	 * record.
	 * 
	 * <ul>
	 * <li>A null value or spaces or zero length string in the criterion is
	 * considered a wild card.
	 *
	 * <li>It is considered a match if this record's size attribute is greater than
	 * or equal to the value specified in the parameter.
	 * </ul>
	 *
	 * @param value A String denoting the search criterion
	 *
	 * @return boolean true if there is a match, false otherwise.
	 */
	private boolean matchesSize(String value) throws DBAccessException {

		if ((value == null) || (value.length() == 0)) {
			return true;
		} else {
			try {
				return (Short.parseShort(value.trim()) <= Short.parseShort(size.trim()));

			} catch (NumberFormatException ex) {
				String msg = "Non numerical values in the size attribute";
				logger.log(Level.WARNING, msg);
				DBAccessException e = new DBAccessException(ex.getMessage(), ex);
				logger.throwing("Record", "matchesSize()", e);
				throw e;
			}
		}
	}

	/**
	 * This determines if the parameter String matches the rate attribute of this
	 * record.
	 * 
	 * <ul>
	 * <li>A null value or spaces or zero length string in the criterion is
	 * considered a wild card.
	 *
	 * <li>It is considered a match if this record's rate attribute is less than or
	 * equal to the value specified in the parameter.
	 * </ul>
	 *
	 * @param value A String denoting the search criterion
	 *
	 * @return boolean true if there is a match, false otherwise.
	 */
	private boolean matchesRate(String value) throws DBAccessException {

		if ((value == null) || (value.trim().length() == 0)) {
			return true;
		} else {
			try {
				return (Float.parseFloat(rate.trim().substring(1)) <= Float.parseFloat(value.trim().substring(1)));

			} catch (NumberFormatException ex) {
				String msg = "Non numerical values in the size attribute";
				logger.log(Level.WARNING, msg);
				DBAccessException e = new DBAccessException(ex.getMessage(), ex);
				logger.throwing("Record", "matchesRate()", e);
				throw e;
			}
		}
	}

	/**
	 * This determines if the parameter String matches the location attribute of
	 * this record.
	 * 
	 * <ul>
	 * <li>A null value or spaces or zero length string in the criterion is
	 * considered a wild card.
	 * <li>If the criterion value equals "-", it is a match if the record is booked.
	 * <li>If the criterion value equals "+", it is a match if the record is not
	 * booked.
	 * <li>It is considered a match if this record's owner attribute starts with the
	 * text of the parameter.
	 * </ul>
	 *
	 * @param value A String denoting the location criterion.
	 *
	 * @return boolean value true if there is a match, false otherwise.
	 */
	private boolean matchesOwner(String value) {

		if ((value == null) || (value.trim().length() == 0)) {
			return true;
		} else {
			if (value.trim().equals("+")) {
				return ((owner == null) || (owner.trim().length() == 0));

			} else if (value.trim().equals("-")) {
				return ((owner != null) && (owner.trim().length() != 0));
			} else {
				return owner.toUpperCase().startsWith(value.toUpperCase());
			}
		}
	}

	/**
	 * This method matches each attribute of this record to each corresponding
	 * criterion in the array of String objects provided.
	 * <ul>
	 * <li>When the "name" and "location" values are specified in the criteria array
	 * and each of the other criteria is equal to null or space or zero length
	 * String, the search is considered a "key" search. This means that only
	 * undeleted records that match both the "name" and "location" exactly will be
	 * returned.
	 * <li>For this record to be considered a match, its attributes must match all
	 * the criteria specified.
	 * </ul>
	 *
	 * @see #matchesName(String)
	 * @see #matchesLocation(String)
	 * @see #matchesSpecialities(String)
	 * @see #matchesSize(String)
	 * @see #matchesRate(String)
	 * @see #matchesOwner(String)
	 *
	 * @param criteria An array of String objects.
	 *
	 * @return A boolean value true if this record matches the specified criterian
	 *         and false otherwise.
	 */
	boolean matchesCriteria(String[] criteria) {

		if (((criteria[NAME_IDX] != null) && (criteria[NAME_IDX].trim().length() != 0))
				&& ((criteria[LOCATION_IDX] != null) && (criteria[LOCATION_IDX].trim().length() != 0))) {

			if (((criteria[SPECIALITIES_IDX] == null) || (criteria[SPECIALITIES_IDX].trim().length() == 0))
					&& ((criteria[SIZE_IDX] == null) || (criteria[SIZE_IDX].trim().length() == 0))
					&& ((criteria[RATE_IDX] == null) || (criteria[RATE_IDX].trim().length() == 0))
					&& ((criteria[OWNER_IDX] == null) || (criteria[OWNER_IDX].trim().length() == 0))) {

				return ((name.trim().compareToIgnoreCase(criteria[NAME_IDX]) == 0)
						&& (location.trim().compareToIgnoreCase(criteria[LOCATION_IDX]) == 0));
			}
		}

		return ((matchesName(criteria[NAME_IDX])) && (matchesLocation(criteria[LOCATION_IDX]))
				&& (matchesSpecialities(criteria[SPECIALITIES_IDX])) && (matchesSize(criteria[SIZE_IDX]))
				&& (matchesRate(criteria[RATE_IDX])) && (matchesOwner(criteria[OWNER_IDX])));

	}

	/**
	 * This method indicates whether a record has been deleted or not.
	 *
	 * @return boolean value true if the record has been deleted, false otherwise.
	 */
	boolean isDeleted() {

		return (statusFlag == DELETED_REC_FLAG_VALUE);
	}
}
