package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;


/* $License$ */
/**
 * A handle that identifies a request. This handle can be used
 * to pause / cancel the request.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class RequestID {

    /**
     * Creates an instance.
     *
     * @param inValue the string value of this ID.
     */
    RequestID(String inValue) {
        if(inValue == null) {
            throw new NullPointerException();
        }
        mValue = inValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestID requestID = (RequestID) o;

        return mValue == null
                ? requestID.mValue == null
                : mValue.equals(requestID.mValue);

    }

    @Override
    public int hashCode() {
        return (mValue != null ? mValue.hashCode() : 0);
    }

    @Override
    public String toString() {
        return mValue;
    }

    private final String mValue;
}
