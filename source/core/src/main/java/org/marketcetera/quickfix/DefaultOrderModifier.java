package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigData;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageKey;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.MsgType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.prefs.BackingStoreException;

/**
 * Takes in a collection of message/header/trailer fields to always
 * modify on a passed-in message
 * @author gmiller
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DefaultOrderModifier implements OrderModifier {
    private static final String ADMIN_MODIFIER_KEY = "ADMIN";
    private static final String APP_MODIFIER_KEY = "APP";
    private static final String GLOBAL_MODIFIER_KEY = "*";
    private static final String PARSER_REGEX = "([0-9]+)(\\((\\*|[0-9a-z]|admin|app)\\))?";
    private static final Pattern DEFAULT_FIELDS_PATTERN = Pattern.compile(PARSER_REGEX, Pattern.CASE_INSENSITIVE);

    class MessageModifier {
        private Map<Integer, Object> msgFields;
        private Map<Integer, Object> headerFields;
        private Map<Integer, Object> trailerFields;

        public MessageModifier()
        {
            msgFields = new HashMap<Integer, Object>();
            headerFields = new HashMap<Integer, Object>();
            trailerFields = new HashMap<Integer, Object>();
        }

        public boolean modifyOrder(Message order) {
            boolean modified = false;
            for (Integer field : msgFields.keySet()){
                modified = modifyOneField(order, field, msgFields.get(field)) || modified;
            }
            for (Integer field : headerFields.keySet()){
                modified = modifyOneField(order.getHeader(), field, headerFields.get(field)) || modified;
            }
            for (Integer field : trailerFields.keySet()){
                modified = modifyOneField(order.getTrailer(), field, trailerFields.get(field)) || modified;
            }
            return modified;
        }

        public void addDefaultField(int field, Object defaultValue, MessageFieldType fieldType) {
            switch(fieldType) {
                case MESSAGE:   msgFields.put(field, defaultValue);
                                break;
                case HEADER:    headerFields.put(field, defaultValue);
                                break;
                case TRAILER:   trailerFields.put(field, defaultValue);
                                break;
            }
        }

        /** Only put the field in if it's not present */
        protected boolean modifyOneField(FieldMap order, int field, Object defaultValue)
        {
            try {
                order.getField(new StringField(field));
                return false;
            } catch (FieldNotFound ex){
                order.setField(new StringField(field, defaultValue.toString()));
                return true;
            }
        }
    }

    private Map<String, MessageModifier> messageModifiers;

    public enum MessageFieldType { MESSAGE, HEADER, TRAILER }

    public DefaultOrderModifier()
    {
        messageModifiers = new HashMap<String, MessageModifier>();
    }

    public void init(ConfigData data) throws BackingStoreException
    {
    }

    public void setMsgFields(Map<String, String> fields) throws MarketceteraException {
        setFieldsHelper(fields, MessageFieldType.MESSAGE);
    }

    public void setHeaderFields(Map<String, String> fields) throws MarketceteraException {
        setFieldsHelper(fields, MessageFieldType.HEADER);
    }

    public void setTrailerFields(Map<String, String> fields) throws MarketceteraException {
        setFieldsHelper(fields, MessageFieldType.TRAILER);
    }

    public boolean modifyOrder(Message order) throws MarketceteraException {
        String msgType = null;
        boolean modified = false;

        try {
            msgType = order.getHeader().getString(MsgType.FIELD);
        } catch (FieldNotFound fieldNotFound) {
            // ignore
        }
        MessageModifier mod;
        if (msgType != null){
            msgType = msgType.toUpperCase();
            mod = messageModifiers.get(msgType);
            if (mod != null){
                modified = mod.modifyOrder(order) || modified;
            }
            if (FIXDataDictionaryManager.isAdminMessageType42(msgType)){
                mod = messageModifiers.get(ADMIN_MODIFIER_KEY);
                if (mod != null){
                    modified = mod.modifyOrder(order) || modified;
                }
            } else {
                mod = messageModifiers.get(APP_MODIFIER_KEY);
                if (mod != null){
                    modified = mod.modifyOrder(order)||modified;
                }
            }
        }
        mod = messageModifiers.get(GLOBAL_MODIFIER_KEY);
        if (mod!= null){
            modified = mod.modifyOrder(order)||modified;
        }
        return modified;
    }


    public void addDefaultField(int field, Object defaultValue, MessageFieldType fieldType)
    {
        addDefaultField(field, defaultValue, fieldType, null );
    }

    public void addDefaultField(int field, Object defaultValue, MessageFieldType fieldType, String predicate) {
        if (predicate == null){
            predicate = GLOBAL_MODIFIER_KEY;
        }
        predicate = predicate.toUpperCase();
        MessageModifier mod = messageModifiers.get(predicate);
        if (mod == null){
            mod = new MessageModifier();
            messageModifiers.put(predicate, mod);
        }
        mod.addDefaultField(field, defaultValue, fieldType);
    }

        /**
     * The fields are of form:
     * <fieldName>(predicate)=<fieldValue>
     * Where fieldName is an integer number.
     * So we parse out the field name, store it as an int, and store the value as an object.
     * The predicate is optional
     *
     * @param fields    Map of key-value pairs
     * @param fieldType       Which particular kind of field we are modifying: trailer/header/message
     */
    protected void setFieldsHelper(Map<String, String> fields, DefaultOrderModifier.MessageFieldType fieldType) throws MarketceteraException {
        Set<String> keys = fields.keySet();
        for (String oneKey : keys) {
            String value = fields.get(oneKey);
            Matcher defaultFieldsMatcher = DEFAULT_FIELDS_PATTERN.matcher(oneKey);
            String predicate = null;
            if (defaultFieldsMatcher.matches()) {
                int groupCount = defaultFieldsMatcher.groupCount();
                String fieldIDString = defaultFieldsMatcher.group(1);
                int fieldID = Integer.parseInt(fieldIDString);
                if (groupCount == 3) {
                    predicate = defaultFieldsMatcher.group(3);
                }
                addDefaultField(fieldID, value, fieldType, predicate);
            } else {
                throw new MarketceteraException(MessageKey.ORDER_MODIFIER_WRONG_FIELD_FORMAT.getLocalizedMessage(oneKey));
            }
        }
    }
}