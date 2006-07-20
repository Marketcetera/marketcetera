package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigData;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;

import java.util.HashMap;
import java.util.prefs.BackingStoreException;

/**
 * Takes in a collection of message/header/trailer fields to always
 * modify on a passed-in message
 * @author gmiller
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DefaultOrderModifier implements OrderModifier {
    private HashMap<Integer, Object> msgFields;
    private HashMap<Integer, Object> headerFields;
    private HashMap<Integer, Object> trailerFields;

    public enum MessageFieldType { MESSAGE, HEADER, TRAILER };

    public DefaultOrderModifier()
    {
        msgFields = new HashMap<Integer, Object>();
        headerFields = new HashMap<Integer, Object>();
        trailerFields = new HashMap<Integer, Object>();
    }

    public void addDefaultField(int field, Object defaultValue, MessageFieldType fieldType)
    {
        switch(fieldType) {
            case MESSAGE:   msgFields.put(field, defaultValue);
                            break;
            case HEADER:    headerFields.put(field, defaultValue);
                            break;
            case TRAILER:   trailerFields.put(field, defaultValue);
                            break;
        }
    }

    public void init(ConfigData data) throws BackingStoreException
    {
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