package org.vaadin.teemu.wizards;

import java.util.Collection;

import org.apache.commons.compress.utils.Lists;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.shared.Registration;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractWizardProgressListener
        implements WizardProgressListener
{
    /* (non-Javadoc)
     * @see com.vaadin.flow.component.ComponentEventListener#onComponentEvent(com.vaadin.flow.component.ComponentEvent)
     */
    @Override
    public void onComponentEvent(ComponentEvent inEvent)
    {
        if(inEvent instanceof WizardCancelledEvent) {
            wizardCancelled((WizardCancelledEvent)inEvent);
        } else if(inEvent instanceof WizardCompletedEvent) {
            wizardCompleted((WizardCompletedEvent)inEvent);
        } else if(inEvent instanceof WizardStepActivationEvent) {
            activeStepChanged((WizardStepActivationEvent)inEvent);
        } else if(inEvent instanceof WizardStepSetChangedEvent) {
            stepSetChanged((WizardStepSetChangedEvent)inEvent);
        } else {
            throw new UnsupportedOperationException("Unhandled event type: " + inEvent.getClass().getSimpleName());
        }
    }
    /* (non-Javadoc)
     * @see org.vaadin.teemu.wizards.event.WizardProgressListener#setRegistration(com.vaadin.flow.shared.Registration)
     */
    @Override
    public void addRegistration(Registration inRegistration)
    {
        registrations.add(inRegistration);
    }
    /* (non-Javadoc)
     * @see org.vaadin.teemu.wizards.event.WizardProgressListener#getRegistration()
     */
    @Override
    public void cancel()
    {
        for(Registration registration : registrations) {
            registration.remove();
        }
        registrations.clear();
    }
    /**
     * listener registration token
     */
    private final Collection<Registration> registrations = Lists.newArrayList();
    private static final long serialVersionUID = 6542482625160190084L;
}
