package org.marketcetera.photon.test;

import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;

import org.apache.commons.lang.ObjectUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class IsExpectedPropertyChangeEvent extends
			BaseMatcher<PropertyChangeEvent> {

		String propertyName;
		Object oldValue;
		Object newValue;

		public IsExpectedPropertyChangeEvent(String propertyName,
				Object oldValue, Object newValue) {
			super();
			this.propertyName = propertyName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@Override
		public boolean matches(Object argument) {
			PropertyChangeEvent event = (PropertyChangeEvent) argument;
			return event.getPropertyName().equals(propertyName)
					&& ObjectUtils.equals(event.getOldValue(), oldValue)
					&& ObjectUtils.equals(event.getNewValue(), newValue);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(MessageFormat.format(
					"<Property \"{0}\" changed from \"{1}\" to \"{2}\">", //$NON-NLS-1$
					propertyName, oldValue, newValue));

		}

	}