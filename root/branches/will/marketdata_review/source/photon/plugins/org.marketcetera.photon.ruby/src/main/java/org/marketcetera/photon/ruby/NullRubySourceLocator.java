package org.marketcetera.photon.ruby;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * This class is a hack to avoid adding the
 * <code>org.rubypeople.rdt.debug.ui</code> plug-in to Photon.
 * <p>
 * In version 1.0.3 of RDT, <code>org.rubypeople.rdt.debug.ui</code> does not
 * run properly on Eclipse 3.4.1
 * (org.rubypeople.rdt.internal.debug.ui.EvaluationContextManager references
 * non-existent
 * org.eclipse.debug.internal.ui.contexts.provisional.IDebugContextListener
 * which throws {@link ClassNotFoundException}). Furthermore,
 * <code>org.rubypeople.rdt.debug.ui</code> includes lots of UI that is not
 * desirable for Photon (since we do not support debugging Ruby scripts).
 * <p>
 * Unfortunately, the required <code>org.rubypeople.rdt.launching</code> plug-in
 * references a source locator defined in
 * <code>org.rubypeople.rdt.debug.ui</code>. This class is registered with the
 * same id (<code>org.rubypeople.rdt.debug.ui.rubySourceLocator</code>) and
 * prevents exceptions from being thrown and cluttering the log files.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.9.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class NullRubySourceLocator implements IPersistableSourceLocator {

	@Override
	public String getMemento() throws CoreException {
		return null;
	}

	@Override
	public void initializeDefaults(ILaunchConfiguration configuration)
			throws CoreException {
	}

	@Override
	public void initializeFromMemento(String memento) throws CoreException {
	}

	@Override
	public Object getSourceElement(IStackFrame stackFrame) {
		return null;
	}
}
