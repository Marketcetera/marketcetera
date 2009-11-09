package org.marketcetera.photon.internal.strategy.engine.sa.ui.workbench;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.marketcetera.photon.commons.ui.workbench.DataBindingPropertyPage;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.sa.ui.StrategyAgentEngineUI;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineImage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Property page for {@link StrategyAgentEngine} connection properties. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentConnectionPropertyPage extends DataBindingPropertyPage {

    private StrategyAgentEngine mOriginalEngine;
    private StrategyAgentEngine mNewEngine;

    /**
     * Constructor.
     */
    public StrategyAgentConnectionPropertyPage() {
        setImageDescriptor(StrategyEngineImage.ENGINE_OBJ.getImageDescriptor());
        noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent) {
        mOriginalEngine = (StrategyAgentEngine) getElement().getAdapter(
                StrategyAgentEngine.class);
        // make a copy so cancel works as expected
        mNewEngine = (StrategyAgentEngine) EcoreUtil.copy(mOriginalEngine);
        Composite composite = StrategyAgentEngineUI
                .createStrategyAgentConnectionComposite(parent,
                        getDataBindingContext(), mNewEngine);
        return composite;
    }

    @Override
    public boolean performOk() {
        mOriginalEngine.setJmsUrl(mNewEngine.getJmsUrl());
        mOriginalEngine.setWebServiceHostname(mNewEngine.getWebServiceHostname());
        mOriginalEngine.setWebServicePort(mNewEngine.getWebServicePort());
        return true;
    }

}
