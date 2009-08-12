package org.marketcetera.ors.info;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A generic store of key-value pairs whose contents are checked
 * against certain conditions during management operations.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface ReadWriteInfo
    extends ReadInfo
{

    /**
     * Sets the value associated with the given key to the given value
     * in the receiver's map.
     *
     * @param key The key.
     * @param value The value. It may be null.
     */

    void setValue
        (String key,
         Object value);

    /**
     * Sets the value associated with the given key to the given value
     * in the receiver's map.
     *
     * @param key The key.
     * @param value The value. It may be null.
     *
     * @throws InfoException Thrown if a value is already associated
     * with the given key.
     */

    void setValueIfUnset
        (String key,
         Object value)
        throws InfoException;

    /**
     * Removes the value associated with the given key in the
     * receiver's map. It is a no-op if there is no value associated
     * with the given key.
     *
     * @param key The key.
     */

    void removeValue
        (String key);

    /**
     * Removes the value associated with the given key in the
     * receiver's map.
     *
     * @param key The key.
     *
     * @throws InfoException Thrown if no value is associated with the
     * given key.
     */

    void removeValueIfSet
        (String key)
        throws InfoException;
}
