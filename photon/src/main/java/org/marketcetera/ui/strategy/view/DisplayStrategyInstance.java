package org.marketcetera.ui.strategy.view;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.marketcetera.strategy.StrategyInstance;
import org.marketcetera.strategy.StrategyStatus;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
public class DisplayStrategyInstance
{
    /**
     * Create a new DisplayStrategy instance.
     */
    public DisplayStrategyInstance() {}
    /**
     * Create a new DisplayStrategy instance.
     *
     * @param inName a <code>String</code> value
     * @param inOwnerName a <code>String</code> value
     */
    public DisplayStrategyInstance(String inName,
                                   String inOwnerName)
    {
        strategyName.setValue(inName);
        owner.setValue(inOwnerName);
        strategyStatus.setValue(StrategyStatus.PREPARING);
        uploadProgress.setValue(0.0);
        started.setValue(new Period(0));
    }
    /**
     * Create a new DisplayStrategy instance.
     *
     * @param inStrategyInstance
     */
    public DisplayStrategyInstance(StrategyInstance inStrategyInstance)
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
                updateRunningProperty(inStrategyInstance.getStarted());
                break;
            default:
                break;
        }
    }
    /**
     * 
     *
     *
     * @param inRunningValue
     */
    public void updateRunningProperty(Date inRunningValue)
    {
        if(inRunningValue == null) {
            started.setValue(new Period(0));
        } else {
            started.setValue(new Period(DateTime.now().minus(inRunningValue.getTime()).getMillis()));
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
     * @return an <code>ObjectProperty&lt;Period&gt;</code> value
     */
    public ObjectProperty<Period> startedProperty()
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
     * @return an <code>ObjectProperty&lt;StrategyStatus&gt;</code> value
     */
    public ObjectProperty<StrategyStatus> strategyStatusProperty()
    {
        return strategyStatus;
    }
    public DoubleProperty uploadProgressProperty()
    {
        return uploadProgress;
    }
    private final StringProperty strategyName = new SimpleStringProperty();
    private final ObjectProperty<Period> started = new SimpleObjectProperty<>();
    private final StringProperty owner = new SimpleStringProperty();
    private final ObjectProperty<StrategyStatus> strategyStatus = new SimpleObjectProperty<>();
    private final DoubleProperty uploadProgress = new SimpleDoubleProperty();
}
