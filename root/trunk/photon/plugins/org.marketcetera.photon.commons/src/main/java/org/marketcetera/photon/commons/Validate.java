package org.marketcetera.photon.commons;

import java.text.MessageFormat;
import java.util.Collection;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Validation utilities.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public final class Validate {

    /**
     * Validate some objects, throwing IllegalArgumentException if any are null.
     * <p>
     * This method requires each object to validate to be followed by a String
     * describing the object. For example:
     * 
     * <pre>
     * public void foo(MyObject a, OtherObject b) {
     *    Validate.notNull(a, &quot;a&quot;, b, &quot;b&quot;);
     *    ... use a and b
     * }
     * </pre>
     * 
     * If "a" is null, an IllegalArgumentException with the message
     * "'a' must not be null" will be thrown.
     * 
     * @param objects
     *            the objects to validate and their descriptions
     * @throws IllegalArgumentException
     *             if any object is null, or if the objects do not have
     *             corresponding descriptions
     */
    public static void notNull(Object... objects) {
        for (int i = 0; i < objects.length; i += 2) {
            if (i + 1 == objects.length) {
                throw new IllegalArgumentException(
                        MessageFormat
                                .format(
                                        "improper usage of Validate.notNull: parameter at index {0} has no description", //$NON-NLS-1$
                                        i));
            }
            if (!(objects[i + 1] instanceof String)) {
                throw new IllegalArgumentException(
                        MessageFormat
                                .format(
                                        "improper usage of Validate.notNull: parameter at index {0} is not a String", //$NON-NLS-1$
                                        i + 1));
            }
            if (objects[i] == null) {
                throw new IllegalArgumentException(MessageFormat.format(
                        "''{0}'' must not be null", objects[i + 1])); //$NON-NLS-1$
            }
        }
    }

    /**
     * Validates an array of objects, throwing IllegalArgumentException if it or
     * any of its elements are null.
     * <p>
     * For example:
     * 
     * <pre>
     * public void foo(MyObject[] a) {
     *    Validate.noNullElements(a, &quot;a&quot;);
     *    ... use a
     * }
     * </pre>
     * 
     * If "a" is null, an IllegalArgumentException with the message
     * "'a' must not be null" will be thrown. If any element in "a" is null, an
     * IllegalArgumentException with the message
     * "'a' must not have null elements" will be thrown.
     * 
     * @param array
     *            the array to validate
     * @param description
     *            the description of the array for error messages
     * @throws IllegalArgumentException
     *             if array is null, or any array element is null
     */
    public static void noNullElements(Object[] array, String description) {
        Validate.notNull(array, description);
        for (Object object : array) {
            if (object == null) {
                throw new IllegalArgumentException(MessageFormat.format(
                        "''{0}'' must not have null elements", description)); //$NON-NLS-1$
            }
        }
    }

    /**
     * Convenience for {@code Validate.noNullElements(collection.toArray(),
     * description)}.
     * 
     * @param collection
     *            the collection to validate
     * @param description
     *            the description of the collection for error messages
     * @throws IllegalArgumentException
     *             if collection is null, or any collection element is null
     */
    public static void noNullElements(Collection<?> collection,
            String description) {
        Validate.notNull(collection, description);
        Validate.noNullElements(collection.toArray(), description);
    }

    /**
     * Validates an array of objects is not empty, throwing
     * IllegalArgumentException otherwise.
     * <p>
     * For example:
     * 
     * <pre>
     * public void foo(MyObject[] a) {
     *    Validate.notEmpty(a, &quot;a&quot;);
     *    ... use a
     * }
     * </pre>
     * 
     * If "a" is null, an IllegalArgumentException with the message
     * "'a' must not be null" will be thrown. If "a" is empty, an
     * IllegalArgumentException with the message "'a' must not be empty" will be
     * thrown.
     * 
     * @param array
     *            the array to validate
     * @param description
     *            the description of the array for error messages
     * @throws IllegalArgumentException
     *             if array is null or empty
     */
    public static void notEmpty(Object[] array, String description) {
        Validate.notNull(array, description);
        if (array.length == 0) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "''{0}'' must not be empty", description)); //$NON-NLS-1$
        }
    }

    /**
     * Convenience for {@code Validate.notEmpty(collection.toArray(),
     * description)}.
     * 
     * @param collection
     *            the collection to validate
     * @param description
     *            the description of the collection for error messages
     * @throws IllegalArgumentException
     *             if collection is null or empty
     */
    public static void notEmpty(Collection<?> collection, String description) {
        Validate.notNull(collection, description);
        Validate.notEmpty(collection.toArray(), description);
    }

    /**
     * Validates an array of objects is not empty, and has no null elements.
     * This is a convenience for:
     * 
     * <pre>
     * Validate.notEmpty(array, description);
     * Validate.noNullElements(array, description);
     * </pre>
     * 
     * @param array
     *            the array to validate
     * @param description
     *            the description of the array for error messages
     * @throws IllegalArgumentException
     *             if array is null, empty, or has null elements
     */
    public static void nonNullElements(Object[] array, String description) {
        Validate.notEmpty(array, description);
        Validate.noNullElements(array, description);
    }

    /**
     * Convenience for {@code Validate.nonNullElements(collection.toArray(),
     * description)}.
     * 
     * @param collection
     *            the collection to validate
     * @param description
     *            the description of the collection for error messages
     * @throws IllegalArgumentException
     *             if collection is null, empty, or has null elements
     */
    public static void nonNullElements(Collection<?> collection,
            String description) {
        Validate.notNull(collection, description);
        Validate.nonNullElements(collection.toArray(), description);
    }

    private Validate() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
