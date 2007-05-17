package org.marketcetera.photon.scripting;

import org.apache.bsf.BSFException;
import org.apache.log4j.Logger;
import org.jruby.RubyException;
import org.jruby.exceptions.RaiseException;

public class ScriptLoggingUtil {

	public static void error(Logger logger, BSFException e) {
		Throwable targetException = e.getTargetException();
		if (targetException instanceof RaiseException){
			RubyException rubyException = ((RaiseException)targetException).getException();
			if (rubyException != null){
				logger.error(""+rubyException);
			} else {
				logger.error(targetException.getMessage(), targetException);
			}
		} else {
			logger.error(e.getMessage(), e);
		}
	}

	public static void warn(Logger logger, BSFException e) {
		Throwable targetException = e.getTargetException();
		if (targetException instanceof RaiseException){
			RubyException rubyException = ((RaiseException)targetException).getException();
			if (rubyException != null){
				logger.warn(""+rubyException);
			} else {
				logger.warn(targetException.getMessage(), targetException);
			}
		} else {
			logger.warn(e.getMessage(), e);
		}
	}

	public static void info(Logger logger, BSFException e) {
		Throwable targetException = e.getTargetException();
		if (targetException instanceof RaiseException){
			RubyException rubyException = ((RaiseException)targetException).getException();
			if (rubyException != null){
				logger.info(""+rubyException);
			} else {
				logger.info(targetException.getMessage(), targetException);
			}
		} else {
			logger.info(e.getMessage(), e);
		}
	}
	
	public static void debug(Logger logger, BSFException e) {
		Throwable targetException = e.getTargetException();
		if (targetException instanceof RaiseException){
			RubyException rubyException = ((RaiseException)targetException).getException();
			if (rubyException != null){
				logger.debug(""+rubyException);
			} else {
				logger.debug(targetException.getMessage(), targetException);
			}
		} else {
			logger.debug(e.getMessage(), e);
		}
	}

}
