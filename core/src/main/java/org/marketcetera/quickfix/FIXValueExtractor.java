package org.marketcetera.quickfix;

import org.joda.time.DateTime;
import org.marketcetera.core.NumericStringSortable;

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
    /**
     * Extract a value from the given field map.
     *
     * @param inMap a <code>FieldMap</code> value
     * @param inFieldId an <code>Integer</code> value
     * @param inGroupId an <code>Integer</code> value
     * @param inGroupDiscriminatorId an <code>Integer</code> value
     * @param inGroupDiscriminatorValue an <code>Integer</code> value
     * @param inHumanReadable a <code>boolean</code> value
     * @return an <code>Object</code> value
     */
    public Object extractValue(FieldMap inMap,
                               Integer inFieldId,
                               Integer inGroupId,
                               Integer inGroupDiscriminatorId,
                               Object inGroupDiscriminatorValue,
                               boolean inHumanReadable)
    {
        Object value = null;
        if(inFieldId != null) {
            FieldMap map;
            if(inMap instanceof Message) {
                if(inGroupId != null && inGroupDiscriminatorId != null && inGroupDiscriminatorValue != null) {
                    map = extractGroup((Message)inMap,
                                       inGroupId,
                                       inGroupDiscriminatorId,
                                       inGroupDiscriminatorValue);
                } else {
                    map = extractMap(inMap,
                                     inFieldId);
                }
            } else {
                map = inMap;
            }
            value = fieldValueFromMap(map,
                                      inFieldId,
                                      dataDictionary,
                                      inHumanReadable);
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
    /**
     * Extracts a field value from the given value map.
     *
     * @param inFieldMap a <code>FieldMap</code> value
     * @param inFieldId an <code>int</code> value
     * @param inDataDictionary a <code>DataDictionary</code> value
     * @param inHumanReadable a <code>boolean</code>value
     * @return an <code>Object</code> value
     */
    public static Object fieldValueFromMap(FieldMap inFieldMap,
                                           int inFieldId,
                                           DataDictionary inDataDictionary,
                                           boolean inHumanReadable)
    {
        Object value = null;
        if (inFieldMap != null) {
            try {
                FieldType fieldType = inDataDictionary.getFieldType(inFieldId);
                if(fieldType == null){
                    value = inFieldMap.getString(inFieldId);
                } else if (inHumanReadable && inDataDictionary.hasFieldValue(inFieldId)) {
                    value = inFieldMap.getString(inFieldId);
                    try {
                        value = FIXDataDictionary.getHumanFieldValue(inDataDictionary,
                                                                     inFieldId,
                                                                     inFieldMap.getString(inFieldId));
                    } catch (Exception ignored) {
                        // do nothing, use the string value
                    }
                } else if(fieldType.equals(FieldType.UTCTIMEONLY)) {
                    value = inFieldMap.getUtcTimeOnly(inFieldId); //i18n_time
                } else if(fieldType.equals(FieldType.UTCTIMESTAMP)){
                    value = new DateTime(inFieldMap.getUtcTimeStamp(inFieldId));
                } else if(fieldType.equals(FieldType.UTCDATEONLY) ||fieldType.equals(FieldType.UTCDATE)){
                    value = inFieldMap.getUtcDateOnly(inFieldId);
                } else if(Number.class.isAssignableFrom(fieldType.getJavaType())){
                    value = inFieldMap.getDecimal(inFieldId);
                } else if (inFieldId == ClOrdID.FIELD) {
                    value = new NumericStringSortable(inFieldMap.getString(inFieldId));
                } else {
                    value = inFieldMap.getString(inFieldId);
                }
            } catch (FieldNotFound ignored) {}
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
