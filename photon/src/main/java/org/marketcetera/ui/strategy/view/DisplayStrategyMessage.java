package org.marketcetera.ui.strategy.view;

import org.joda.time.DateTime;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.strategy.StrategyMessage;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* $License$ */

/**
 * Provides a UI implementation of a {@link StrategyMessage}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayStrategyMessage
{
    /**
     * Create a new DisplayStrategyEvent instance.
     *
     * @param inStrategyMessage a <code>StrategyMessage</code> value
     */
    public DisplayStrategyMessage(StrategyMessage inStrategyMessage)
    {
        messageProperty.set(inStrategyMessage.getMessage());
        strategyIdProperty.set(inStrategyMessage.getStrategyMessageId());
        severityProperty.set(inStrategyMessage.getSeverity());
        strategyNameProperty.set(inStrategyMessage.getStrategyInstance().getName());
        timestampProperty.set(new DateTime(inStrategyMessage.getMessageTimestamp()));
    }
    /**
     *  the strategyNameProperty value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty strategyNameProperty()
    {
        return strategyNameProperty;
    }
    /**
     *  the timestampProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<DateTime></code> value
     */
    public ReadOnlyObjectProperty<DateTime> timestampProperty()
    {
        return timestampProperty;
    }
    /**
     *  the severityProperty value.
     *
     * @return a <code>ReadOnlyObjectProperty<Severity></code> value
     */
    public ReadOnlyObjectProperty<Severity> severityProperty()
    {
        return severityProperty;
    }
    /**
     *  the messageProperty value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty messageProperty()
    {
        return messageProperty;
    }
    /**
     * Get the strategyIdProperty value.
     *
     * @return a <code>ReadOnlyLongProperty</code> value
     */
    public ReadOnlyLongProperty strategyIdProperty()
    {
        return strategyIdProperty;
    }
    /**
     * holds the strategy message id property
     */
    private final LongProperty strategyIdProperty = new SimpleLongProperty();
    /**
     * holds the strategy instance name property
     */
    private final StringProperty strategyNameProperty = new SimpleStringProperty();
    /**
     * holds the strategy message timestamp property
     */
    private final ObjectProperty<DateTime> timestampProperty = new SimpleObjectProperty<>();
    /**
     * holds the strategy message severity property
     */
    private final ObjectProperty<Severity> severityProperty = new SimpleObjectProperty<>();
    /**
     * holds the strategy message message property
     */
    private final StringProperty messageProperty = new SimpleStringProperty();
}
