package org.marketcetera.ui.strategy.view;

import org.joda.time.DateTime;
import org.marketcetera.core.notifications.INotification.Severity;

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
public class DisplayStrategyEvent
{
    /**
     *  the strategyIdProperty value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty strategyIdProperty()
    {
        return strategyIdProperty;
    }
    /**
     *  the timestampProperty value.
     *
     * @return a <code>ObjectProperty<DateTime></code> value
     */
    public ReadOnlyObjectProperty<DateTime> timestampProperty()
    {
        return timestampProperty;
    }
    /**
     *  the severityProperty value.
     *
     * @return a <code>ObjectProperty<Severity></code> value
     */
    public ReadOnlyObjectProperty<Severity> severityProperty()
    {
        return severityProperty;
    }
    /**
     *  the eventTypeProperty value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty eventTypeProperty()
    {
        return eventTypeProperty;
    }
    /**
     *  the messageProperty value.
     *
     * @return a <code>StringProperty</code> value
     */
    public ReadOnlyStringProperty messageProperty()
    {
        return messageProperty;
    }
    private final StringProperty strategyIdProperty = new SimpleStringProperty();
    private final ObjectProperty<DateTime> timestampProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Severity> severityProperty = new SimpleObjectProperty<>();
    private final StringProperty eventTypeProperty = new SimpleStringProperty();
    private final StringProperty messageProperty = new SimpleStringProperty();
}
