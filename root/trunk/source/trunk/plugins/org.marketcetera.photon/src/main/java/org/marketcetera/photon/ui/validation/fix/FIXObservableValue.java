package org.marketcetera.photon.ui.validation.fix;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;

import quickfix.BooleanField;
import quickfix.CharField;
import quickfix.DataDictionary;
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
 * 
 */
public class FIXObservableValue extends AbstractObservableValue {

	private static final String QUICKFIX_FIELD_PACKAGE = ClOrdID.class.getPackage().getName()+".";
	protected final Message message;
	protected final int fieldNumber;
	protected final DataDictionary dataDictionary;
	protected String fieldName;
	private Class fieldClass;
	private FieldType fieldTypeEnum;
	private FieldMap fieldMap;
	
	public FIXObservableValue(Realm realm, Message message, int fieldNumber, DataDictionary dataDictionary) {
		super(realm);
		this.message = message;
		this.fieldNumber = fieldNumber;
		this.dataDictionary = dataDictionary;
		this.fieldName = dataDictionary.getFieldName(fieldNumber);
		this.fieldTypeEnum = dataDictionary.getFieldTypeEnum(fieldNumber);
		init(realm);
	}
	
	private void init(Realm realm){
		String qualifiedFieldName = QUICKFIX_FIELD_PACKAGE+fieldName;
		Class myFieldClass = null;
		try {
			myFieldClass = Class.forName(qualifiedFieldName);
		} catch (ClassNotFoundException e) {
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
	
	public FIXObservableValue(Realm realm, Message message, int fieldNumber, DataDictionary dataDictionary, String fieldName, FieldType fieldTypeEnum) {
		super(realm);
		this.message = message;
		this.fieldNumber = fieldNumber;
		this.dataDictionary = dataDictionary;
		this.fieldName = fieldName;
		this.fieldTypeEnum = fieldTypeEnum;
		init(realm);
	}
	
	protected FieldMap getFieldMap() {
		return fieldMap;
	}

	@Override
	protected Object doGetValue() {

		try {
			if (fieldClass != null){
				if (BooleanField.class.isAssignableFrom(fieldClass)){
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
					StringField field = new StringField(fieldNumber);
					return new BigDecimal(fieldMap.getField(field).getValue());
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
			return null;
		}
	}

	public Object getValueType() {
		if (fieldTypeEnum != null) {
			Class javaType = fieldTypeEnum.getJavaType();
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

	@Override
	protected void doSetValue(Object value) {
		if (value != null) {
			if (fieldTypeEnum == FieldType.Amt){
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.Boolean) {
				fieldMap.setField(new BooleanField(fieldNumber, (Boolean) value));
			} else if (fieldTypeEnum == FieldType.Char) {
				fieldMap.setField(new CharField(fieldNumber, (Character) value));
			} else if (fieldTypeEnum == FieldType.Country) {
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.Currency) {
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.Data) {
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.DayOfMonth) {
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.Exchange) {
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.Float) {
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.Int) {
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.Length) {
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.LocalMktDate) {
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.MonthYear) {
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.MultipleValueString) {
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.NumInGroup) {
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.Percentage) {
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.Price) {
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.PriceOffset) {
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.Qty) {
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			} else if (fieldTypeEnum == FieldType.SeqNum) {
				fieldMap.setField(new IntField(fieldNumber, (Integer)value));
			} else if (fieldTypeEnum == FieldType.String) {
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.Time) {
				UtcTimeOnlyField utcTimeOnlyField = new UtcTimeOnlyField(fieldNumber);
				utcTimeOnlyField.setValue((Date) value);
				fieldMap.setField(utcTimeOnlyField);
			} else if (fieldTypeEnum == FieldType.Unknown) {
				fieldMap.setField(new StringField(fieldNumber, (String) value));
			} else if (fieldTypeEnum == FieldType.UtcDate) {
				UtcDateOnlyField utcDateOnlyField = new UtcDateOnlyField(fieldNumber);
				utcDateOnlyField.setValue((Date) value);
				fieldMap.setField(utcDateOnlyField);
			} else if (fieldTypeEnum == FieldType.UtcDateOnly) {
				UtcDateOnlyField utcDateOnlyField = new UtcDateOnlyField(fieldNumber);
				utcDateOnlyField.setValue((Date) value);
				fieldMap.setField(utcDateOnlyField);
			} else if (fieldTypeEnum == FieldType.UtcTimeOnly) {
				UtcTimeOnlyField utcTimeOnlyField = new UtcTimeOnlyField(fieldNumber);
				utcTimeOnlyField.setValue((Date) value);
				fieldMap.setField(utcTimeOnlyField);
			} else if (fieldTypeEnum == FieldType.UtcTimeStamp) {
				UtcTimeStampField utcTimeStampField = new UtcTimeStampField(fieldNumber);
				utcTimeStampField.setValue((Date) value);
				fieldMap.setField(utcTimeStampField);
			} else {
				fieldMap.setField(new StringField(fieldNumber, value.toString()));
			}
		} else {
			fieldMap.removeField(fieldNumber);
		}
	}

	public Class getFieldClass() {
		return fieldClass;
	}
}
