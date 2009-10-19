package org.marketcetera.ors.info;

import java.util.concurrent.atomic.AtomicInteger;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A generator of unique names comprising a prefix, a separator, and a
 * monotonically increasing integer. Uniqueness is guaranteed only in
 * the context of the current process and provided no more than {@link
 * Integer#MAX_VALUE} names are generated.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class NameGenerator
{

    // CLASS DATA.

    /**
     * The separator between the prefix and the integer.
     */

    public static final String INT_SEPARATOR=
        "_"; //$NON-NLS-1$

    private static final String INT_FORMAT=
        "%010d"; //$NON-NLS-1$


    // INSTANCE DATA.

    private final String mPrefix;
    private final AtomicInteger mNextInt;


    // CONSTRUCTORS.

    /**
     * Creates a new name generator that uses the given prefix.
     *
     * @param prefix The prefix.
     */

    public NameGenerator
        (String prefix)
    {
        mPrefix=prefix;
        mNextInt=new AtomicInteger(1);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's prefix.
     *
     * @return The prefix.
     */

    private String getPrefix()
    {
        return mPrefix;
    }

    /**
     * Returns the receiver's next integer.
     *
     * @return The next integer.
     */

    private AtomicInteger getNextInt()
    {
        return mNextInt;
    }

    /**
     * Returns the receiver's next generated name.
     *
     * @return The name.
     */

    public String getNextName()
    {
        StringBuilder builder=new StringBuilder();
        builder.append(getPrefix());
        builder.append(INT_SEPARATOR);
        builder.append
            (String.format(INT_FORMAT,getNextInt().getAndIncrement()));
        return builder.toString();
    }
}
