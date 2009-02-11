package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.io.Serializable;
import java.beans.ConstructorProperties;

/* $License$ */
/**
 * Instances of this class uniquely identify a data flow.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")   //$NON-NLS-1$
public final class DataFlowID implements Serializable {
    /**
     * Creates an instance.
     *
     * @param inValue the string value of the ID, the value cannot be null.
     */
    @ConstructorProperties({"value"})  //$NON-NLS-1$
    public DataFlowID(String inValue) {
        if(inValue == null) {
            throw new NullPointerException();
        }
        mValue = inValue;
    }

    /**
     * The string value of the ID.
     *
     * @return the string value of the ID.
     */
    public String getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public int hashCode() {
        return mValue.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataFlowID that = (DataFlowID) o;

        return mValue == null
                ? that.mValue == null
                : mValue.equals(that.mValue); 

    }

    private String mValue;
    private static final long serialVersionUID = -117964306857928984L;
}
