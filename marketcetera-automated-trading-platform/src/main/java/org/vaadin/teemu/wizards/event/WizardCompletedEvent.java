package org.vaadin.teemu.wizards.event;

import org.vaadin.teemu.wizards.Wizard;

@SuppressWarnings("serial")
public class WizardCompletedEvent extends WizardEvent {

    public WizardCompletedEvent(Wizard source) {
        super(source);
    }

}
