package org.marketcetera.core;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

public class NoOpLogger extends Logger {

	private Vector zeroElementVector;

	public NoOpLogger(String name) {
		super(name);
		zeroElementVector = new Vector();
	}

	@Override
	public synchronized void addAppender(Appender arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void assertLog(boolean arg0, String arg1) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void callAppenders(LoggingEvent arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void debug(Object arg0, Throwable arg1) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void debug(Object arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void error(Object arg0, Throwable arg1) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void error(Object arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void fatal(Object arg0, Throwable arg1) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void fatal(Object arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public boolean getAdditivity() {
		return false;
	}

	@Override
	public synchronized Enumeration getAllAppenders() {
		return zeroElementVector.elements();
	}

	@Override
	public synchronized Appender getAppender(String name) {
		return super.getAppender(name);
	}

	@Override
	public Priority getChainedPriority() {
		return super.getChainedPriority();
	}

	@Override
	public Level getEffectiveLevel() {
		return super.getEffectiveLevel();
	}

	@Override
	public LoggerRepository getHierarchy() {
		return super.getHierarchy();
	}

	@Override
	public LoggerRepository getLoggerRepository() {
		return super.getLoggerRepository();
	}

	@Override
	public ResourceBundle getResourceBundle() {
		return super.getResourceBundle();
	}

	@Override
	public void info(Object arg0, Throwable arg1) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void info(Object arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public boolean isAttached(Appender arg0) {
		return false;
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public boolean isEnabledFor(Priority arg0) {
		return false;
	}

	@Override
	public boolean isInfoEnabled() {
		return false;
	}

	@Override
	public void l7dlog(Priority arg0, String arg1, Object[] arg2, Throwable arg3) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void l7dlog(Priority arg0, String arg1, Throwable arg2) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void log(Priority arg0, Object arg1, Throwable arg2) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void log(Priority arg0, Object arg1) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void log(String arg0, Priority arg1, Object arg2, Throwable arg3) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public synchronized void removeAllAppenders() {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public synchronized void removeAppender(Appender arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public synchronized void removeAppender(String arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void setAdditivity(boolean arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void setLevel(Level arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void setPriority(Priority arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void setResourceBundle(ResourceBundle arg0) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void warn(Object arg0, Throwable arg1) {
		// do nothing, I'm the NoOpLogger
	}

	@Override
	public void warn(Object arg0) {
		// do nothing, I'm the NoOpLogger
	}
	
	

}
