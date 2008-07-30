package org.marketcetera.photon.ui.validation.fix;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;

import quickfix.BooleanField;
import quickfix.CharField;
import quickfix.DataDictionary;
import quickfix.DecimalField;
import quickfix.DoubleField;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.IntField;
import quickfix.Message;
import quickfix.StringField;
import quickfix.UtcDateOnlyField;
import quickfix.UtcTimeOnlyField;
import quickfix.UtcTimeStampField;
import quickfix.field.ClOrdID;

/**
 * An implementation of {@link IObservableValue} that can extract values out
 * of a QuickFIX message, given a field number and a data dictionary.
 * The values returned are of the correct Java type, given the field number
 * and the data dictionary.
 * 
 * This class only partially fulfils the observable value contract, as it will
 * not fire events when changes are made to the fields in the underlying message.
 */
public class FIXObservableValue extends AbstractObservableValue {

	private static final String QUICKFIX_FIELD_PACKAGE = ClOrdID.class.getPackage().getName()+"."; //$NON-NLS-1$
	protected final Message message;
	protected final int fieldNumber;
	protected final DataDictionary dataDictionary;
	protected String fieldName;
	private Class<?> fieldClass;
	private FieldType fieldTypeEnum;
	private FieldMap fieldMap;
	
	/**
	 * Create a new observable value for the specified message and field number, using the provided data dictionary.
	 * @param realm the realm for this
	 * @param message the message to observe
	 * @param fieldNumber the field into which to insert and extract values
	 * @param dataDictionary the data dictionary
	 * @see AbstractObservableValue
	 */
	public FIXObservableValue(Realm realm, Message message, int fieldNumber, DataDictionary dataDictionary) {
		super(realm);
		this.message = message;
		this.fieldNumber = fieldNumber;
		this.dataDictionary = dataDictionary;
		this.fieldName = dataDictionary.getFieldName(fieldNumber);
		this.fieldTypeEnum = dataDictionary.getFieldTypeEnum(fieldNumber);
		init(realm);
	}

	/**
	 * Create a new observable value for the specified message and field number, using the provided data dictionary,
	 * specifying the field name and type enum.
	 * @param realm the realm for this
	 * @param message the message to observe
	 * @param fieldNumber the field into which to insert and extract values
	 * @param dataDictionary the data dictionary
	 * @param fieldName the name of the field
	 * @param fieldTypeEnum the enumeration representing the Java type.
	 */
    public FIXObservableValue(Realm realm, Message message, int fieldNumber, DataDictionary dataDictionary,
                              String fieldName, FieldType fieldTypeEnum) {
        super(realm);
        this.message = message;
        this.fieldNumber = fieldNumber;
        this.dataDictionary = dataDictionary;
        this.fieldName = fieldName;
        this.fieldTypeEnum = fieldTypeEnum;
        init(realm);
    }

	private void init(Realm realm){
		String qualifiedFieldName = QUICKFIX_FIELD_PACKAGE+fieldName;
		Class<?> myFieldClass = null;
		try {
			myFieldClass = Class.forName(qualifiedFieldName);
		} catch (ClassNotFoundException e) {
			// if fieldClass cannot be determined it is assumed to be String by doGetValue()
		}
		fieldClass = myFieldClass;

		// I think that the fact that the compiler requires this extra
		// variable is a bug
		FieldMap myFieldMap;
		if (dataDictionary.isHeaderField(fieldNumber)) {
			myFieldMap = message.getHeader();
		} else if (dataDictionary.isTrailerField(fieldNumber)) {
			myFieldMap = message.getTrailer();
		} else {
			myFieldMap = message;
		}
		fieldMap = myFieldMap;
	}
	
	protected FieldMap getFieldMap() {
		return fieldMap;
	}

	/**
	 * Get the value in the message contained herein.
	 * 
	 * @return a Java object representing the value of the appropriate field.
	 */
	@Override
	protected Object doGetValue() {

		try {
			if (fieldClass != null){
				if (DecimalField.class.isAssignableFrom(fieldClass)){
					DecimalField field = new DecimalField(fieldNumber);
					return fieldMap.getField(field).getValue();
				} else if (BooleanField.class.isAssignableFrom(fieldClass)){
					BooleanField field = new BooleanField(fieldNumber);
					return fieldMap.getField(field).getValue();
				} else if (CharField.class.isAssignableFrom(fieldClass)) {
					CharField field = new CharField(fieldNumber);
					return fieldMap.getField(field).getValue();
				} else if (UtcDateOnlyField.class.isAssignableFrom(fieldClass)) {
					UtcDateOnlyField field = new UtcDateOnlyField(fieldNumber);
					return fieldMap.getField(field).getValue();
				} else if (UtcTimeOnlyField.class.isAssignableFrom(fieldClass)) {
					UtcTimeOnlyField field = new UtcTimeOnlyField(fieldNumber);
					return fieldMap.getField(field).getValue();
				} else if (UtcTimeStampField.class.isAssignableFrom(fieldClass)) {
					UtcTimeStampField field = new UtcTimeStampField(fieldNumber);
					return fieldMap.getField(field).getValue();
				} else if (DoubleField.class.isAssignableFrom(fieldClass)) {
					DecimalField field = new DecimalField(fieldNumber);
					return fieldMap.getField(field).getValue();
				} else if (IntField.class.isAssignableFrom(fieldClass)) {
					IntField field = new IntField(fieldNumber);
					return fieldMap.getField(field).getValue();
				} else if (StringField.class.isAssignableFrom(fieldClass)) {
					StringField field = new StringField(fieldNumber);
					return fieldMap.getField(field).getValue();
				} else {
					return null;
				}
			} else {
				StringField field = new StringField(fieldNumber);
				return fieldMap.getField(field).getValue();
			}
		} catch (FieldNotFound fnf){
			// if the field is missing, simply return null to so indicate
			return null;
		}
	}

