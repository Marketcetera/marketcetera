package org.marketcetera.photon.scripting;

import org.apache.bsf.BSFException;
import org.apache.log4j.Logger;
import org.jruby.RubyException;
import org.jruby.exceptions.RaiseException;

public class ScriptLoggingUtil {

	public static void error(Logger logger, BSFException e) {
		Throwable targetException = e.getTargetException();
		if (targetException instanceof RaiseException){
			RaiseException targetRaiseException = ((RaiseException)targetException);
			error(logger, targetRaiseException);
		} else {
			logger.error(e.getMessage(), e);
		}
	}

	public static void error(Logger logger, RaiseException targetRaiseException) {
		RubyException rubyException = targetRaiseException.getException();
		if (rubyException != null){
			logger.error(""+rubyException);
		} else {
			logger.error(targetRaiseException.getMessage(), targetRaiseException);
		}
	}

	public static void warn(Logger logger, BSFException e) {
		Throwable targetException = e.getTargetException();
		if (targetException instanceof RaiseException){
			RaiseException targetRaiseException = ((RaiseException)targetException);
			warn(logger, targetRaiseException);
		} else {
			logger.warn(e.getMessage(), e);
		}
	}

	public static void warn(Logger logger, RaiseException targetRaiseException) {
		RubyException rubyException = targetRaiseException.getException();
		if (rubyException != null){
			logger.warn(""+rubyException);
		} else {
			logger.warn(targetRaiseException.getMessage(), targetRaiseException);
		}
	}

	public static void info(Logger logger, BSFException e) {
		Throwable targetException = e.getTargetException();
		if (targetException instanceof RaiseException){
			RaiseException targetRaiseException = ((RaiseException)targetException);
			info(logger, targetRaiseException);
		} else {
			logger.info(e.getMessage(), e);
		}
	}

	public static void info(Logger logger, RaiseException targetRaiseException) {
		RubyException rubyException = targetRaiseException.getException();
		if (rubyException != null){
			logger.info(""+rubyException);
		} else {
			logger.info(targetRaiseException.getMessage(), targetRaiseException);
		}
	}
	
	public static void debug(Logger logger, BSFException e) {
		Throwable targetException = e.getTargetException();
		if (targetException instanceof RaiseException){
			RaiseException targetRaiseException = ((RaiseException)targetException);
			debug(logger, targetRaiseException);
		} else {
			logger.debug(e.getMessage(), e);
		}
	}

	public static void debug(Logger logger, RaiseException targetRaiseException) {
		RubyException rubyException = targetRaiseException.getException();
		if (rubyException != null){
			logger.debug(""+rubyException);
		} else {
			logger.debug(targetRaiseException.getMessage(), targetRaiseException);
		}
	}

}
