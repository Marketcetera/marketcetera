package org.marketcetera.ors.brokers;

import java.util.ArrayList;
import java.util.List;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.SpringSessionDescriptor;
import org.marketcetera.util.quickfix.SpringSessionSettings;
import org.springframework.beans.factory.InitializingBean;

/**
 * The collective Spring-based configuration of all destinations.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class SpringBrokers
    implements InitializingBean
{

    // INSTANCE DATA.

    private SpringSessionSettings mSessionSettings;
    private List<SpringBroker> mDestinations;


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
     * Sets the configurations of the receiver's destinations to the
     * given ones.
     *
     * @param destinations The configurations.
     */

    public void setDestinations
        (List<SpringBroker> destinations)
    {
        mDestinations=destinations;
    }

    /**
     * Returns the configurations of the receiver's destinations.
     *
     * @return The configurations.
     */

    public List<SpringBroker> getDestinations()
    {
        return mDestinations;
    }


    // InitializingBean.

    @Override
    public void afterPropertiesSet()
        throws I18NException
    {
        if (getSettings()==null) {
            throw new I18NException(Messages.NO_SETTINGS);
        }
        if (getDestinations()==null) {
            throw new I18NException(Messages.NO_BROKERS);
        }
        List<SpringSessionDescriptor> list=
            new ArrayList<SpringSessionDescriptor>(getDestinations().size());
        for (SpringBroker d:getDestinations()) {
            list.add(d.getDescriptor());
        }
        getSettings().setDescriptors(list);
    }
}
