package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.marketcetera.photon.commons.ui.workbench.DataBindingPropertyPage;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineImage;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineUI;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Property page for configuring a {@link StrategyEngine}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyEngineIdentificationPropertyPage extends
        DataBindingPropertyPage {

    private StrategyEngine mOriginalEngine;
    private StrategyEngine mNewEngine;

    /**
     * Constructor.
     */
    public StrategyEngineIdentificationPropertyPage() {
        setImageDescriptor(StrategyEngineImage.ENGINE_OBJ.getImageDescriptor());
        noDefaultAndApplyButton();
    }

    @Override
    protected Control createContents(Composite parent) {
        mOriginalEngine = (StrategyEngine) getElement().getAdapter(
                StrategyEngine.class);
        // make a copy so cancel works as expected
        mNewEngine = (StrategyEngine) EcoreUtil.copy(mOriginalEngine);
        Composite composite = StrategyEngineUI
                .createStrategyEngineIdentificationComposite(parent,
                        getDataBindingContext(), mNewEngine);
        return composite;
    }

    @Override
    public boolean performOk() {
        if (!mOriginalEngine.isReadOnly()) {
            mOriginalEngine.setName(mNewEngine.getName());
            mOriginalEngine.setDescription(mNewEngine.getDescription());
        }
        return true;
    }
}
