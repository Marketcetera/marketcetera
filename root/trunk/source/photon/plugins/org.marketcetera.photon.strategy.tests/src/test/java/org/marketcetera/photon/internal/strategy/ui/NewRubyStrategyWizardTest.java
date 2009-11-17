package org.marketcetera.photon.internal.strategy.ui;

import org.junit.runner.RunWith;
import org.marketcetera.photon.internal.strategy.ui.NewRubyStrategyWizard;
import org.marketcetera.photon.test.WorkbenchRunner;

/* $License$ */

/**
 * Tests {@link NewRubyStrategyWizard}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(WorkbenchRunner.class)
public class NewRubyStrategyWizardTest extends AbstractNewStrategyWizardTestBase<NewRubyStrategyWizard> {

    @Override
    protected NewRubyStrategyWizard createWizard() {
        return new NewRubyStrategyWizard();
    }
    
    @Override
    protected Fixture createFixture() {
        return new Fixture("New Ruby Strategy");
    }
    
    @Override
    protected String getFileNameForMyStrategy() {
        return "my_strategy.rb";
    }

    @Override
    protected String[] getInvalidClassNames() {
        return new String[] { "1234", "$$$$"};
    }
    
    @Override
    protected String getInvalidClassNameError() {
        return "The class name is invalid. It must begin with a letter or underscore, and contain only letters, digits, or underscores.";
    }
}
