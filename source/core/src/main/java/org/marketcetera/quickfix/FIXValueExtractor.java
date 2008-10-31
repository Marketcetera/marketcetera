package org.marketcetera.quickfix;

import org.marketcetera.core.NumericStringSortable;
import org.marketcetera.quickfix.FIXMessageFactory;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MsgType;

public class FIXValueExtractor {

	private DataDictionary dataDictionary;
	private FIXMessageFactory messageFactory;


	public FIXValueExtractor(DataDictionary dataDictionary, FIXMessageFactory messageFactory) {
		super();
		this.dataDictionary = dataDictionary;
		this.messageFactory = messageFactory;

	}

	public Object extractValue(FieldMap inMap, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue) {
		return extractValue(inMap, fieldID, groupID, groupDiscriminatorID, groupDiscriminatorValue, false);
	}
	public Object extractValue(FieldMap inMap, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue, boolean humanReadable) {
		Object value = null;
		if (fieldID != null) {
			FieldMap map;
			if (inMap instanceof Message){
				if (groupID != null && groupDiscriminatorID!=null &&
						groupDiscriminatorValue!=null){
					map = extractGroup((Message)inMap, groupID, groupDiscriminatorID, groupDiscriminatorValue);
				} else {
					map = extractMap(inMap, fieldID);
				}
			} else {
				map = inMap;
			}
			value = fieldValueFromMap(map, fieldID, dataDictionary, humanReadable);
		}
		return value;
	}
	
	private FieldMap extractGroup(Message message, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue) {
		FieldMap map = null;
		try {
			Group aGroup = messageFactory.createGroup(message.getHeader().getString(MsgType.FIELD), groupID);
			if (aGroup !=null){
				int groupTag = aGroup.getFieldTag();
				int numGroups = message.getInt(groupTag);
				String discriminatorString = groupDiscriminatorValue.toString();
				for (int i = 1; i<= numGroups; i++){
					message.getGroup(i, aGroup);
					String messageDiscriminatorValue = aGroup.getString(groupDiscriminatorID);
					if (discriminatorString.equals(messageDiscriminatorValue)){
						return aGroup;
					}
				}
			} else {
				return null;
			}
		} catch (FieldNotFound e) {
		}
		return map;
	}

	public static Object fieldValueFromMap(FieldMap map, int fieldID, DataDictionary dict, boolean humanReadable) {
		Object value = null;
		if (map != null){
			try {
				FieldType fieldType = dict.getFieldTypeEnum(fieldID);
				if (fieldType == null){
					value = map.getString(fieldID);
				} else if (fieldType.equals(FieldType.UtcTimeOnly)) {
					value = map.getUtcTimeOnly(fieldID); //i18n_time
				} else if (fieldType.equals(FieldType.UtcTimeStamp)){
					value = map.getUtcTimeStamp(fieldID); //i18n_datetime
				} else if (fieldType.equals(FieldType.UtcDateOnly)
						||fieldType.equals(FieldType.UtcDate)){
					value = map.getUtcDateOnly(fieldID); //i18n_date
				} else if (Number.class.isAssignableFrom(fieldType.getJavaType())){
					value = map.getDecimal(fieldID);
				} else if (humanReadable && dict.hasFieldValue(fieldID)){
					value = map.getString(fieldID);
					try {
						value = CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getHumanFieldValue(fieldID, map.getString(fieldID));
					} catch (Exception ex){
						// do nothing, use the string value
					}
				} else if (fieldID == ClOrdID.FIELD) {
					value = new NumericStringSortable(map.getString(fieldID));
				} else {
					value = map.getString(fieldID);
				}
			} catch (FieldNotFound e) {
			}
		}
		return value;
	}


	private FieldMap extractMap(FieldMap mapParam, Integer fieldID) {
		FieldMap returnMap = null;

		if (mapParam instanceof Message) {
			returnMap = getAppropriateMap(fieldID, ((Message)mapParam));
		} else if (mapParam instanceof FieldMap){
			returnMap = (FieldMap) mapParam;
		}
		return returnMap;
	}

	private FieldMap getAppropriateMap(Integer fieldID, Message message) {
		FieldMap map;
		if (dataDictionary.isHeaderField(fieldID)) {
			map = message.getHeader();
		} else if (dataDictionary.isTrailerField(fieldID)) {
			map = message.getTrailer();
		} else {
			map = message;
		}
		return map;
	}


}
