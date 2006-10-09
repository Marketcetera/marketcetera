package org.marketcetera.photon.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import ca.odell.glazedlists.impl.beans.BeanProperty;

public class BeanCellModifier<T> implements ICellModifier {

    /** The easy way to work with JavaBean-like object properties */
    private Map<String,BeanProperty<T> > beanProperties = new HashMap<String, BeanProperty<T>>();

    private IElementChangeListener<T> elementChangeListener;

    public BeanCellModifier(IElementChangeListener<T> elementChangeListener) {
		this.elementChangeListener = elementChangeListener;
	}

	/**
     * Loads the property descriptors which are used to invoke property
     * access methods using the property names.
     */
    @SuppressWarnings("unchecked")
	private void loadPropertyDescriptor(Object beanObject, String propertyName) {
        Class<T> beanClass = (Class<T>) beanObject.getClass();
        beanProperties.put(propertyName, new BeanProperty<T>(beanClass, propertyName, true, true));
    }
    
	public boolean canModify(Object element, String property) {
		if(!beanProperties.containsKey(property)){
			try {
				loadPropertyDescriptor(element, property);
			} catch (IllegalArgumentException ex){
				return false;
			}
        }
        BeanProperty<T> beanProperty = beanProperties.get(property);
        return beanProperty.isWritable();
	}

	@SuppressWarnings("unchecked")
	public Object getValue(Object elementObj, String property) {
		T element = (T)elementObj;
		if(!beanProperties.containsKey(property)){
        	loadPropertyDescriptor(element, property);
        }
        BeanProperty<T> beanProperty = beanProperties.get(property);
        Object propertyObj = beanProperty.get(element);
        return propertyObj;
	}

	@SuppressWarnings("unchecked")
	public void modify(Object elementObj, String property, Object value) {
		if(!beanProperties.containsKey(property)){
        	loadPropertyDescriptor(elementObj, property);
        }
        BeanProperty<T> beanProperty = beanProperties.get(property);
        if (elementObj instanceof TableItem)
        {
			TableItem item = (TableItem) elementObj;
	        T element = (T)item.getData();
	        beanProperty.set(element, value);
	        elementChangeListener.elementChanged(element);
		}
	}

}
