package org.marketcetera.quickfix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import quickfix.LogUtil;
import quickfix.SessionID;
import quickfix.SystemTime;
import quickfix.field.converter.UtcTimestampConverter;

public class EventLog implements quickfix.Log {

    private static final byte[] TIME_STAMP_DELIMETER = ": ".getBytes();

    private SessionID sessionID;
    private String eventFileName;
    private boolean syncAfterWrite;

    private FileOutputStream events;
    
    private boolean includeMillis;
    private boolean includeTimestampForMessages;
    
    EventLog(String path, SessionID sessionID) throws FileNotFoundException {
        String sessionName = sessionID.getBeginString() + "-" + sessionID.getSenderCompID() + "-"
                + sessionID.getTargetCompID();
        this.sessionID = sessionID;
        
        if (sessionID.getSessionQualifier() != null && sessionID.getSessionQualifier().length() > 0) {
            sessionName += "-" + sessionID.getSessionQualifier();
        }

        String prefix = fileAppendPath(path, sessionName + ".");
        eventFileName = prefix + "event.log";

        File directory = new File(eventFileName).getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        openLogStreams(true);
    }

    private void openLogStreams(boolean append) throws FileNotFoundException {
        events = new FileOutputStream(eventFileName, append);
    }

    public void onEvent(String message) {
        try {
            writeTimeStamp(events);
            events.write(message.getBytes());
            events.write('\n');
            events.flush();
            if (syncAfterWrite) {
                events.getFD().sync();
            }
        } catch (IOException e) {
            LogUtil.logThrowable(sessionID, "error writing event to log", e);
        }
    }

    private void writeTimeStamp(OutputStream out) throws IOException {
        String formattedTime = UtcTimestampConverter.convert(SystemTime.getDate(), includeMillis);
        out.write(formattedTime.getBytes());
        out.write(TIME_STAMP_DELIMETER);
    }

    String getEventFileName() {
        return eventFileName;
    }

    public void setSyncAfterWrite(boolean syncAfterWrite) {
        this.syncAfterWrite = syncAfterWrite;
    }
    
    void close() throws IOException {
        events.close();
    }
    
    /**
     * Deletes the log files. Do not perform any log operations while performing
     * this operation.
     */
    public void clear() {
        try {
            close();
            openLogStreams(false);
        } catch (IOException e) {
            System.err.println("Could not clear log: "+getClass().getName());
        }
    }
    
	public void onIncoming(String arg0) {
		
	}

	public void onOutgoing(String arg0) {
		
	}

    public static String fileAppendPath(String pathPrefix, String pathSuffix) {
        return pathPrefix
                + (pathPrefix.endsWith(File.separator) ? "" : File.separator)
                + pathSuffix;
    }


}
