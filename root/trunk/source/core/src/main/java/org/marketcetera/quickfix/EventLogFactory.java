package org.marketcetera.quickfix;

import quickfix.FileLogFactory;
import quickfix.Log;
import quickfix.LogFactory;
import quickfix.RuntimeError;
import quickfix.SessionID;
import quickfix.SessionSettings;

public class EventLogFactory implements LogFactory {

	private final SessionSettings settings;

	public EventLogFactory(SessionSettings settings) {
		this.settings = settings;
	}

	public Log create() {
		throw new UnsupportedOperationException();
	}

	public Log create(SessionID sessionID) {
		try {
		return new EventLog(settings.getString(sessionID, FileLogFactory.SETTING_FILE_LOG_PATH), sessionID);
        } catch (Exception e) {
            throw new RuntimeError(e);
        }
	}

}
