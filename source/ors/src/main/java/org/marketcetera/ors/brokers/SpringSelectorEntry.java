package org.marketcetera.ors.brokers;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;

/**
 * The Spring-based configuration of a selector entry.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class SpringSelectorEntry
    implements InitializingBean
{

    // INSTANCE DATA.

    private String mTargetType;
    private SpringDestination mDestination;
    private boolean mSkipIfUnavailable;


    // INSTANCE METHODS.

    /**
     * Sets the receiver's target type to the given string form value.
     *
     * @param targetType The target type.
     */

    public void setTargetType
        (String targetType)
    {
        mTargetType=targetType;
    }

    /**
     * Returns the receiver's target type, in string form.
     *
     * @return The target type.
     */

    public String getTargetType()
    {
        return mTargetType;
    }

    /**
     * Sets the receiver's destination to the given one.
     *
     * @param destination The destination.
     */

    public void setDestination
        (SpringDestination destination)
    {
        mDestination=destination;
    }

    /**
     * Returns the receiver's destination.
     *
     * @return The destination.
     */

    public SpringDestination getDestination()
    {
        return mDestination;
    }

    /**
     * Sets the receiver's skip-if-unavailable flag to the given
     * value.
     *
     * @param skipIfUnavailable The flag.
     */

    public void setSkipIfUnavailable
        (boolean skipIfUnavailable)
    {
        mSkipIfUnavailable=skipIfUnavailable;
    }

    /**
     * Returns the receiver's skip-if-unavailable flag.
     *
     * @return The flag.
     */

    public boolean getSkipIfUnavailable()
    {
        return mSkipIfUnavailable;
    }


    // InitializingBean.

    @Override
    public void afterPropertiesSet()
        throws I18NException
    {
        if (getTargetType()==null) {
            throw new I18NException(Messages.NO_TARGET_TYPE);
        }
        if (getDestination()==null) {
            throw new I18NException(Messages.NO_BROKER);
        }
    }
}
