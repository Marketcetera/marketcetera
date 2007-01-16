package org.marketcetera.photon;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
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
	private static final String LAYOUT_PATTERN = "%d{ABSOLUTE} %5p - %m";
	private static final String DEBUG_LAYOUT_PATTERN = "%d{ABSOLUTE} %5p %c{2}:%L - %m";
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
    	PatternLayout patternLayout = new PatternLayout(LAYOUT_PATTERN);
    	setLayout(patternLayout);
    	
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
            	String loggableMessage = "";
            	Layout theLayout = getLayout();
				if (theLayout != null){
            		loggableMessage = theLayout.format(loggingEvent);
            	} else {
            		loggableMessage = loggingEvent.getRenderedMessage();
            	}
                stream.println(loggableMessage);
                ThrowableInformation throwableInformation = loggingEvent.getThrowableInformation();
                if (throwableInformation != null){
					stream.println(throwableInformation.getThrowable().getMessage());
                }
            }
        });
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
     */
    public boolean requiresLayout() {
        return true;
    }

    /**
     * Does nothing.
     * 
     * @see org.apache.log4j.AppenderSkeleton#close()
     */
    public void close() {
        // do nothing
    }

	@Override
	public void setLayout(Layout arg0) {
		// TODO Auto-generated method stub
		super.setLayout(arg0);
	}
    
    

}