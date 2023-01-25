package org.vaadin.teemu.wizards.event;

import org.vaadin.teemu.wizards.Wizard;

@SuppressWarnings("serial")
public class WizardStepSetChangedEvent extends WizardEvent {

    public WizardStepSetChangedEvent(Wizard source) {
        super(source);
    }

}
