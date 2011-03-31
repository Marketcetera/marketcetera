package org.marketcetera.publication.protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.GuardedBy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.server.service.DataPublicationManager;
import org.marketcetera.server.service.DataSubscriber;
import org.marketcetera.server.ws.impl.ClientSession;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class ProtocolBufferPublicationManager
        implements DataPublicationManager, Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        if(running.get()) {
            return;
        }
        InetSocketAddress address = new InetSocketAddress(hostname,
                                                          port);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(address);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        if(!running.get()) {
            return;
        }
        for(Socket socket : sockets) {
            try {
                // TODO send shutdown message
                socket.close();
            } catch (IOException e) {
                // TODO skip exception and continue
            }
        }
        try {
            if(serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ignored) {}

                serverSocket = null;
            }
        } finally {
            running.set(false);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.SessionListener#sessionInvalidated(java.lang.Object)
     */
    @Override
    public void sessionInvalidated(ClientSession inSession)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.DataPublicationManager#publish(java.lang.Object)
     */
    @Override
    public void publish(Object inObject)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.DataPublicationManager#subscribe(org.marketcetera.server.service.DataSubscriber)
     */
    @Override
    public void subscribe(DataSubscriber inDataSubscriber)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.DataPublicationManager#unsubscribe(org.marketcetera.server.service.DataSubscriber)
     */
    @Override
    public void unsubscribe(DataSubscriber inDataSubscriber)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * 
     *
     *
     * @throws Exception
     */
    @PostConstruct
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notNull(hostname,
                         "Protocol buffer publication manager requires a hostname");
    }
    /**
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    public String getHostname()
    {
        return hostname;
    }
    /**
     * Sets the hostname value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = StringUtils.trimToNull(inHostname);
    }
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * 
     */
    private volatile String hostname;
    /**
     * 
     */
    private volatile int port;
    /**
     * 
     */
    @GuardedBy("socketLock")
    private ServerSocket serverSocket;
    /**
     * 
     */
    private final ReentrantReadWriteLock socketLock = new ReentrantReadWriteLock();
    /**
     * 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * 
     */
    private final List<Socket> sockets = new ArrayList<Socket>();
}
