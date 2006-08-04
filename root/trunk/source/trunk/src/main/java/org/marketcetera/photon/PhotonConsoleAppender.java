package org.marketcetera.photon;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;
import org.marketcetera.core.ClassVersion;


@ClassVersion("$Id$")
class PhotonConsoleAppender extends AppenderSkeleton {
	private MainConsole console;
	private Display display;

	PhotonConsoleAppender(Display pDisplay, MainConsole pConsole) {
    	console = pConsole;
    	display = pDisplay;
    }

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

    public boolean requiresLayout() {
        return false;
    }

    public void close() {
        // do nothing
    }

}