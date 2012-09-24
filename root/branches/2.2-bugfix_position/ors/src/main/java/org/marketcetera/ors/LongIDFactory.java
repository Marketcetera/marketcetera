package org.marketcetera.ors;

import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A long ID factory. It relies on a block factory to assign blocks,
 * and then locally assigns IDs within that block. The block factory
 * must generate numeric IDs.
 *
 * @author tlerios@marketcetera.com
 * @since 2.1.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class LongIDFactory
{

    // CLASS DATA.

    /**
     * The count of local IDs assigned per block.
     */

    public static final long LOCAL_ID_COUNT=
        1000;


    // INSTANCE DATA.

    private final IDFactory mBlockFactory;
    private long mBlockID;
    private long mLocalID=0;


    // CONSTRUCTOR.

    /**
     * Creates a new long ID factory that relies on the given block
     * factory, which must generate numeric IDs.
     *
     * @param blockFactory The block factory.
     */

    public LongIDFactory
        (IDFactory blockFactory)
    {
        mBlockFactory=blockFactory;
        mLocalID=LOCAL_ID_COUNT-1;
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
        mBlockID=Long.parseLong(getBlockFactory().getNext())*LOCAL_ID_COUNT;
        mLocalID=0;
    }

    /**
     * Gets the next ID from the receiver.
     *
     * @return The ID.
     *
     * @throws NoMoreIDsException Thrown if retrieval fails.
     */

    public synchronized long getNext()
        throws NoMoreIDsException
    {
        mLocalID++;
        if (mLocalID==LOCAL_ID_COUNT) {
            getNextBlock();
        }
        return mBlockID+mLocalID;
    }
}
