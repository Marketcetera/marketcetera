package org.marketcetera.photon.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.AbstractWorkbenchTrimWidget;
import org.marketcetera.core.ClassVersion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Places the clock widget in the bottom status bar of the Photon client.
 * Sets up the clock to be updated every second.
 * 
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class ClockTrimWidget extends AbstractWorkbenchTrimWidget {
    private Label clockValue;
    private Timer timer;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d HH:mm:ss z");

    public ClockTrimWidget() {
    }

    public void fill(Composite parent, int oldSide, int newSide) {
        clockValue = new Label(parent, SWT.NONE);
        clockValue.setText(DATE_FORMAT.format(new Date()));

        TimerTask task = new TimerTask() {
            public void run() {
                if (!clockValue.isDisposed()) {
                    clockValue.getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            clockValue.setText(DATE_FORMAT.format(new Date()));
                        }
                    });
                }
            }
        };
        timer = new Timer("ClockUpdateTimer");
        timer.schedule(task, 0, 1000);
    }

    /** Need to dispoose of the clockValue widget b/c the fill() method actually gets called
     * twice so we need to  dispose of the first one created.
     */
    public void dispose() {
        if (clockValue != null) {
            clockValue.dispose();
            clockValue = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
