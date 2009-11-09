package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcher;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcher.IPropertiesChangedListener;
import org.marketcetera.photon.strategy.engine.ui.StrategyEngineStatusDecorator;
import org.marketcetera.photon.strategy.engine.ui.StrategyEnginesContentProvider;
import org.marketcetera.photon.strategy.engine.ui.StrategyEnginesLabelProvider;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableSet;

/* $License$ */

/**
 * This label provider enhances {@link StrategyEnginesLabelProvider} to support
 * {@link ICommonLabelProvider}. It also hooks up label redecoration to the
 * content provider's elements.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class StrategyEnginesCommonLabelProvider extends StrategyEnginesLabelProvider implements
        ICommonLabelProvider {

    private final PropertyWatcher mDecorationPropertyWatcher = new PropertyWatcher(
            StrategyEngineStatusDecorator.PROPERTIES,
            new IPropertiesChangedListener() {
                @Override
                public void propertiesChanged(ImmutableSet<?> affectedElements) {
                    PlatformUI
                            .getWorkbench()
                            .getDecoratorManager()
                            .update(
                                    StrategyEngineWorkbenchUI.STRATEGY_ENGINES_STATUS_DECORATOR_ID);
                }
            });

    @Override
    public void init(ICommonContentExtensionSite aConfig) {
        final IObservableSet elements = ((StrategyEnginesContentProvider) aConfig
                .getExtension().getContentProvider()).getKnownElements();
        track(elements);
        mDecorationPropertyWatcher.watch(elements);
    }

    @Override
    public void restoreState(IMemento aMemento) {
    }

    @Override
    public void saveState(IMemento aMemento) {
    }

    @Override
    public String getDescription(Object anElement) {
        return getText(anElement);
    }

    @Override
    public void dispose() {
        mDecorationPropertyWatcher.dispose();
        super.dispose();
    }
}