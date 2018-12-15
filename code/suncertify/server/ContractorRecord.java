/*
 * ContractorRecord.java 
 * Version 1.0
 * Date: 07/28/2015
 * Copyright @Augustine Ogundimu, 2015
 */

package suncertify.server;

import java.io.Serializable;

import static suncertify.db.DatabaseMetaData.*;
import suncertify.client.gui.ClientDialogMode;

/**
 * The ContractorRecord class encapsulates the attributes of a contractor record
 * in the application.
 *
 * @author Augustine Ogundimu
 * @version 1.0
 * @since 1.0
 */
public class ContractorRecord implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 20120731125400L;

	/**
	 * A constant value assigned as the default record number.
	 */
	private static final int UNASSIGNED_REC_NUM = -1;

	/**
	 * This is an integer value uniquely identifying this record.
	 */
	protected int recordNumber = UNASSIGNED_REC_NUM;

	/**
	 * The contractor name.
	 */
	protected String name;

	/**
	 * The contractor location.
	 */
	protected String location;

	/**
	 * The contractor specialities.
	 */
	protected String specialities;

	/**
	 * The contractor size.
	 */
	protected String size;

	/**
	 * The contractor hourly rate.
	 */
	protected String rate;

	/**
	 * The current owner of the contractor.
	 */
	protected String owner;

	/**
	 * A String object denoting the result of the last validation done on this
	 * record.
	 */
	private String reason = null;

	/**
	 * The default constructor.
	 */
	public ContractorRecord() {

	}

	/**
	 * Constructor taking record number and attributes.
	 *
	 * @param data      An array of String objects denoting the attributes of the
	 *                  record to be created.
	 *
	 * @param recNumber An integer uniquely identifying the record.
	 */
	public ContractorRecord(int recNumber, String[] data) {
		this(data);
		recordNumber = recNumber;
	}

	/**
	 * Constructor taking just attributes.
	 *
	 * @param data - An array of String objects denoting the attributes of the
	 *             record.
	 */
	public ContractorRecord(String[] data) {

		if (data[NAME_IDX] != null) {
			name = data[NAME_IDX].trim();
		}

		if (data[LOCATION_IDX] != null) {
			location = data[LOCATION_IDX].trim();
		}

		if (data[SPECIALITIES_IDX] != null) {
			specialities = data[SPECIALITIES_IDX].trim();
		}

		if (data[SIZE_IDX] != null) {
			size = data[SIZE_IDX].trim();
		}

		if (data[RATE_IDX] != null) {
			rate = data[RATE_IDX].trim();
		}

		if (data[OWNER_IDX] != null) {
			owner = data[OWNER_IDX].trim();
		}
	}

	/**
	 * The copy constructor.
	 *
	 * @param record A reference to a ContractorRecord object.
	 */
	public ContractorRecord(ContractorRecord record) {
		recordNumber = record.getRecordNumber();
		name = record.getName();
		location = record.getLocation();
		specialities = record.getSpecialities();
		size = record.getSize();
		rate = record.getRate();
		owner = record.getOwner();
	}

	/**
	 * This method does "name" attribute validation.
	 * 
	 * @return boolean value true if the name attribute is valid, false otherwise.
	 */
	private boolean nameFieldValid() {
		if ((name == null) || (name.length() == 0)) {
			reason = "The NAME filed cannot be left blank!\n";
			return false;
		} else {
			reason = "";
			return true;
		}
	}

	/**
	 * This method does the "location" attribute validation.
	 *
	 * @return boolean value true if the location attribute is valid, false
	 *         otherwise.
	 */
	private boolean locationFieldValid() {
		if ((location == null) || (location.length() == 0)) {
			reason = "The LOCATION filed cannot be left blank!\n";
			return false;
		} else {
			reason = "";
			return true;
		}
	}

	/**
	 * This method does the "specialities" attribute validation.
	 *
	 * @return boolean value true if the specialities attribute is valid, false
	 *         otherwise.
	 */
	private boolean specialitiesFieldValid() {
		if ((specialities == null) || (specialities.trim().length() == 0)) {
			reason = "The SPECIALITIES field must not be blank!\n";
			return false;
		} else {
			reason = "";
			return true;
		}
	}

	/**
	 * This method does the "rate" attribute validation.
	 *
	 * @return boolean value true if the rate attribute is valid, false otherwise.
	 */
	private boolean rateFieldValid() {
		if ((rate == null) || (rate.length() == 0)) {
			reason = "The rate field cannot be left blank";
			return false;
		} else {
			reason = "";
			return true;
		}
	}

	/**
	 * This method does the "size" attribute validation.
	 *
	 * @return boolean value true if the size attribute is valid, false otherwise.
	 */
	private boolean sizeFieldValid() {
		if ((size == null) || (size.length() == 0)) {
			reason = "The size field cannot be left blank";
			return false;
		} else {
			reason = "";
			return true;
		}
	}

	/**
	 * This method does the "owner" attribute validation.
	 * 
	 * @param mode The mode specifies what operation is being done on the record.
	 *
	 * @return boolean value true if the owner attribute is valid, false otherwise.
	 */
	private boolean ownerFieldValid(ClientDialogMode mode) {
		if (mode == ClientDialogMode.RELEASE) {
			if ((owner == null) || (owner.trim().length() == 0)) {
				reason = "";
				return true;
			} else {
				reason = "To release a record the owner" + " field should be set to blank! \n";
				return false;
			}
		}

		if ((owner != null) && (owner.trim().length() == 8) && (owner.trim().matches("[0-9]+"))) {
			reason = "";
			return true;
		} else {
			reason = owner + " is not a valid customer number!\n\n";
			return false;
		}
	}

	/**
	 * This method determines if this record is logically equal to the record in the
	 * parameter. Two records are logically equal if both their name and location
	 * attributes are exact matches.
	 *
	 * @param obj A reference to an Object instance to be compared with this record.
	 *
	 * @return A boolean value true if the obj parameter is a reference to an object
	 *         that is logically equal to this object.
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ContractorRecord) {

			String nam = ((ContractorRecord) obj).getName().trim();
			String loc = ((ContractorRecord) obj).getLocation().trim();

			if ((this.name.trim().compareToIgnoreCase(nam) == 0)
					&& (this.location.trim().compareToIgnoreCase(loc) == 0)) {

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * This returns the hashcode for this record. The value returned will always be
	 * the same for a given record at all times. This is because the value returned
	 * is the unique record number assigned to the record when it was created.
	 * 
	 * @return an integer value denoting the hashcode for this record.
	 */
	@Override
	public int hashCode() {

		return recordNumber;
	}

	/**
	 * ContractorContractorRecord number attribute accessor.
	 *
	 * @return The record number for this record.
	 */
	public int getRecordNumber() {

		return recordNumber;
	}

	/**
	 * Sets the record number.
	 *
	 * @param value The record number.
	 */
	public void setRecordNumber(int value) {

		recordNumber = value;
	}

	/**
	 * Name attribute accessor.
	 *
	 * @return The name attribute of this record.
	 */
	public String getName() {

		return name;
	}

	/**
	 * Sets the name attribute of the record.
	 * 
	 * @param value String object denoting the name.
	 */
	public void setName(String value) {

		name = value;
	}

	/**
	 * Location attribute accessor.
	 *
	 * @return The location attribute of this record.
	 */
	public String getLocation() {

		return location;
	}

	/**
	 * Sets the value attribute of this record.
	 * 
	 * @param value String object denoting the value.
	 */
	public void setLocation(String value) {

		location = value.trim();
	}

	/**
	 * Owner attribute accessor.
	 * 
	 * @return The owner attribute of this record.
	 */
	public String getOwner() {

		return owner;
	}

	/**
	 * Sets the owner attribute of this record.
	 *
	 * @param value String object denoting the value.
	 */
	public void setOwner(String value) {

		owner = value.trim();
	}

	/**
	 * Specialities attribute accessor.
	 *
	 * @return The specialities attribute of this record
	 */
	public String getSpecialities() {

		return specialities;
	}

	/**
	 * Sets the specialities attribute of this record.
	 *
	 * @param value String object denoting the new value.
	 */
	public void setSpecialities(String value) {

		specialities = value.trim();
	}

	/**
	 * Rate attribute accessor.
	 *
	 * @return The rate attribute of this record.
	 */
	public String getRate() {

		return rate;
	}

	/**
	 * Sets the rate attribute of this record.
	 *
	 * @param value String object denoting the new value.
	 */
	public void setRate(String value) {

		rate = value.trim();
	}

	/**
	 * Size attribute accessor.
	 *
	 * @return The size attribute of this record.
	 */
	public String getSize() {

		return size;
	}

	/**
	 * Sets the size attribute of this record.
	 *
	 * @param value String object denoting the new value.
	 */
	public void setSize(String value) {

		size = value;
	}

	/**
	 * Retrieves the attributes of this record.
	 * 
	 * @return An array of String objects denoting the attributes of this record.
	 */
	public String[] getAttributes() {
		String[] attributes = new String[] { name, location, specialities, size, rate, owner };
		return attributes;
	}

	/**
	 * This methods sets the attributes of this record with the values in the String
	 * array argument.
	 *
	 * @param data A reference to an array of String objects.
	 */
	public void setAttributes(String[] data) {

		name = data[NAME_IDX].trim();

		location = data[LOCATION_IDX].trim();

		specialities = data[SPECIALITIES_IDX].trim();

		size = data[SIZE_IDX].trim();

		rate = data[RATE_IDX].trim();

		owner = data[OWNER_IDX].trim();
	}

	/**
	 * This method determines if one of the attributes of this record is different
	 * from a corresponding attribute is the argument record. Only size,
	 * specialities and rate attributes are considered.
	 *
	 * @param record The reference to the ContractorRecord object being considered.
	 * 
	 * @return A boolean value true if there is a difference, false otherwise.
	 */
	public boolean differsFrom(ContractorRecord record) {

		if ((this.size.equals(record.getSize())) && (this.specialities.equals(record.getSpecialities()))
				&& (this.rate.equals(record.getRate()))) {

			reason = "";
			return false;

		} else {
			reason = name + " at " + location + " has been modified since last read!";
			return true;
		}
	}

	/**
	 * Does a validation of this record depending on the type of operation being
	 * done on it.
	 *
	 * @param mode This specfies the mode or operation on the record.
	 *
	 * @return A boolean value true if the record is valid for the specified mode
	 *         and false otherwise.
	 *
	 * @see suncertify.client.gui.ClientDialogMode
	 */
	public boolean isValid(ClientDialogMode mode) {

		switch (mode) {
		case ADD:
			return (nameFieldValid() && locationFieldValid() && specialitiesFieldValid() && rateFieldValid()
					&& sizeFieldValid());
		case BOOK:
		case RELEASE:
			return ownerFieldValid(mode);

		case UPDATE:
			return (specialitiesFieldValid() && rateFieldValid() && sizeFieldValid());
		case SEARCH:
			return true;
		case DELETE:
			return true;
		default:
			return false;
		}
	}

	/**
	 * This indicates whether this record is booked or not. When a record does have
	 * a non blank owner attribute it is considered booked.
	 *
	 * @return A boolean value true if record is booked, false otherwise.
	 */
	public boolean isBooked() {
		if ((owner != null) && (owner.trim().length() > 0)) {
			reason = name + " at " + location + " is currently booked!";
			return true;
		} else {
			reason = name + " at " + location + " is not currently booked!";
			return false;
		}
	}

	/**
	 * Returns the text denoting the result of the validation done on this object.
	 *
	 * @return A String object denoting the reason the last validation on this
	 *         record failed.
	 */
	public String getReason() {
		String s = reason;
		reason = "";
		return s;
	}

	/*
	 * Returns a String representation of this record. This is a concactenation of
	 * all the attributes of the record delimited by " | ".
	 *
	 * @return A String object which equals to a concactenation of all the
	 * attributes of the record.
	 */
	@Override
	public String toString() {
		return name + " | " + location + " | " + specialities + " | " + size + " | " + rate + " | " + owner;
	}
}
