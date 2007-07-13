package quickfix.spring;

import org.marketcetera.core.ClassVersion;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import quickfix.*;
import quickfix.mina.acceptor.AcceptorSessionProvider;

import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * Wrapper around the @{link quickfix.SocketAcceptor} to be used for creation via Spring
 * that adds the @{link InitializingBean} and @{link DisposableBean} behaviour. 
 * @author toli
 * @version $Id$
 */
@ClassVersion("$Id$")
public class SocketAcceptor extends quickfix.SocketAcceptor implements InitializingBean, DisposableBean {

    private HashMap<String, AcceptorSessionProvider> sessionProviderList = new HashMap<String, AcceptorSessionProvider>();

    public SocketAcceptor(Application application, MessageStoreFactory messageStoreFactory,
                          SessionSettings settings, LogFactory logFactory, MessageFactory messageFactory) throws ConfigError {
        super(application, messageStoreFactory, settings, logFactory, messageFactory);
    }

    public SocketAcceptor(SessionFactory sessionFactory, SessionSettings settings) throws ConfigError
    {
	super(sessionFactory, settings);
    }

    /** Add all the session providers registered for each port before starting the acceptor */
    public void afterPropertiesSet() throws Exception {
        for (String port : sessionProviderList.keySet()) {
            setSessionProvider(new InetSocketAddress(new Integer(port)), sessionProviderList.get(port));
        }
        start();
    }

    public void destroy() throws Exception {
        stop(true);
    }

    public void setSessionProviderList(HashMap<String, AcceptorSessionProvider> sessionProviderList) {
        this.sessionProviderList = sessionProviderList;
    }
}
