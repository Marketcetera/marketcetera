package org.marketcetera.ui.strategy.view;

import org.joda.time.DateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayStrategy
{
    public DisplayStrategy() {}
    
    /**
     * Get the strategyName value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty strategyNameProperty()
    {
        return strategyName;
    }
    /**
     * Get the strategyId value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty strategyIdProperty()
    {
        return strategyId;
    }
    /**
     * Get the started value.
     *
     * @return a <code>ObjectProperty<DateTime></code> value
     */
    public ReadOnlyObjectProperty<DateTime> startedProperty()
    {
        return started;
    }
    /**
     * Get the owner value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty ownerProperty()
    {
        return owner;
    }
    /**
     * Get the strategyStatus value.
     *
     * @return a <code>ObjectProperty<StrategyStatus></code> value
     */
    public ReadOnlyObjectProperty<StrategyStatus> strategyStatusProperty()
    {
        return strategyStatus;
    }
    private final StringProperty strategyName = new SimpleStringProperty();
    private final StringProperty strategyId = new SimpleStringProperty();
    private final ObjectProperty<DateTime> started = new SimpleObjectProperty<>();
    private final StringProperty owner = new SimpleStringProperty();
    private final ObjectProperty<StrategyStatus> strategyStatus = new SimpleObjectProperty<>();
}
