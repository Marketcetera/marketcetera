package org.marketcetera.client.dest;

import java.io.Serializable;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The web service representation of a single destination's status.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class DestinationStatus
    implements Serializable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private String mName;
    private DestinationID mId;
    private boolean mLoggedOn;


    // CONSTRUCTORS.

    /**
     * Creates a new status representation, given the destination
     * information.
     *
     * @param name The destination name.
     * @param id The destination ID.
     * @param loggedOn The logon flag.
     */

    public DestinationStatus
        (String name,
         DestinationID id,
         boolean loggedOn)
    {
        setName(name);
        setId(id);
        setLoggedOn(loggedOn);
    }

    /**
     * Creates a new status representation. This empty constructor is
     * intended for use by JAXB.
     */

    protected DestinationStatus() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's name to the given value.
     *
     * @param name The name.
     */

    public void setName
        (String name)
    {
        mName=name;
    }

    /**
     * Returns the receiver's name.
     *
     * @return The name.
     */

    public String getName()
    {
        return mName;
    }

    /**
     * Sets the receiver's destination ID to the given value,
     *
     * @param id The ID.
     */

    public void setId
        (DestinationID id)
    {
        mId=id;
    }

    /**
     * Returns the receiver's destination ID.
     *
     * @return The ID.
     */

    public DestinationID getId()
    {
        return mId;
    }

    /**
     * Sets the receiver's logon flag to the given value.
     *
     * @param loggedOn The flag.
     */

    public void setLoggedOn
        (boolean loggedOn)
    {
        mLoggedOn=loggedOn;
    }

    /**
     * Returns the receiver's logon flag.
     *
     * @return The flag.
     */

    public boolean getLoggedOn()
    {
        return mLoggedOn;
    }
}
