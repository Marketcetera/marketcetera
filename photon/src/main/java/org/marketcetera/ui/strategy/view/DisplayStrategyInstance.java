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
     *
     * @param inName a <code>String</code> value
     * @param inOwnerName a <code>String</code> value
     */
    public DisplayStrategyInstance(String inName,
                                   String inOwnerName)
    {
        strategyNameProperty.setValue(inName);
        ownerProperty.setValue(inOwnerName);
        strategyStatusProperty.setValue(StrategyStatus.PREPARING);
        uploadProgress.setValue(0.0);
        startedProperty.setValue(null);
        uptimeProperty.setValue(new Period(0));
        setupRuntimeListener();
    }
    /**
     * Create a new DisplayStrategy instance.
     *
     * @param inStrategyInstance a <code>StrategyInstance</code> value
     */
    public DisplayStrategyInstance(StrategyInstance inStrategyInstance)
    {
        strategyNameProperty.setValue(inStrategyInstance.getName());
        ownerProperty.setValue(inStrategyInstance.getUser().getName());
        strategyStatusProperty.setValue(inStrategyInstance.getStatus());
        startedProperty.setValue(inStrategyInstance.getStarted());
        setupRuntimeListener();
        updateRunningProperty();
    }
    /**
     * Update the running property. 
     */
    public void updateRunningProperty()
    {
        if(startedProperty.get() == null || startedProperty.get().getTime() == 0) {
            uptimeProperty.setValue(new Period(0));
        } else {
            uptimeProperty.setValue(new Period(DateTime.now().minus(startedProperty.get().getTime()).getMillis()));
        }
    }
    /**
     * Get the strategyName value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty strategyNameProperty()
    {
        return strategyNameProperty;
    }
    /**
     * Get the uptime value.
     *
     * @return an <code>ObjectProperty&lt;Period&gt;</code> value
     */
    public ObjectProperty<Period> uptimeProperty()
    {
        return uptimeProperty;
    }
    /**
     * Get the owner value.
     *
     * @return a <code>ReadOnlyStringProperty</code> value
     */
    public ReadOnlyStringProperty ownerProperty()
    {
        return ownerProperty;
    }
    /**
     * Get the started property.
     *
     * @return an <code>ObjectProperty&lt;Date&gt;</code> value
     */
    public ObjectProperty<Date> startedProperty()
    {
        return startedProperty;
    }
    /**
     * Get the strategyStatus value.
     *
     * @return an <code>ObjectProperty&lt;StrategyStatus&gt;</code> value
     */
    public ObjectProperty<StrategyStatus> strategyStatusProperty()
    {
        return strategyStatusProperty;
    }
    /**
     * Get the upload progress property.
     *
     * @return a <code>DoubleProperty</code> value
     */
    public DoubleProperty uploadProgressProperty()
    {
        return uploadProgress;
    }
    /**
     * Initialize the runtime listener for updating strategy status.
     */
    private void setupRuntimeListener()
    {
        strategyStatusProperty.addListener((observableValue,oldValue,newValue) -> updateRunningProperty());
    }
    /**
     * started date property
     */
    private final ObjectProperty<Date> startedProperty = new SimpleObjectProperty<>();
    /**
     * strategy name property
     */
    private final StringProperty strategyNameProperty = new SimpleStringProperty();
    /**
     * uptime property
     */
    private final ObjectProperty<Period> uptimeProperty = new SimpleObjectProperty<>();
    /**
     * owner property
     */
    private final StringProperty ownerProperty = new SimpleStringProperty();
    /**
     * strategy status property
     */
    private final ObjectProperty<StrategyStatus> strategyStatusProperty = new SimpleObjectProperty<>();
    /**
     * upload property
     */
    private final DoubleProperty uploadProgress = new SimpleDoubleProperty();
}
