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
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class SocketInitiator extends quickfix.SocketInitiator implements
		InitializingBean, DisposableBean {

	public SocketInitiator(Application arg0, MessageStoreFactory arg1, SessionSettings arg2, LogFactory arg3, MessageFactory arg4) throws ConfigError {
		super(arg0, arg1, arg2, arg3, arg4);
	}

	public SocketInitiator(Application arg0, MessageStoreFactory arg1, SessionSettings arg2, MessageFactory arg3) throws ConfigError {
		super(arg0, arg1, arg2, arg3);
	}

	public SocketInitiator(SessionFactory arg0, SessionSettings arg1) throws ConfigError {
		super(arg0, arg1);
	}

	public void afterPropertiesSet() throws Exception {
		start();
	}

	public void destroy() throws Exception {
		stop(true);
	}
	
}
