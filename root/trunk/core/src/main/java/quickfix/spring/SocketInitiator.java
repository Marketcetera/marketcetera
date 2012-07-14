package quickfix.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.marketcetera.core.ClassVersion;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionFactory;
import quickfix.SessionSettings;

/**
 * Straight subclass of {@link quickfix.SocketInitiator} to be used from Spring config files
 * that adds the {@link InitializingBean} and {@link DisposableBean} behaviour.
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class SocketInitiator extends quickfix.SocketInitiator implements
		InitializingBean, DisposableBean {

    public SocketInitiator(Application application, MessageStoreFactory messageStoreFactory,
            SessionSettings settings, LogFactory logFactory, MessageFactory messageFactory)
            throws ConfigError {
		super(application, messageStoreFactory, settings, logFactory, messageFactory);
	}

    public SocketInitiator(Application application, MessageStoreFactory messageStoreFactory,
                           SessionSettings settings, MessageFactory messageFactory) throws ConfigError {
        super(application, messageStoreFactory, settings, messageFactory);
    }

    public SocketInitiator(SessionFactory sessionFactory, SessionSettings settings) throws ConfigError {
        super(sessionFactory, settings);
    }

    public void afterPropertiesSet() throws Exception {
		start();
	}

	public void destroy() throws Exception {
		stop(true);
	}
	
}
