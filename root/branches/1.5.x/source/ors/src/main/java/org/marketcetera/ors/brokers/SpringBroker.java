package org.marketcetera.ors.brokers;

import org.marketcetera.ors.filters.MessageModifierManager;
import org.marketcetera.ors.filters.MessageRouteManager;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.SpringSessionDescriptor;
import org.springframework.beans.factory.InitializingBean;

/**
 * The Spring-based configuration of a single broker.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SpringBroker
    implements InitializingBean
{

    // INSTANCE DATA.

    private SpringSessionDescriptor mSessionDescriptor;
    private String mName;
    private String mId;
    private MessageModifierManager mModifiers;
    private MessageModifierManager mPreSendModifiers;
    private MessageRouteManager mRoutes;


    // INSTANCE METHODS.

    /**
     * Sets the configuration of the receiver's QuickFIX/J session
     * descriptor to the given one.
     *
     * @param sessionDescriptor The configuration.
     */

    public void setDescriptor
        (SpringSessionDescriptor sessionDescriptor)
    {
        mSessionDescriptor=sessionDescriptor;
    }

    /**
     * Returns the configuration of the receiver's QuickFIX/J session
     * descriptor.
     *
     * @return The configuration.
     */

    public SpringSessionDescriptor getDescriptor()
    {
        return mSessionDescriptor;
    }

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
     * Sets the receiver's broker ID to the given string form value.
     *
     * @param id The ID.
     */

    public void setId
        (String id)
    {
        mId=id;
    }

    /**
     * Returns the receiver's broker ID, in string form.
     *
     * @return The ID.
     */

    public String getId()
    {
        return mId;
    }

    /**
     * Sets the receiver's message modifier manager to the given one.
     *
     * @param modifiers The manager. It may be null.
     */

    public void setModifiers
        (MessageModifierManager modifiers)
    {
        mModifiers=modifiers;
    }

    /**
     * Returns the receiver's message modifier manager.
     *
     * @return The manager. It may be null.
     */

    public MessageModifierManager getModifiers()
    {
        return mModifiers;
    }

    /**
     * Sets the receiver's route manager to the given one.
     *
     * @param routes The manager. It may be null.
     */

    public void setRoutes
        (MessageRouteManager routes)
    {
        mRoutes=routes;
    }

    /**
     * Returns the receiver's route manager.
     *
     * @return The manager. It may be null.
     */

    public MessageRouteManager getRoutes()
    {
        return mRoutes;
    }

    /**
     * Sets the receiver's pre-sending message modifier manager to the
     * given one.
     *
     * @param preSendModifiers The manager. It may be null.
     */

    public void setPreSendModifiers
        (MessageModifierManager preSendModifiers)
    {
        mPreSendModifiers=preSendModifiers;
    }

    /**
     * Returns the receiver's pre-sending message modifier manager.
     *
     * @return The manager. It may be null.
     */

    public MessageModifierManager getPreSendModifiers()
    {
        return mPreSendModifiers;
    }


    // InitializingBean.

    @Override
    public void afterPropertiesSet()
        throws I18NException
    {
        if (getDescriptor()==null) {
            throw new I18NException(Messages.NO_DESCRIPTOR);
        }
        if (getName()==null) {
            throw new I18NException(Messages.NO_NAME);
        }
        if (getId()==null) {
            throw new I18NException(Messages.NO_ID);
        }
    }
}
