package org.marketcetera.photon.internal.strategy.ruby;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.Wizard;
import org.marketcetera.photon.internal.strategy.StrategyManager;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Collects the necessary data and registers a given ruby script with the
 * {@link StrategyManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class RegisterRubyStrategyWizard extends Wizard {

	private RegisterRubyStrategyWizardPage mPage;

	private final IFile mFile;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            the Ruby script file
	 */
	public RegisterRubyStrategyWizard(IFile file) {
		mFile = file;
	}

	@Override
	public void addPages() {
		mPage = new RegisterRubyStrategyWizardPage(mFile.getFullPath().toString());
		addPage(mPage);
	}

	@Override
	public boolean performFinish() {
		StrategyManager manager = StrategyManager.getCurrent();
		manager.registerStrategy(mFile, mPage.getClassName().trim(), mPage.getDisplayName().trim(), mPage.getRouteToServer());
		return true;
	}

}