	/**
	 * Get the value type (Java class) of the values returned by this.
	 * @return a Java Class<?> object representing the type of values returned.
	 */
	public Object getValueType() {
		if (fieldTypeEnum != null) {
			Class<?> javaType = fieldTypeEnum.getJavaType();
			if (FieldType.Char.equals(fieldTypeEnum)){
				return Character.class;
			} else if (Calendar.class.equals(javaType)){
				// TODO: this is a result of QuickFIX/J bug, remove after QF/J 1.1.1 release
				return Date.class;
			} else if (Double.class.equals(javaType)){
				return BigDecimal.class;
			} else {
				return javaType;
			}
		} else {
			return String.class;
		}
	}

	/**
	 * Set a value in to the field specified by this.
	 * 
	 * First this will attempt to determine the old value, if available, for event generation purposes.
	 * 
	 * @param value the value to set
	 */
	@Override
	protected void doSetValue(Object value) {
		Object oldValue = null;
		if (value != null) {
			if (fieldTypeEnum == FieldType.Amt){
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.Boolean) {
				try { oldValue = fieldMap.getBoolean(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new BooleanField(fieldNumber, (Boolean) value));
			} else if (fieldTypeEnum == FieldType.Char) {
				try { oldValue = fieldMap.getChar(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new CharField(fieldNumber, (Character) value));
			} else if (fieldTypeEnum == FieldType.Country) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.Currency) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.Data) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.DayOfMonth) {
				try { oldValue = fieldMap.getInt(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.Exchange) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.Float) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.Int) {
				try { oldValue = fieldMap.getInt(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.Length) {
				try { oldValue = fieldMap.getInt(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.LocalMktDate) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.MonthYear) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.MultipleValueString) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.NumInGroup) {
				try { oldValue = fieldMap.getInt(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.Percentage) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.Price) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.PriceOffset) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.Qty) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.SeqNum) {
				try { oldValue = fieldMap.getInt(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.String) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.Time) {
				try { oldValue = fieldMap.getUtcTimeOnly(fieldNumber); } catch (FieldNotFound fnf) {}
				UtcTimeOnlyField utcTimeOnlyField = new UtcTimeOnlyField(fieldNumber);
				utcTimeOnlyField.setValue((Date) value);
				fieldMap.setField(utcTimeOnlyField);
			} else if (fieldTypeEnum == FieldType.Unknown) {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.UtcDate) {
				try { oldValue = fieldMap.getUtcDateOnly(fieldNumber); } catch (FieldNotFound fnf) {}
				UtcDateOnlyField utcDateOnlyField = new UtcDateOnlyField(fieldNumber);
				utcDateOnlyField.setValue((Date) value);
				fieldMap.setField(utcDateOnlyField);
			} else if (fieldTypeEnum == FieldType.UtcDateOnly) {
				try { oldValue = fieldMap.getUtcDateOnly(fieldNumber); } catch (FieldNotFound fnf) {}
				UtcDateOnlyField utcDateOnlyField = new UtcDateOnlyField(fieldNumber);
				utcDateOnlyField.setValue((Date) value);
				fieldMap.setField(utcDateOnlyField);
			} else if (fieldTypeEnum == FieldType.UtcTimeOnly) {
				try { oldValue = fieldMap.getUtcTimeOnly(fieldNumber); } catch (FieldNotFound fnf) {}
				UtcTimeOnlyField utcTimeOnlyField = new UtcTimeOnlyField(fieldNumber);
				utcTimeOnlyField.setValue((Date) value);
				fieldMap.setField(utcTimeOnlyField);
			} else if (fieldTypeEnum == FieldType.UtcTimeStamp) {
				try { oldValue = fieldMap.getUtcTimeStamp(fieldNumber); } catch (FieldNotFound fnf) {}
				UtcTimeStampField utcTimeStampField = new UtcTimeStampField(fieldNumber);
				utcTimeStampField.setValue((Date) value);
				fieldMap.setField(utcTimeStampField);
			} else {
				try { oldValue = fieldMap.getString(fieldNumber); } catch (FieldNotFound fnf) {}
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			}
		} else {
			fieldMap.removeField(fieldNumber);
		}

		if (oldValue != value){
			fireValueChange(Diffs.createValueDiff(oldValue,value));
		}
	}

	public Class<?> getFieldClass() {
		return fieldClass;
	}
}
