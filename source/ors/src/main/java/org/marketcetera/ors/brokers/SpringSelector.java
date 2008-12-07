package org.marketcetera.ors.brokers;

import java.util.List;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The Spring-based configuration of the selector.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class SpringSelector
{

    // INSTANCE DATA.

    private List<SpringSelectorEntry> mEntries;
    private SpringBroker mDefaultDestination;


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
     * Sets the receiver's default destination to the given one.
     *
     * @param defaultDestination The destination. It may be null.
     */

    public void setDefaultDestination
        (SpringBroker defaultDestination)
    {
        mDefaultDestination=defaultDestination;
    }

    /**
     * Returns the receiver's default destination.
     *
     * @return The destination. It may be null.
     */

    public SpringBroker getDefaultDestination()
    {
        return mDefaultDestination;
    }
}
