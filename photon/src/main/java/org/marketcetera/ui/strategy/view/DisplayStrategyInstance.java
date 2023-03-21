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
        started.setValue(null);
        uptime.setValue(new Period(0));
        setupRuntimeListener();
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
        started.setValue(inStrategyInstance.getStarted());
        setupRuntimeListener();
        updateRunningProperty();
    }
    /**
     * 
     *
     *
     * @param inRunningValue
     */
    public void updateRunningProperty()
    {
        if(started.get() == null || started.get().getTime() == 0) {
            uptime.setValue(new Period(0));
        } else {
            uptime.setValue(new Period(DateTime.now().minus(started.get().getTime()).getMillis()));
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
     * Get the uptime value.
     *
     * @return an <code>ObjectProperty&lt;Period&gt;</code> value
     */
    public ObjectProperty<Period> uptimeProperty()
    {
        return uptime;
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
     * Get the started property.
     *
     * @return an <code>ObjectProperty&lt;Date&gt;</code> value
     */
    public ObjectProperty<Date> startedProperty()
    {
        return started;
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
    private void setupRuntimeListener()
    {
        strategyStatus.addListener((observableValue,oldValue,newValue) -> updateRunningProperty());
    }
    private final ObjectProperty<Date> started = new SimpleObjectProperty<>();
    private final StringProperty strategyName = new SimpleStringProperty();
    private final ObjectProperty<Period> uptime = new SimpleObjectProperty<>();
    private final StringProperty owner = new SimpleStringProperty();
    private final ObjectProperty<StrategyStatus> strategyStatus = new SimpleObjectProperty<>();
    private final DoubleProperty uploadProgress = new SimpleDoubleProperty();
}
