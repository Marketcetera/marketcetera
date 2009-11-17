package org.marketcetera.ors.info;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A generic store of key-value pairs whose contents are checked
 * against certain conditions upon retrieval.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface ReadInfo
{

    /**
     * Returns the receiver's name.
     *
     * @return The name.
     */

    String getName();

    /**
     * Returns the receiver's path name.
     *
     * @return The path name.
     */

    String getPath();

    /**
     * Checks whether the given key is associated with a value (which
     * may or may not be null) in the receiver's map.
     *
     * @param key The key.
     *
     * @return Returns true if it is.
     */

    boolean contains
        (String key);

    /**
     * Returns the value associated with the given key in the
     * receiver's map.
     *
     * @param key The key.
     *
     * @return The value. It may be null, indicating either that the
     * given key is absent or that the null value is associated with
     * that key; use {@link #contains(String)} to distinguish these
     * two cases.
     */

    Object getValue
        (String key);

    /**
     * Returns the value associated with the given key in the
     * receiver's map, provided one (which may be null) is set.
     *
     * @param key The key.
     *
     * @return The value. It may be null.
     *
     * @throws InfoException Thrown if no value is associated with the
     * given key.
     */

    Object getValueIfSet
        (String key)
        throws InfoException;

    /**
     * Returns the value associated with the given key in the
     * receiver's map, provided one is set and is not null.
     *
     * @param key The key.
     *
     * @return The value.
     *
     * @throws InfoException Thrown if no or a null value is
     * associated with the given key.
     */

    Object getValueIfNonNull
        (String key)
        throws InfoException;

    /**
     * Returns the value associated with the given key in the
     * receiver's map, provided one is set and is either null or an
     * object of the given class.
     *
     * @param key The key.
     * @param cls The class.
     *
     * @return The value. It may be null.
     *
     * @throws InfoException Thrown if a no value is associated with
     * the given key, or if the value is non-null and of a class other
     * than the given one.
     */

    <T> T getValueIfInstanceOf
        (String key,
         Class<T> cls)
        throws InfoException;

    /**
     * Returns the value associated with the given key in the
     * receiver's map, provided one is set and is non-null and an
     * object of the given class.
     *
     * @param key The key.
     * @param cls The class.
     *
     * @return The value.
     *
     * @throws InfoException Thrown if a no value is associated with
     * the given key, or if the value is null or of a class other than
     * the given one.
     */

    <T> T getValueIfNonNullInstanceOf
        (String key,
         Class<T> cls)
        throws InfoException;
}
