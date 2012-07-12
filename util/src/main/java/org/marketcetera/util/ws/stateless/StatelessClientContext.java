package org.marketcetera.util.ws.stateless;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.NodeId;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;

/**
 * The client context which the client must supply as an argument to
 * every stateless remote call. {@link StatelessClient#getContext()}
 * is the preferred way for service clients to obtain a ready-to-use
 * context. It conveys key (but optional) information about the client
 * to the server.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class StatelessClientContext
{

    // INSTANCE DATA.

    private VersionId mVersionId;
    private AppId mAppId;
    private NodeId mClientId;
    private LocaleWrapper mLocale;


    // INSTANCE METHODS.

    /**
     * Sets the receiver's version ID to the given one.
     *
     * @param versionId The version ID, which may be null.
     */

    public void setVersionId
        (VersionId versionId)
    {
        mVersionId=versionId;
    }
 
    /**
     * Returns the receiver's version ID.
     *
     * @return The version ID, which may be null.
     */

    public VersionId getVersionId()
    {
        return mVersionId;
    }   

    /**
     * Sets the receiver's application ID to the given one.
     *
     * @param appId The application ID, which may be null.
     */

    public void setAppId
        (AppId appId)
    {
        mAppId=appId;
    }
 
    /**
     * Returns the receiver's application ID.
     *
     * @return The application ID, which may be null.
     */

    public AppId getAppId()
    {
        return mAppId;
    }   

    /**
     * Sets the receiver's client ID to the given one.
     *
     * @param clientId The client ID, which may be null.
     */

    public void setClientId
        (NodeId clientId)
    {
        mClientId=clientId;
    }

   /**
     * Returns the receiver's client ID.
     *
     * @return The client ID, which may be null.
     */

    public NodeId getClientId()
    {
        return mClientId;
    }   

    /**
     * Sets the receiver's (wrapped) locale to the given one.
     *
     * @param locale The (wrapped) locale, which may be null.
     */

    public void setLocale
        (LocaleWrapper locale)
    {
        mLocale=locale;
    }

    /**
     * Returns the receiver's (wrapped) locale.
     *
     * @return The (wrapped) locale, which may be null.
     */

    public LocaleWrapper getLocale()
    {
        return mLocale;
    }   


    // Object.

    @Override
    public String toString()
    {
        return Messages.STATELESS_CLIENT_CONTEXT.getText
            (getVersionId(),getAppId(),getClientId(),
             ((getLocale()==null) || (getLocale().getRaw()==null))?
             StringUtils.EMPTY:getLocale().getRaw().toString());
    }

    @Override
    public int hashCode()
    {
        return (ObjectUtils.hashCode(getVersionId())+
                ObjectUtils.hashCode(getAppId())+
                ObjectUtils.hashCode(getClientId())+
                ObjectUtils.hashCode(getLocale()));
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        StatelessClientContext o=(StatelessClientContext)other;
        return (ObjectUtils.equals(getVersionId(),o.getVersionId()) &&
                ObjectUtils.equals(getAppId(),o.getAppId()) &&
                ObjectUtils.equals(getClientId(),o.getClientId()) &&
                ObjectUtils.equals(getLocale(),o.getLocale()));                
    }
}
