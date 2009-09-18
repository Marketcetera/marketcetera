package org.marketcetera.photon.internal.strategy.ui;

import org.junit.runner.RunWith;
import org.marketcetera.photon.internal.strategy.ui.NewJavaStrategyWizard;
import org.marketcetera.photon.test.WorkbenchRunner;

/* $License$ */

/**
 * Tests {@link NewJavaStrategyWizard}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class NewJavaStrategyWizardTest extends AbstractNewStrategyWizardTestBase<NewJavaStrategyWizard> {

    @Override
    protected NewJavaStrategyWizard createWizard() {
        return new NewJavaStrategyWizard();
    }
    
    @Override
    protected Fixture createFixture() {
        return new Fixture("New Java Strategy");
    }
    
    @Override
    protected String getFileNameForMyStrategy() {
        return "MyStrategy.java";
    }    
}
