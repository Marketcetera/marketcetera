package org.marketcetera.photon.internal.strategy.ui;

import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INewWizard;
import org.marketcetera.photon.internal.strategy.StrategyTemplate;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Wizard to create a new Java strategy script from a template.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class NewJavaStrategyWizard extends AbstractNewStrategyWizard
        implements INewWizard {

    private static final IValidator sClassNameValidator = new IValidator() {
        @Override
        public IStatus validate(Object value) {
            final String string = (String) value;
            if (StringUtils.isEmpty(string)
                    || !validIdentifier(string.toCharArray())) {
                return ValidationStatus
                        .error(Messages.NEW_JAVA_STRATEGY_WIZARD_INVALID_CLASS_NAME
                                .getText());
            }
            return ValidationStatus.ok();
        }

        private boolean validIdentifier(char[] chars) {
            if (!Character.isJavaIdentifierStart(chars[0])) {
                return false;
            }
            for (int i = 1; i < chars.length; i++) {
                if (!Character.isJavaIdentifierPart(chars[i])) {
                    return false;
                }
            }
            return true;
        }
    };

    private static final String SCRIPT_EXTENSION = ".java"; //$NON-NLS-1$
    private static final String JAVA_STRATEGY_TEMPLATE = "JavaStrategyTemplate.txt"; //$NON-NLS-1$

    @Override
    protected NewStrategyWizardPage createPage(ISelection selection) {
        return new NewStrategyWizardPage(selection,
                Messages.NEW_JAVA_STRATEGY_WIZARD__TITLE.getText(),
                sClassNameValidator);
    }

    @Override
    protected String getScriptName(String typeName) {
        return typeName + SCRIPT_EXTENSION;
    }

    @Override
    protected InputStream openContentStream(String className) {
        return StrategyTemplate.createNewScript(JAVA_STRATEGY_TEMPLATE,
                className);
    }
}
