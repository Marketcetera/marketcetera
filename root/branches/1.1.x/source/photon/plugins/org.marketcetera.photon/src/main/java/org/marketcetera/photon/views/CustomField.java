package org.marketcetera.photon.views;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class CustomField {
	private boolean isEnabled;

	private final PropertyChangeSupport propertyChangeSupport;
	
	private static final String UTF_8 = "UTF-8";  //$NON-NLS-1$

	private final String keyString;

	private final String valueString;

	public CustomField(String keyString, String valueString) {
		this.keyString = keyString;
		this.valueString = valueString;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setEnabled(boolean isEnabled) {
		boolean oldValue = this.isEnabled;
		this.isEnabled = isEnabled;
		propertyChangeSupport.firePropertyChange("enabled", oldValue, isEnabled); //$NON-NLS-1$
	}
	
	public String getKeyString() {
		return keyString;
	}
	
	public String getValueString() {
		return valueString;
	}
	
	public static CustomField fromString(String aString){
		String[] keyValueArray = aString.split("="); //$NON-NLS-1$
		CustomField outField;
		try {
			outField = new CustomField(URLDecoder.decode(keyValueArray[0], UTF_8)
					, URLDecoder.decode(keyValueArray[1], UTF_8));
		} catch (UnsupportedEncodingException e) {
			outField = new CustomField(keyValueArray[0]
					, keyValueArray[1]);
		}
		return outField;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		try {
			buf.append(URLEncoder.encode(keyString, UTF_8));
			buf.append("="); //$NON-NLS-1$
			buf.append(URLEncoder.encode(valueString, UTF_8));
		} catch (UnsupportedEncodingException e) {
			buf.append(keyString);
			buf.append("="); //$NON-NLS-1$
			buf.append(valueString);
		}
		return buf.toString();
	}

	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		propertyChangeSupport.addPropertyChangeListener(arg0);
	}

	public void addPropertyChangeListener(String arg0,
			PropertyChangeListener arg1) {
		propertyChangeSupport.addPropertyChangeListener(arg0, arg1);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return propertyChangeSupport.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners(String arg0) {
		return propertyChangeSupport.getPropertyChangeListeners(arg0);
	}

	public boolean hasListeners(String arg0) {
		return propertyChangeSupport.hasListeners(arg0);
	}

	public void removePropertyChangeListener(PropertyChangeListener arg0) {
		propertyChangeSupport.removePropertyChangeListener(arg0);
	}

	public void removePropertyChangeListener(String arg0,
			PropertyChangeListener arg1) {
		propertyChangeSupport.removePropertyChangeListener(arg0, arg1);
	}
}
