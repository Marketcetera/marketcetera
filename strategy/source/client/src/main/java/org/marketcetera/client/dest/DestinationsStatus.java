package org.marketcetera.client.dest;

import java.util.List;
import java.util.Collections;

import org.marketcetera.util.misc.ClassVersion;

/**
 * The collective web service representation of the status of all
 * destinations.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class DestinationsStatus
{

    // INSTANCE DATA.

    private List<DestinationStatus> mDestinations;


    // CONSTRUCTORS.

    /**
     * Creates a new collective status representation, given the
     * status of the destinations.
     *
     * @param destinations The status.
     */

    public DestinationsStatus
        (List<DestinationStatus> destinations)
    {
        setDestinations(destinations);
    }

    /**
     * Creates a new collective status representation. This empty
     * constructor is intended for use by JAXB.
     */

    protected DestinationsStatus() {}


    // INSTANCE METHODS.

    /**
     * Sets the status of the receiver's destinations to the given
     * one.
     *
     * @param destinations The status.
     */

    public void setDestinations
        (List<DestinationStatus> destinations)
    {
        mDestinations=Collections.unmodifiableList(destinations);
    }

    /**
     * Returns the status of the receiver's destinations. The returned
     * list is not modifiable.
     *
     * @return The status.
     */

    public List<DestinationStatus> getDestinations()
    {
        return mDestinations;
    }
}
