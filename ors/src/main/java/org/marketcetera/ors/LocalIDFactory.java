package org.marketcetera.ors;

import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A local ID factory. It relies on a block factory to assign blocks,
 * and then locally assigns IDs within that block.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class LocalIDFactory
    implements IDFactory
{

    // CLASS DATA.

    /**
     * The count of local IDs assigned per block.
     */

    public static final int LOCAL_ID_COUNT=
        1000;


    // INSTANCE DATA.

    private final IDFactory mBlockFactory;
    private String mBlockID;
    private int mLocalID=0;


    // CONSTRUCTOR.

    /**
     * Creates a new local ID factory that relies on the given block
     * factory.
     *
     * @param blockFactory The block factory.
     */

    public LocalIDFactory
        (IDFactory blockFactory)
    {
        mBlockFactory=blockFactory;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's block factory.
     *
     * @return The factory.
     */

    public IDFactory getBlockFactory()
    {
        return mBlockFactory;
    }

    /**
     * Retrieves the next block from the receiver's block factory, and
     * resets the local ID.
     *
     * @throws NoMoreIDsException Thrown if retrieval fails.
     */

    private void getNextBlock()
        throws NoMoreIDsException
    {
        mBlockID=getBlockFactory().getNext();
        mLocalID=0;
    }


    // IDFactory.

    @Override
    public void init()
        throws ClassNotFoundException,
               NoMoreIDsException
    {
        getNextBlock();
    }

    @Override
    public synchronized String getNext()
        throws NoMoreIDsException
    {
        mLocalID++;
        if (mLocalID==LOCAL_ID_COUNT) {
            getNextBlock();
        }
        return String.format("%1$s%2$03d",mBlockID,mLocalID); //$NON-NLS-1$
    }
}
