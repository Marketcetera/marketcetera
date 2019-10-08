package com.marketcetera.colin.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HasLogger is a feature interface that provides Logging capability for anyone
 * implementing it where logger needs to operate in serializable environment
 * without being static.
 */
public interface HasLogger {

	default Logger getLogger() {
		return LoggerFactory.getLogger(getClass());
	}
}
