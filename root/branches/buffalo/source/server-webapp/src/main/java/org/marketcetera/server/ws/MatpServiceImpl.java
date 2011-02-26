package org.marketcetera.server.ws;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.marketcetera.util.ws.stateless.StatelessServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
public class MatpServiceImpl
        implements MatpService, InitializingBean, DisposableBean
{
    /**
     * Create a new MatpServiceImpl instance.
     *
     * @param inHost
     * @param inPort
     */
    public MatpServiceImpl(String inHost,
                           int inPort)
    {
        host = StringUtils.trimToNull(inHost);
        Validate.notNull(host,
                         "MATP WebServices host property missing");
        port = inPort;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.ws.MatpServices#test(java.lang.String)
     */
    @Override
    public void test(String inValue)
    {
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        server = new StatelessServer(getHost(),
                                     getPort());
        SLF4JLoggerProxy.info(MatpServiceImpl.class,
                              "Starting MATP Service on {}:{}",
                              getHost(),
                              getPort());
        service = server.publish(this,
                                 MatpService.class);
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void destroy()
            throws Exception
    {
        SLF4JLoggerProxy.info(MatpServiceImpl.class,
                              "Stopping MATP Service");
        service.stop();
    }
    /**
     * Get the host value.
     *
     * @return a <code>String</code> value
     */
    public String getHost()
    {
        return host;
    }
    /**
     * Get the port value.
     *
     * @return a <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    private StatelessServer server;
    private ServiceInterface service;
    private final String host;
    private final int port;
}
