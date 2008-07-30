package org.marketcetera.photon.ui.validation.fix;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.core.databinding.observable.Realm;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;

/**
 * Observe a FIX date of type String in format yyyyMMdd, such as
 * quickfix.field.ExpireDate.
 * <p>
 * This observable's value type is java.util.Date though it observes a String
 * FIX field.
 * </p>
 */
public class StringDateObservableValue extends FIXObservableValue {
	private SimpleDateFormat ymdFormatter;
	private SimpleDateFormat ymFormatter;
	
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
		ymdFormatter = new SimpleDateFormat("yyyyMMdd"); //$NON-NLS-1$
		ymFormatter = new SimpleDateFormat("yyyyMM"); //$NON-NLS-1$
	}

	@Override
	public Object getValueType() {
		return Date.class;
	}

	private Date getFIXFieldDate(int fieldNumber, FieldMap fixFieldMap) {
		Date currentDate = null;
		if (fixFieldMap.isSetField(fieldNumber)){
			String fixDateField;
			try {
				fixDateField = fixFieldMap.getString(fieldNumber);
				try {
					// Get the currently set FIX date
					currentDate = ymdFormatter.parse(fixDateField);
				} catch (Exception anyException) {
					try {
						currentDate = ymFormatter.parse(fixDateField);
					} catch (Exception ex){}
				}
			} catch (FieldNotFound e) {
				/* should never happen */
			}
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
			return null;
		}
	}

	@Override
	protected Object doGetValue() {
		FieldMap fixFieldMap = getFieldMap();
		Date currentDate = getFIXFieldDate(fieldNumber, fixFieldMap);
		return currentDate;
	}

	@Override
	protected void doSetValue(Object value) {
		if (value != null) {
			if (!(value instanceof Date)) {
				super.doSetValue(value);
			} else {
				Date dateToSet = (Date) value;
	
				FieldMap fixFieldMap = getFieldMap();
				Date currentDate = getFIXFieldDate(fieldNumber, fixFieldMap);
	
				if (currentDate == null) {
					// If the date field is not set on the fix message, create
					// a new one.
					// todo: Some date fields, such as ExpireDate, have choices
					// dictated by market data. Creating a new date is incorrect for
					// such fields.
					Calendar calendar = GregorianCalendar.getInstance();
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar.set(Calendar.HOUR, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					
					currentDate = calendar.getTime();
				}
	
				if (currentDate != null) {
					Date updatedDate = getUpdatedDate(currentDate,
							targetCalendarField, dateToSet);
					if (updatedDate != null) {
						String updatedDateString = ymFormatter
								.format(updatedDate);
						fixFieldMap.setString(fieldNumber, updatedDateString);
					}
				}
			}
		}
	}
}
