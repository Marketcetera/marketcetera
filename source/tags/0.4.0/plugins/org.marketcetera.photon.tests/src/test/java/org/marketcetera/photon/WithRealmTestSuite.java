package org.marketcetera.photon;

import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.widgets.Display;

public class WithRealmTestSuite extends TestSuite {

	@Override
	public void run(final TestResult result) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				WithRealmTestSuite.super.run(result);
			}
		});

	}

}
