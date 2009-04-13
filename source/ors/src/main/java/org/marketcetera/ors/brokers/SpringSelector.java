package org.marketcetera.ors.brokers;

import java.util.List;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The Spring-based configuration of the selector.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SpringSelector
{

    // INSTANCE DATA.

    private List<SpringSelectorEntry> mEntries;
    private SpringBroker mDefaultBroker;


    // INSTANCE METHODS.

    /**
     * Sets the configurations of the receiver's entries to the given
     * ones.
     *
     * @param entries The configurations. It may be null.
     */

    public void setEntries
        (List<SpringSelectorEntry> entries)
    {
        mEntries=entries;
    }

    /**
     * Returns the configurations of the receiver's entries.
     *
     * @return The configurations. It may be null.
     */

    public List<SpringSelectorEntry> getEntries()
    {
        return mEntries;
    }

    /**
     * Sets the receiver's default broker to the given one.
     *
     * @param defaultBroker The broker. It may be null.
     */

    public void setDefaultBroker
        (SpringBroker defaultBroker)
    {
        mDefaultBroker=defaultBroker;
    }

    /**
     * Returns the receiver's default broker.
     *
     * @return The broker. It may be null.
     */

    public SpringBroker getDefaultBroker()
    {
        return mDefaultBroker;
    }
}
