package org.marketcetera.photon.ui.validation.fix;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.core.databinding.observable.Realm;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.Message;
import quickfix.StringField;

/**
 * Observe a FIX date of type String in format yyyyMMdd, such as
 * quickfix.field.ExpireDate.
 * <p>
 * This observable's value type is java.util.Date though it observes a String
 * FIX field.
 * </p>
 */
public class StringDateObservableValue extends FIXObservableValue {
	private SimpleDateFormat fixUTCFormatter;

	private int targetCalendarField;

	/**
	 * @param calendarField
	 *            which Calendar field to update on the underlying FIX message
	 *            date, such as Calendar.YEAR or Calendar.MONTH.
	 */
	public StringDateObservableValue(int calendarField, Realm realm,
			Message message, int fieldNumber, DataDictionary dataDictionary) {
		super(realm, message, fieldNumber, dataDictionary);
		this.targetCalendarField = calendarField;
		// todo: Quickfix expects a UTC time zone but this uses the local time
		// zone. See http://www.quickfixj.org/jira/browse/QFJ-104
		fixUTCFormatter = new SimpleDateFormat("yyyyMMdd");
	}

	@Override
	public Object getValueType() {
		return Date.class;
	}

	private void handleError() {
		// todo: http://trac.marketcetera.org/trac.fcgi/ticket/197
		FieldMap fixFieldMap = getFieldMap();
		fixFieldMap.removeField(fieldNumber);
	}

	private Date getFIXFieldDate(StringField field, FieldMap fixFieldMap) {
		Date currentDate = null;
		try {
			// Get the currently set FIX date
			String fixDateField = fixFieldMap.getField(field).getValue();
			currentDate = fixUTCFormatter.parse(fixDateField);
		} catch (Exception anyException) {
			handleError();
		}
		return currentDate;
	}

	private Date getUpdatedDate(Date currentDate, int calendarField,
			Date partialDateToSet) {
		try {
			// Create a calendar with the pre-existing date
			GregorianCalendar existingCalendar = new GregorianCalendar();
			existingCalendar.setTime(currentDate);
			// Create a calendar for the requested field with the newly set date
			GregorianCalendar partiallySetCalendar = new GregorianCalendar();
			partiallySetCalendar.setTime(partialDateToSet);
			int calendarFieldValue = partiallySetCalendar.get(calendarField);
			// Update only the requested field
			existingCalendar.set(calendarField, calendarFieldValue);
			return existingCalendar.getTime();
		} catch (Exception anyException) {
			handleError();
		}
		return null;
	}

	@Override
	protected Object doGetValue() {
		StringField fixDateField = new StringField(fieldNumber);
		FieldMap fixFieldMap = getFieldMap();
		Date currentDate = getFIXFieldDate(fixDateField, fixFieldMap);
		return currentDate;
	}

	@Override
	protected void doSetValue(Object value) {
		if (value != null && !(value instanceof Date)) {
			super.doSetValue(value);
		} else {
			Date dateToSet = (Date) value;
			StringField fixDateField = new StringField(fieldNumber);

			FieldMap fixFieldMap = getFieldMap();
			Date currentDate = getFIXFieldDate(fixDateField, fixFieldMap);

			if (currentDate == null) {
				// If the date field is not set on the fix message, create
				// a new one.
				// todo: Some date fields, such as ExpireDate, have choices
				// dictated by market data. Creating a new date is incorrect for
				// such fields.
				currentDate = new Date();
			}

			if (currentDate != null) {
				Date updatedDate = getUpdatedDate(currentDate,
						targetCalendarField, dateToSet);
				if (updatedDate != null) {
					String updatedDateString = fixUTCFormatter
							.format(updatedDate);
					fixDateField.setValue(updatedDateString);
				}
			}
		}
	}
}
