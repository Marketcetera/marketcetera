package org.marketcetera.photon.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.algo.BrokerAlgoTag;

/* $License$ */

/**
 * Provides an observable AlgoTag implementation
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ObservableAlgoTag
{
    /**
     * Create a new AlgoTag instance.
     *
     * @param inAlgoTag a <code>BrokerAlgoTag</code> value
     */
    public ObservableAlgoTag(BrokerAlgoTag inAlgoTag)
    {
        algoTag = inAlgoTag;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    /**
     * Indicates if the algo is enabled or not.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isEnabled()
    {
        return isEnabled;
    }
    /**
     * Set if the algo is enabled or not.
     *
     * @param isEnabled a <code>boolean</code> value
     */
    public void setEnabled(boolean isEnabled)
    {
        boolean oldValue = this.isEnabled;
        this.isEnabled = isEnabled;
        propertyChangeSupport.firePropertyChange("enabled", oldValue, isEnabled); //$NON-NLS-1$
    }
    /**
     * Get the key string value.
     *
     * @return a <code>String</code> value
     */
    public String getKeyString()
    {
        return algoTag.getLabel();
    }
    /**
     * Get the value string value.
     *
     * @return a <code>String</code> value
     */
    public String getValueString()
    {
        return algoTag.getValue();
    }
    /**
     * Get the description string value.
     *
     * @return a <code>String</code> value
     */
    public String getDescriptionString()
    {
        return algoTag.getTagSpec().getDescription();
    }
    /**
     * Set the value.
     *
     * @param inValue a <code>String</code> value
     */
    public void setValue(String inValue)
    {
        String oldValue = algoTag.getValue();
        String value = StringUtils.trimToNull(inValue);
        algoTag.setValue(inValue);
        if(oldValue != null){
            if(!oldValue.equals(value)){
                propertyChangeSupport.firePropertyChange("value",
                                                         oldValue,
                                                         value);
            }
        } else {
            if(value != null)
                propertyChangeSupport.firePropertyChange("value",
                                                         oldValue,
                                                         value);
        }
    }
    /**
     * Add the given property change listener.
     *
     * @param inListener a <code>PropertyChangeListener</code> value
     */
    public void addPropertyChangeListener(PropertyChangeListener inListener)
    {
        propertyChangeSupport.addPropertyChangeListener(inListener);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return algoTag.toString();
    }
    /**
     * Add the given property change listener for the given property name.
     *
     * @param inProperty a <code>String</code> value
     * @param inListener a <code>PropertyChangeListener</code> value
     */
    public void addPropertyChangeListener(String inProperty,
                                          PropertyChangeListener inListener)
    {
        propertyChangeSupport.addPropertyChangeListener(inProperty,
                                                        inListener);
    }
    /**
     * Get the property change listeners.
     *
     * @return a <code>PropertyChangeListener[]</code> value
     */
    public PropertyChangeListener[] getPropertyChangeListeners()
    {
        return propertyChangeSupport.getPropertyChangeListeners();
    }
    /**
     * Get the property change listeners for the given property.
     *
     * @param inProperty a <code>String</code> value
     * @return a <code>PropertyChangeListener[]</code> value
     */
    public PropertyChangeListener[] getPropertyChangeListeners(String inProperty)
    {
        return propertyChangeSupport.getPropertyChangeListeners(inProperty);
    }
    /**
     * Indicates if the given property has listeners or not.
     *
     * @param inProperty a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public boolean hasListeners(String inProperty)
    {
        return propertyChangeSupport.hasListeners(inProperty);
    }
    /**
     * Remove the given property change listener.
     *
     * @param inListener a <code>PropertyChangeListener</code> value
     */
    public void removePropertyChangeListener(PropertyChangeListener inListener)
    {
        propertyChangeSupport.removePropertyChangeListener(inListener);
    }
    /**
     * Remove the given property change listener from the given property.
     *
     * @param inProperty a <code>String</code> value
     * @param inListener a <code>PropertyChangeListener</code> value
     */
    public void removePropertyChangeListener(String inProperty,
                                             PropertyChangeListener inListener)
    {
        propertyChangeSupport.removePropertyChangeListener(inProperty, inListener);
    }
    /**
     * Get the underlying broker algo tag value.
     *
     * @return a <code>BrokerAlgoTag</code> value
     */
    public BrokerAlgoTag getAlgoTag()
    {
        return algoTag;
    }
    /**
     * Validate the value applied to the underlying broker algo tag.
     */
    public void validate()
    {
        algoTag.validate();
    }
    /**
     * indicates if the row is checked or not
     */
    private boolean isEnabled;
    /**
     * watches properties
     */
    private final PropertyChangeSupport propertyChangeSupport;
    /**
     * underlying broker algo tag value
     */
    private final BrokerAlgoTag algoTag;
}
