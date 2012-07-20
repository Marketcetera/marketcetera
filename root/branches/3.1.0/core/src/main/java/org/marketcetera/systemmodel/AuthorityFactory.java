package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link Authority} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AuthorityFactory.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: AuthorityFactory.java 82384 2012-07-20 19:09:59Z colin $")
public interface AuthorityFactory
{
    /**
     * Creates an <code>Authority</code> with the given name.
     *
     * @param inAuthorityName a <code>String</code> value
     * @return an <code>Authority</code> value
     */
    public Authority create(String inAuthorityName);
    /**
     * Creates an <code>Authority</code> object.
     *
     * @return an <code>Authority</code> value
     */
    public Authority create();
}
