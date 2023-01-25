package org.vaadin.teemu.wizards;

import java.util.Collection;
import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.shared.Registration;

/**s
 * Displays a progress bar for a {@link Wizard}.
 */
@SuppressWarnings({ "serial", "rawtypes" })
@StyleSheet("wizard-progress-bar.css")
public class WizardProgressBar
        extends HorizontalLayout
        implements WizardProgressListener
{

    private final Wizard wizard;
    private final ProgressBar progressBar = new ProgressBar();
    private final HorizontalLayout stepCaptions = new HorizontalLayout();
    private int activeStepIndex;

    public WizardProgressBar(Wizard wizard)
    {
        addClassName("wizard-progress-bar");
        this.wizard = wizard;
        stepCaptions.setWidth("100%");
        progressBar.setWidth("100%");
        progressBar.setHeight("13px");

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setMargin(false);
        layout.setWidth("100%");
        layout.add(stepCaptions);
        layout.add(progressBar);
        setWidth("100%");
        add(layout);
    }
    /* (non-Javadoc)
     * @see org.vaadin.teemu.wizards.event.WizardProgressListener#onWizardEvent(org.vaadin.teemu.wizards.event.WizardEvent)
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
    private void updateProgressBar() {
        int stepCount = wizard.getSteps().size();
        float padding = (1.0f / stepCount) / 2;
        float progressValue = padding + activeStepIndex / (float) stepCount;
        progressBar.setValue(progressValue);
    }

    private void updateStepCaptions() {
        stepCaptions.removeAll();
        int index = 1;
        for (WizardStep step : wizard.getSteps()) {
            Label label = createCaptionLabel(index, step);
            stepCaptions.add(label);
            index++;
        }
    }

    private Label createCaptionLabel(int index, WizardStep step) {
        Label label = new Label(index + ". " + step.getCaption());
        label.addClassName("step-caption");
        label.setWidth("100%");

        // Add styles for themeing.
        if (wizard.isCompleted(step)) {
            label.addClassName("completed");
        }
        if (wizard.isActive(step)) {
            label.addClassName("current");
        }
        if (wizard.isFirstStep(step)) {
            label.addClassName("first");
        }
        if (wizard.isLastStep(step)) {
            label.addClassName("last");
        }

        return label;
    }

    private void updateProgressAndCaptions() {
        updateProgressBar();
        updateStepCaptions();
    }

    @Override
    public void activeStepChanged(WizardStepActivationEvent event) {
        List<WizardStep> allSteps = wizard.getSteps();
        activeStepIndex = allSteps.indexOf(event.getActivatedStep());
        updateProgressAndCaptions();
    }

    @Override
    public void stepSetChanged(WizardStepSetChangedEvent event) {
        updateProgressAndCaptions();
    }

    @Override
    public void wizardCompleted(WizardCompletedEvent event) {
        progressBar.setValue(1.0f);
        updateStepCaptions();
    }

    @Override
    public void wizardCancelled(WizardCancelledEvent event) {
        // NOP, no need to react to cancellation
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
    private final Collection<Registration> registrations = Lists.newArrayList();
}
