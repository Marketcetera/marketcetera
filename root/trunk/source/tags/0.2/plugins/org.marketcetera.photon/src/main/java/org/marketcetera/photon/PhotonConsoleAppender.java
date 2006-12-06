package org.marketcetera.photon;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.ui.MainConsole;


/**
 * THe PhotonConsoleAppender integrates the RCP's console facility
 * with the Marketcetera Core's Log4J-based logging by providing
 * an implementation for {@link AppenderSkeleton} that knows how
 * to write messages into the console using the appropriate
 * {@link MessageConsoleStream}.
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
class PhotonConsoleAppender extends AppenderSkeleton {
	private MainConsole console;
	private Display display;

	/**
	 * Create a new PhotonConsoleAppender 
	 * that will write messages to the specified {@link MainConsole}.
	 * 
	 * @param pConsole the console to which to write messages
	 */
	PhotonConsoleAppender(MainConsole pConsole) {
    	console = pConsole;
    	display = Display.getDefault();
    }

    /**
     * Writes the message out to the console using the correct 
     * {@link MessageConsoleStream}.  This method queries the incoming
     * {@link LoggingEvent} for its log-level and then
     * chooses the appropriate MessageConsoleStream to write to based
     * on the result.  The actual write is doen asynchronously on the
     * UI thread, and therefore may not be complete before this
     * method returns.
     * 
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    protected void append(final LoggingEvent loggingEvent) {
    	final MessageConsoleStream stream;
    	if (Level.ERROR.equals(loggingEvent.getLevel())){
    		stream = console.getErrorMessageStream();
    	} else if (Level.WARN.equals(loggingEvent.getLevel())){
    		stream = console.getWarnMessageStream();
    	} else if (Level.INFO.equals(loggingEvent.getLevel())){
    		stream = console.getInfoMessageStream();
    	} else {
    		stream = console.getDebugMessageStream();
    	}
        display.asyncExec(new Runnable() {
            public void run() {
                stream.println(loggingEvent.getRenderedMessage());
            }
        });
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
     */
    public boolean requiresLayout() {
        return false;
    }

    /**
     * Does nothing.
     * 
     * @see org.apache.log4j.AppenderSkeleton#close()
     */
    public void close() {
        // do nothing
    }

}