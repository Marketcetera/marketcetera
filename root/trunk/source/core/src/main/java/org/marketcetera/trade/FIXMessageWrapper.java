package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;
import quickfix.Field;
import quickfix.StringField;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/* $License$ */
/**
 * Base class for all messages that wrap a FIX Message.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$") //$NON-NLS-1$
class FIXMessageWrapper implements FIXMessageSupport {
    /**
     * Creates an instance.
     *
     * @param inMessage The FIX Message instance.
     */
    public FIXMessageWrapper(Message inMessage) {
        mMessage = inMessage;
    }

    /**
     * Creates an instance. This empty constructor is intended for use
     * by JAXB.
     */

    protected FIXMessageWrapper() {
        mMessage = null;
    }

    @Override
    public Message getMessage() {
        return mMessage;
    }

    @Override
    public synchronized Map<Integer, String> getFields() {
        if(mFields == null) {
            Map<Integer, String> map = new HashMap<Integer, String>();
            Iterator<Field<?>> iterator = getMessage().iterator();
            while(iterator.hasNext()) {
                Field<?> f = iterator.next();
                if(f instanceof StringField) {
                    map.put(f.getTag(),((StringField)f).getValue());
                }
            }
            mFields = Collections.unmodifiableMap(map);
        }
        return mFields;
    }
    private transient Map<Integer,String> mFields;
    @XmlJavaTypeAdapter(FIXMessageAdapter.class)
    private final Message mMessage;
    private static final long serialVersionUID = 1L;
}
