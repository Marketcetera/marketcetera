package org.marketcetera.util.ws.tags;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A version ID. Each set of collaborating web service classes has a
 * unique version ID, covering all services together. Accordingly, one
 * JVM-wide ID, {@link #SELF}, suffices to describe the version
 * "spoken" by all classes in the JVM, and there is no need to create
 * new instances. However, additional instances of this class may
 * appear in a JVM when the version of another potential collaborator
 * is marshalled to the JVM.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class VersionId
        extends Tag
{
    /**
     * Creates a new version ID with the given ID value.
     *
     * @param value a <code>String</code> value
     */
    public VersionId(String value)
    {
        super(value);
    }
    /**
     * Create a new VersionId instance.
     * 
     * This empty constructor is intended for use by JAXB.
     */
    protected VersionId() {}
    /**
     * The JVM-wide version ID.
     */
    public static final VersionId SELF = new VersionId("1.0.0"); //$NON-NLS-1$
    private static final long serialVersionUID=1L;
}
