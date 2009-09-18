package org.marketcetera.photon.internal.strategy.ui;

import java.io.InputStream;

import org.eclipse.jface.viewers.ISelection;
import org.marketcetera.photon.internal.strategy.StrategyTemplate;
import org.marketcetera.util.misc.ClassVersion;
import org.rubypeople.rdt.core.util.Util;

/* $License$ */

/**
 * Wizard to create a new Ruby strategy script from a template.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class NewRubyStrategyWizard extends AbstractNewStrategyWizard {

	private static final String SCRIPT_EXTENSION = ".rb"; //$NON-NLS-1$
	private static final String RUBY_STRATEGY_TEMPLATE = "RubyStrategyTemplate.txt"; //$NON-NLS-1$

	@Override
    protected NewStrategyWizardPage createPage(ISelection selection) {
        return new NewStrategyWizardPage(selection, Messages.NEW_RUBY_STRATEGY_WIZARD__TITLE.getText());
    }

	@Override
	protected String getScriptName(String typeName) {
		return Util.camelCaseToUnderscores(typeName) + SCRIPT_EXTENSION;
	}

	@Override
    protected InputStream openContentStream(String className) {
		return StrategyTemplate.createNewScript(RUBY_STRATEGY_TEMPLATE, className);
	}
}