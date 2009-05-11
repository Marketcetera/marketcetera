package org.marketcetera.photon.test;

import java.beans.PropertyChangeEvent;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class IsExpectedPropertyChangeEvent extends BaseMatcher<PropertyChangeEvent> {

    private final String propertyName;
    private final Matcher<?> oldValue;
    private final Matcher<?> newValue;

    public IsExpectedPropertyChangeEvent(String propertyName, Matcher<?> oldValue,
            Matcher<?> newValue) {
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public boolean matches(Object argument) {
        PropertyChangeEvent event = (PropertyChangeEvent) argument;
        return event.getPropertyName().equals(propertyName)
                && oldValue.matches(event.getOldValue())
                && newValue.matches(event.getNewValue());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("<Property ").appendValue(propertyName)
                .appendText(" changing from ").appendValue(oldValue).appendText(" to ")
                .appendValue(newValue).appendText(">");
    }

    public static Matcher<PropertyChangeEvent> isPropertyChange(String propertyName,
            Matcher<?> oldValue, Matcher<?> newValue) {
        return new IsExpectedPropertyChangeEvent(propertyName, oldValue, newValue);
    }
}