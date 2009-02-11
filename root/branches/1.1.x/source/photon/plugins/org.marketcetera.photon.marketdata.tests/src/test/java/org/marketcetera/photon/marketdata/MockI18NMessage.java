package org.marketcetera.photon.marketdata;

import java.io.Serializable;
import java.util.Locale;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Wraps a string for testing.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public class MockI18NMessage implements I18NBoundMessage {
	
	private String mMessage;

	public MockI18NMessage(String message) {
		mMessage = message;
	}

	@Override
	public String getText(Locale locale) {
		return getText();
	}

	@Override
	public String getText() {
		return mMessage;
	}
	
	@Override
	public void debug(Object category, Throwable throwable) {
	}

	@Override
	public void debug(Object category) {
	}

	@Override
	public void error(Object category, Throwable throwable) {
	}

	@Override
	public void error(Object category) {
	}

	@Override
	public I18NLoggerProxy getLoggerProxy() {
		return null;
	}

	@Override
	public I18NMessage getMessage() {
		return null;
	}

	@Override
	public I18NMessageProvider getMessageProvider() {
		return null;
	}

	@Override
	public void info(Object category, Throwable throwable) {
	}

	@Override
	public void info(Object category) {
	}

	@Override
	public void trace(Object category, Throwable throwable) {
	}

	@Override
	public void trace(Object category) {
	}

	@Override
	public void warn(Object category, Throwable throwable) {
	}

	@Override
	public void warn(Object category) {
	}

	@Override
	public Object[] getParamsAsObjects() {
		return null;
	}

	@Override
	public Serializable[] getParams() {
		return null;
	}

}
