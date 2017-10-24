package org.marketcetera.admin;

/* $License$ */

/**
 * Creates {@link MutableRole} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableRoleFactory
        extends RoleFactory
{
    /**
     * Create a <code>MutableRole</code> object with the given attributes.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @return a <code>MutableRole</code> value
     */
    MutableRole create(String inName,
                       String inDescription);
    /**
     * Create a <code>MutableRole</code> object.
     *
     * @return a <code>MutableRole</code> value
     */
    MutableRole create();
}
