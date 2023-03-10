package org.marketcetera.ui.strategy.view;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.marketcetera.strategy.StrategyInstance;
import org.marketcetera.strategy.StrategyStatus;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* $License$ */

/**
 * Provides a UI-oriented implementation of {@link StrategyInstance}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayStrategy
{
    /**
     * Create a new DisplayStrategy instance.
     */
    public DisplayStrategy() {}
    
    /**
     * Create a new DisplayStrategy instance.
     *
     * @param inStrategyInstance
     */
    public DisplayStrategy(StrategyInstance inStrategyInstance)
    {
        strategyName.setValue(inStrategyInstance.getName());
        owner.setValue(inStrategyInstance.getUser().getName());
        strategyStatus.setValue(inStrategyInstance.getStatus());
        switch(inStrategyInstance.getStatus()) {
            case ERROR:
            case LOADING:
            case STOPPED:
                started.setValue(new Period(0));
                break;
            case RUNNING:
                started.setValue(new Period(DateTime.now().minus(inStrategyInstance.getStarted().getTime())));
                break;
            default:
                break;
        }
    }
    /**
     * Get the strategyName value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty strategyNameProperty()
    {
        return strategyName;
    }
    /**
     * Get the started value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;Period&gt;</code> value
     */
    public ReadOnlyObjectProperty<Period> startedProperty()
    {
        return started;
    }
    /**
     * Get the owner value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty ownerProperty()
    {
        return owner;
    }
    /**
     * Get the strategyStatus value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;StrategyStatus&gt;</code> value
     */
    public ReadOnlyObjectProperty<StrategyStatus> strategyStatusProperty()
    {
        return strategyStatus;
    }
    private final StringProperty strategyName = new SimpleStringProperty();
    private final ObjectProperty<Period> started = new SimpleObjectProperty<>();
    private final StringProperty owner = new SimpleStringProperty();
    private final ObjectProperty<StrategyStatus> strategyStatus = new SimpleObjectProperty<>();
}
