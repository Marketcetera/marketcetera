package org.marketcetera.util.auth;

import java.util.Arrays;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A holder of a character array.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class HolderCharArray
    extends Holder<char[]>
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see Holder#Holder(I18NBoundMessage)
     */

    public HolderCharArray
        (I18NBoundMessage message)
    {
        super(message);
    }

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see Holder#Holder()
     */

    public HolderCharArray() {}


    // Holder.

    /**
     * Sets the receiver's data to the given value, after clearing the
     * previous data via {@link #clear()}.
     */

    @Override
    public void setValue
        (char[] value)
    {
        clear();
        super.setValue(value);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's data as a string. This method should not
     * be called if it is important to ensure that the holder data
     * (such as a password) can be completely removed from memory
     * using {@link #clear()}; this is because the string created and
     * returned by this method cannot be zeroed out.
     *
     * @return The data, which is null if the receiver's data is not
     * set.
     */

    public String getValueAsString()
    {
        char[] value=getValue();
        if (value!=null) {
            return new String(value);
        }
        return null;
    }

    /**
     * Clears the receiver's data by first overwriting all prior
     * characters with the nul ('\0') character (if the receiver had
     * non-null data), and then setting the data to null.
     */

    public void clear()
    {
        char[] value=getValue();
        if (value!=null) {
            Arrays.fill(value,'\0'); //$NON-NLS-1$
            super.setValue(null);
        }
    }
}
