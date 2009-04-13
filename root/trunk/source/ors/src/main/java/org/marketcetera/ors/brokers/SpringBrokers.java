package org.marketcetera.ors.brokers;

import java.util.ArrayList;
import java.util.List;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.SpringSessionDescriptor;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import org.springframework.beans.factory.InitializingBean;

/**
 * The collective Spring-based configuration of all brokers.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SpringBrokers
    implements InitializingBean
{

    // INSTANCE DATA.

    private SpringSessionSettings mSessionSettings;
    private List<SpringBroker> mBrokers;


    // INSTANCE METHODS.

    /**
     * Sets the configuration of the receiver's QuickFIX/J session
     * settings to the given one.
     *
     * @param sessionSettings The configuration.
     */

    public void setSettings
        (SpringSessionSettings sessionSettings)
    {
        mSessionSettings=sessionSettings;
    }

    /**
     * Returns the configuration of the receiver's QuickFIX/J session
     * settings.
     *
     * @return The configuration.
     */

    public SpringSessionSettings getSettings()
    {
        return mSessionSettings;
    }

    /**
     * Sets the configurations of the receiver's brokers to the given
     * ones.
     *
     * @param brokers The configurations.
     */

    public void setBrokers
        (List<SpringBroker> brokers)
    {
        mBrokers=brokers;
    }

    /**
     * Returns the configurations of the receiver's brokers.
     *
     * @return The configurations.
     */

    public List<SpringBroker> getBrokers()
    {
        return mBrokers;
    }


    // InitializingBean.

    @Override
    public void afterPropertiesSet()
        throws I18NException
    {
        if (getSettings()==null) {
            throw new I18NException(Messages.NO_SETTINGS);
        }
        if (getBrokers()==null) {
            throw new I18NException(Messages.NO_BROKERS);
        }
        List<SpringSessionDescriptor> list=
            new ArrayList<SpringSessionDescriptor>(getBrokers().size());
        for (SpringBroker b:getBrokers()) {
            list.add(b.getDescriptor());
        }
        getSettings().setDescriptors(list);
    }
}
