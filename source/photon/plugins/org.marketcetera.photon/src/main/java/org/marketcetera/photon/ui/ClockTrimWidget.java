package org.marketcetera.photon.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.AbstractWorkbenchTrimWidget;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ThreadLocalSimpleDateFormat;

/**
 * Places the clock widget in the bottom status bar of the Photon client.
 * Sets up the clock to be updated every second.
 * 
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class ClockTrimWidget extends AbstractWorkbenchTrimWidget {
    private Label clockValue;
    private static final ThreadLocalSimpleDateFormat DATE_FORMAT_LOCAL = new ThreadLocalSimpleDateFormat("MMM d HH:mm:ss z"); //$NON-NLS-1$
    private static final Timer timer = new Timer("ClockUpdateTimer"); //$NON-NLS-1$
	private TimerTask task;
    
    public ClockTrimWidget() {
    }

    public void fill(Composite parent, int oldSide, int newSide) {
        clockValue = new Label(parent, SWT.NONE);
        clockValue.setText(DATE_FORMAT_LOCAL.get().format(new Date()));

        task = new TimerTask() {
            public void run() {
                if (!clockValue.isDisposed()) {
                    clockValue.getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            if (!clockValue.isDisposed()) {
                            	clockValue.setText(DATE_FORMAT_LOCAL.get().format(new Date()));
                            }
                        }
                    });
                }
            }
        };
		timer.schedule(task, 0, 1000);
    }

    /** Need to dispose of the clockValue widget b/c the fill() method actually gets called
     * twice so we need to  dispose of the first one created.
     */
    public void dispose() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (clockValue != null) {
            clockValue.dispose();
            clockValue = null;
        }
    }
}
