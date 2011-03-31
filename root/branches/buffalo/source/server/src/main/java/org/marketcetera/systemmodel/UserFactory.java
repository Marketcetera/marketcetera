package org.marketcetera.systemmodel;

import java.util.Properties;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface UserFactory
{
    /**
     * 
     *
     *
     * @param inName
     * @param inDescription
     * @param inHashedPassword
     * @param inActive
     * @param inUserData
     * @return
     */
    public User create(String inName,
                       String inDescription,
                       String inHashedPassword,
                       boolean inActive,
                       Properties inUserData);
}
