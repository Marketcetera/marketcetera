package org.vaadin.teemu.wizards.event;

import org.vaadin.teemu.wizards.Wizard;

import com.vaadin.flow.component.ComponentEvent;

@SuppressWarnings("serial")
public class WizardEvent extends ComponentEvent<Wizard> {

    protected WizardEvent(Wizard source)
    {
        super(source,
              false);
    }

    /**
     * Returns the {@link Wizard} component that was the source of this event.
     * 
     * @return the source {@link Wizard} of this event.
     */
    public Wizard getWizard() {
        return (Wizard) getSource();
    }
}
