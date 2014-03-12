package org.marketcetera.util.ws.stateless;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.wrappers.LocaleWrapper;

/**
 * A client node for stateless communication. Its (optional)
 * application ID is that of the application which hosts the client.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class StatelessClient
    extends Node
{

    // INSTANCE DATA.

    private final AppId mAppId;
    /**
     * context classes to add to the client context, if any
     */
    private final ContextClassProvider contextClassProvider;
    
    // CONSTRUCTORS.
    
    /**
     * Create a new StatelessClient instance.
     *
     * @param inHost a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inAppId an <code>AppId</code> value
     * @param inContextClasses a <code>ContextClassProvider</code> value
     */
    public StatelessClient(String inHost,
                           int inPort,
                           AppId inAppId,
                           ContextClassProvider inContextClasses)
    {
        super(inHost,
              inPort);
        mAppId=inAppId;
        contextClassProvider = inContextClasses;
    }
    /**
     * Creates a new client node with the given server host name,
     * port, and client application ID.
     *
     * @param host The host name.
     * @param port The port.
     * @param appId The application ID, which may be null.
     */    

    public StatelessClient(String host,
                           int port,
                           AppId appId)
    {
        this(host,
             port,
             appId,
             null);
    }

    /**
     * Creates a new client node with the default server host name and
     * port, and the given client application ID.
     *
     * @param appId The application ID, which may be null.
     */    

    public StatelessClient
        (AppId appId)
    {
        this(DEFAULT_HOST,DEFAULT_PORT,appId);
    }

    /**
     * Creates a new client node with the default server host name and
     * port, and no client application ID.
     */    

    public StatelessClient()
    {
        this(null);
    }


    // INSTANCE METHODS.
 
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
     * Sets the properties of the given client context, so that it may
     * be used for remote calls made via proxies obtained from the
     * receiver.
     *
     * @param context The context.
     */

    protected void fillContext
        (StatelessClientContext context)
    {
        context.setVersionId(VersionId.SELF);
        context.setAppId(getAppId());
        context.setClientId(getId());
        context.setLocale(new LocaleWrapper(ActiveLocale.getLocale()));
    }

    /**
     * Returns a client context which the client must supply as an
     * argument to every remote call made via proxies obtained from
     * the receiver.
     *
     * @return The context.
     */

    public StatelessClientContext getContext()
    {
        StatelessClientContext context=new StatelessClientContext();
        fillContext(context);
        return context;
    }

    /**
     * Retrieves the client-side proxy for the given service
     * interface.
     *
     * @param inInterface The interface class.
     */

    @SuppressWarnings("unchecked")
    public <T extends StatelessServiceBase> T getService(Class<T> inInterface)
    {
        System.setProperty("com.sun.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");
        System.setProperty("com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl.fastBoot", "true");
        JaxWsProxyFactoryBean f = new JaxWsProxyFactoryBean();
        f.setServiceClass(inInterface);
        f.setAddress(getConnectionUrl(inInterface));
        Map<String,Object> props = f.getProperties(); 
        if (props == null) {
            props = new HashMap<String,Object>();
        }
        if(contextClassProvider != null) {
            SLF4JLoggerProxy.debug(this,
                                   "Using additional context: {}", //$NON-NLS-1$
                                   contextClassProvider);
            props.put("jaxb.additionalContextClasses",  //$NON-NLS-1$
                      contextClassProvider.getContextClasses());
        }
        f.setProperties(props); 
        T service=(T)(f.create());
        HTTPConduit http=(HTTPConduit)ClientProxy.getClient(service).getConduit();
        HTTPClientPolicy httpClientPolicy=new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(0);
        httpClientPolicy.setReceiveTimeout(0);
        http.setClient(httpClientPolicy);
        return service;
    }
}
