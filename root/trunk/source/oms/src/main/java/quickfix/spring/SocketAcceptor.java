package quickfix.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionFactory;
import quickfix.SessionSettings;

public class SocketAcceptor extends quickfix.SocketAcceptor implements
		InitializingBean, DisposableBean {

	public SocketAcceptor(Application arg0, MessageStoreFactory arg1, SessionSettings arg2, LogFactory arg3, MessageFactory arg4) throws ConfigError {
		super(arg0, arg1, arg2, arg3, arg4);
	}

	public SocketAcceptor(Application arg0, MessageStoreFactory arg1, SessionSettings arg2, MessageFactory arg3) throws ConfigError {
		super(arg0, arg1, arg2, arg3);
	}

	public SocketAcceptor(SessionFactory arg0, SessionSettings arg1) throws ConfigError {
		super(arg0, arg1);
	}

	public void afterPropertiesSet() throws Exception {
		super.start();
	}

	public void destroy() throws Exception {
		super.stop();
	}
}
