package org.vaadin.teemu.wizards.event;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

@SuppressWarnings("serial")
public class WizardStepActivationEvent extends WizardEvent {

    private final WizardStep activatedStep;

    public WizardStepActivationEvent(Wizard source, WizardStep activatedStep) {
        super(source);
        this.activatedStep = activatedStep;
    }

    /**
     * Returns the {@link WizardStep} that was the activated.
     * 
     * @return the activated {@link WizardStep}.
     */
    public WizardStep getActivatedStep() {
        return activatedStep;
    }

}