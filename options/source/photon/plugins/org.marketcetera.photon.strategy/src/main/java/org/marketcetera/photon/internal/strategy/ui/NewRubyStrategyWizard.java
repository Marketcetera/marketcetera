package org.marketcetera.photon.internal.strategy.ui;

import java.io.InputStream;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
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

    private static final IValidator sClassNameValidator = new IValidator() {
        /**
         * From <a href=
         * "http://web.njit.edu/all_topics/Prog_Lang_Docs/html/ruby/yacc.html"
         * >http
         * ://web.njit.edu/all_topics/Prog_Lang_Docs/html/ruby/yacc.html</a>.
         */
        private final Pattern CLASS_NAME_PATTERN = Pattern
                .compile("^[a-zA-Z_][a-zA-Z0-9_]*"); //$NON-NLS-1$

        @Override
        public IStatus validate(Object value) {
            final String string = (String) value;
            if (!CLASS_NAME_PATTERN.matcher(string).matches()) {
                return ValidationStatus
                        .error(Messages.NEW_RUBY_STRATEGY_WIZARD_INVALID_CLASS_NAME
                                .getText());
            }
            return ValidationStatus.ok();
        }
    };

    private static final String SCRIPT_EXTENSION = ".rb"; //$NON-NLS-1$
    private static final String RUBY_STRATEGY_TEMPLATE = "RubyStrategyTemplate.txt"; //$NON-NLS-1$

    @Override
    protected NewStrategyWizardPage createPage(ISelection selection) {
        return new NewStrategyWizardPage(selection,
                Messages.NEW_RUBY_STRATEGY_WIZARD__TITLE.getText(), sClassNameValidator);
    }

    @Override
    protected String getScriptName(String typeName) {
        return Util.camelCaseToUnderscores(typeName) + SCRIPT_EXTENSION;
    }

    @Override
    protected InputStream openContentStream(String className) {
        return StrategyTemplate.createNewScript(RUBY_STRATEGY_TEMPLATE,
                className);
    }
}