package org.marketcetera.photon.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
public class ClockTrimWidget extends Workaround253082ContributionItem {
    private Label clockValue;
    private static final ThreadLocalSimpleDateFormat DATE_FORMAT_LOCAL = new ThreadLocalSimpleDateFormat("MMM d HH:mm:ss z"); //$NON-NLS-1$
    private static final Timer timer = new Timer("ClockUpdateTimer"); //$NON-NLS-1$
	private TimerTask task;
    
	@Override
    public void doDispose() {
        if (task != null) {
            task.cancel();
            task = null;
        }
		super.doDispose();
    }

	@Override
	protected Control createControl(Composite parent) {
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
		return clockValue;
	}
}
