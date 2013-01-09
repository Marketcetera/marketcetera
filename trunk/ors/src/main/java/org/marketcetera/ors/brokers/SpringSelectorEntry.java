package org.marketcetera.ors.brokers;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;

/**
 * The Spring-based configuration of a selector entry.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SpringSelectorEntry
    implements InitializingBean
{

    // INSTANCE DATA.

    private String mTargetType;
    private SpringBroker mBroker;
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
     * Sets the receiver's broker to the given one.
     *
     * @param broker The broker.
     */

    public void setBroker
        (SpringBroker broker)
    {
        mBroker=broker;
    }

    /**
     * Returns the receiver's broker.
     *
     * @return The broker.
     */

    public SpringBroker getBroker()
    {
        return mBroker;
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
        if (getBroker()==null) {
            throw new I18NException(Messages.NO_BROKER);
        }
    }
}
