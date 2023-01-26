package org.vaadin.teemu.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Component for displaying multi-step wizard style user interface.
 * 
 * <p>
 * The steps of the wizard must be implementations of the {@link WizardStep}
 * interface. Use the {@link #addStep(WizardStep)} method to add these steps in
 * the same order they are supposed to be displayed.
 * </p>
 * 
 * <p>
 * The wizard also supports navigation through URI fragments. This feature is
 * disabled by default, but you can enable it using
 * {@link #setUriFragmentEnabled(boolean)} method. Each step will get a
 * generated identifier that is used as the URI fragment. If you wish to
 * override these with your own identifiers, you can add the steps using the
 * overloaded {@link #addStep(WizardStep, String)} method.
 * </p>
 * 
 * <p>
 * To react on the progress, cancellation or completion of this {@code Wizard}
 * you should add one or more listeners that implement the
 * {@link WizardProgressListener} interface. These listeners are added using the
 * {@link #addListener(WizardProgressListener)} method and removed with the
 * {@link #removeListener(WizardProgressListener)}.
 * </p>
 * 
 * @author Teemu Pöntelin / Vaadin Ltd
 */
@SuppressWarnings("serial")
public class Wizard
        extends VerticalLayout
//        implements UriFragmentChangedListener
{

    protected final List<WizardStep> steps = new ArrayList<WizardStep>();
    protected final Map<String, WizardStep> idMap = new HashMap<String, WizardStep>();
    private final Map<WizardStep, ScrollPosition> scrollPositions = new HashMap<WizardStep, ScrollPosition>();

    protected WizardStep currentStep;
    protected WizardStep lastCompletedStep;

    private int stepIndex = 1;

    protected VerticalLayout mainLayout;
    protected HorizontalLayout footer;
    private Panel contentPanel;

    private Button nextButton;
    private Button backButton;
    private Button finishButton;
    private Button cancelButton;

    private Component header;
    private boolean uriFragmentEnabled;

    private static final class ScrollPosition {
        int scrollTop;
        int scrollLeft;

        public ScrollPosition(int scrollTop, int scrollLeft) {
            this.scrollTop = scrollTop;
            this.scrollLeft = scrollLeft;
        }
    }

    public Wizard()
    {
        init();
    }

    private void init() {
        mainLayout = new VerticalLayout();
        mainLayout.addClassName("wizard");

        contentPanel = new Panel();
        contentPanel.setSizeFull();

        initControlButtons();

        footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.add(cancelButton);
        footer.add(backButton);
        footer.add(nextButton);
        footer.add(finishButton);

//        mainLayout.setComponentAlignment(footer,
//                                         Alignment.END);

//        mainLayout.setExpandRatio(contentPanel, 1.0f);
        mainLayout.setSizeFull();

        initDefaultHeader();
        mainLayout.add(contentPanel);
        mainLayout.add(footer);
        add(mainLayout);
    }

    private void initControlButtons() {
        nextButton = new Button("Next");
        nextButton.addClickListener(event -> next());

        backButton = new Button("Back");
        backButton.addClickListener(event -> back());

        finishButton = new Button("Finish");
        finishButton.addClickListener(event -> finish());
        finishButton.setEnabled(false);

        cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> cancel());
    }

    private void initDefaultHeader() {
        WizardProgressBar progressBar = new WizardProgressBar(this);
        addListener(progressBar);
        setHeader(progressBar);
    }

//    public void setUriFragmentEnabled(boolean enabled) {
//        if (enabled) {
//            Page.getCurrent().addUriFragmentChangedListener(this);
//        } else {
//            Page.getCurrent().removeUriFragmentChangedListener(this);
//        }
//        uriFragmentEnabled = enabled;
//    }

    public boolean isUriFragmentEnabled() {
        return uriFragmentEnabled;
    }

    /**
     * Sets a {@link Component} that is displayed on top of the actual content.
     * Set to {@code null} to remove the header altogether.
     * 
     * @param newHeader
     *            {@link Component} to be displayed on top of the actual content
     *            or {@code null} to remove the header.
     */
    public void setHeader(Component newHeader)
    {
        if (header != null) {
            if (newHeader == null) {
                mainLayout.remove(header);
            } else {
                mainLayout.replace(header, newHeader);
            }
        } else {
            if (newHeader != null) {
                mainLayout.addComponentAsFirst(newHeader);
            }
        }
        this.header = newHeader;
    }

    /**
     * Returns a {@link Component} that is displayed on top of the actual
     * content or {@code null} if no header is specified.
     * 
     * <p>
     * By default the header is a {@link WizardProgressBar} component that is
     * also registered as a {@link WizardProgressListener} to this Wizard.
     * </p>
     * 
     * @return {@link Component} that is displayed on top of the actual content
     *         or {@code null}.
     */
    public Component getHeader() {
        return header;
    }

    /**
     * Adds a step to this Wizard with the given identifier. The used {@code id}
     * must be unique or an {@link IllegalArgumentException} is thrown. If you
     * don't wish to explicitly provide an identifier, you can use the
     * {@link #addStep(WizardStep)} method.
     * 
     * @param step
     * @param id
     * @throws IllegalStateException
     *             if the given {@code id} already exists.
     */
    public void addStep(WizardStep step, String id) {
        if (idMap.containsKey(id)) {
            throw new IllegalArgumentException(
                    String.format(
                            "A step with given id %s already exists. You must use unique identifiers for the steps.",
                            id));
        }

        steps.add(step);
        idMap.put(id, step);
        updateButtons();

        // notify listeners
        fireEvent(new WizardStepSetChangedEvent(this));

        // activate the first step immediately
        if (currentStep == null) {
            activateStep(step);
        }
    }

    /* (non-Javadoc)
     * @see com.vaadin.flow.component.Component#fireEvent(com.vaadin.flow.component.ComponentEvent)
     */
    @Override
    protected void fireEvent(ComponentEvent<?> inComponentEvent)
    {
        super.fireEvent(inComponentEvent);
    }

    /**
     * Adds a step to this Wizard. The WizardStep will be assigned an identifier
     * automatically. If you wish to provide an explicit identifier for your
     * WizardStep, you can use the {@link #addStep(WizardStep, String)} method
     * instead.
     * 
     * @param step
     */
    public void addStep(WizardStep step) {
        addStep(step, "wizard-step-" + stepIndex++);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addListener(WizardProgressListener listener) {
        listener.addRegistration(addListener(WizardCancelledEvent.class,
                                             listener));
        listener.addRegistration(addListener(WizardCompletedEvent.class,
                                             listener));
        listener.addRegistration(addListener(WizardStepActivationEvent.class,
                                             listener));
        listener.addRegistration(addListener(WizardStepSetChangedEvent.class,
                                             listener));
    }
    @SuppressWarnings("rawtypes")
    public void removeListener(WizardProgressListener listener) {
        listener.cancel();
    }

    public List<WizardStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    /**
     * Returns {@code true} if the given step is already completed by the user.
     * 
     * @param step
     *            step to check for completion.
     * @return {@code true} if the given step is already completed.
     */
    public boolean isCompleted(WizardStep step) {
        return steps.indexOf(step) < steps.indexOf(currentStep);
    }

    /**
     * Returns {@code true} if the given step is the currently active step.
     * 
     * @param step
     *            step to check for.
     * @return {@code true} if the given step is the currently active step.
     */
    public boolean isActive(WizardStep step) {
        return (step == currentStep);
    }

    private void updateButtons() {
        if (isLastStep(currentStep)) {
            finishButton.setEnabled(true);
            nextButton.setEnabled(false);
        } else {
            finishButton.setEnabled(false);
            nextButton.setEnabled(true);
        }
        backButton.setEnabled(!isFirstStep(currentStep));
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getFinishButton() {
        return finishButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    protected void activateStep(WizardStep step) {
        if (step == null) {
            return;
        }

        if (currentStep != null) {
            if (currentStep.equals(step)) {
                // already active
                return;
            }

            // ask if we're allowed to move
            boolean advancing = steps.indexOf(step) > steps
                    .indexOf(currentStep);
            if (advancing) {
                if (!currentStep.onAdvance()) {
                    // not allowed to advance
                    return;
                }
            } else {
                if (!currentStep.onBack()) {
                    // not allowed to go back
                    return;
                }
            }

            // keep track of the last step that was completed
            int currentIndex = steps.indexOf(currentStep);
            if (lastCompletedStep == null
                    || steps.indexOf(lastCompletedStep) < currentIndex) {
                lastCompletedStep = currentStep;
            }
            saveScrollPosition(currentStep);
        }
        if(currentStep != null) {
            contentPanel.remove(currentStep.getContent());
        }
        contentPanel.add(step.getContent());
        currentStep = step;
        restoreScrollPosition(currentStep);

//        updateUriFragment();
        updateButtons();
        fireEvent(new WizardStepActivationEvent(this, step));
    }

    private void restoreScrollPosition(WizardStep step) {
        ScrollPosition scrollPosition = scrollPositions.get(step);
        if (scrollPosition != null) {
            contentPanel.setScrollTop(scrollPosition.scrollTop);
            contentPanel.setScrollLeft(scrollPosition.scrollLeft);
        } else {
            // scroll to top
            contentPanel.setScrollTop(0);
            contentPanel.setScrollLeft(0);
        }
    }

    private void saveScrollPosition(WizardStep step) {
        // remove possible old value
        scrollPositions.remove(step);

        int scrollTop = contentPanel.getScrollTop();
        int scrollLeft = contentPanel.getScrollLeft();
        if (scrollTop > 0 || scrollLeft > 0) {
            // save only if not at the default value (both 0)
            scrollPositions
                    .put(step, new ScrollPosition(scrollTop, scrollLeft));
        }
    }

    protected void activateStep(String id) {
        WizardStep step = idMap.get(id);
        if (step != null) {
            // check that we don't go past the lastCompletedStep by using the id
            int lastCompletedIndex = lastCompletedStep == null ? -1 : steps
                    .indexOf(lastCompletedStep);
            int stepIndex = steps.indexOf(step);

            if (lastCompletedIndex < stepIndex) {
                activateStep(lastCompletedStep);
            } else {
                activateStep(step);
            }
        }
    }

    protected String getId(WizardStep step) {
        for (Map.Entry<String, WizardStep> entry : idMap.entrySet()) {
            if (entry.getValue().equals(step)) {
                return entry.getKey();
            }
        }
        return null;
    }

//    private void updateUriFragment() {
//        if (isUriFragmentEnabled()) {
//            String currentStepId = getId(currentStep);
//            if (currentStepId != null && currentStepId.length() > 0) {
//                Page.getCurrent().setUriFragment(currentStepId, false);
//            } else {
//                Page.getCurrent().setUriFragment(null, false);
//            }
//        }
//    }

    protected boolean isFirstStep(WizardStep step) {
        if (step != null) {
            return steps.indexOf(step) == 0;
        }
        return false;
    }

    protected boolean isLastStep(WizardStep step) {
        if (step != null && !steps.isEmpty()) {
            return steps.indexOf(step) == (steps.size() - 1);
        }
        return false;
    }

    /**
     * Cancels this Wizard triggering a {@link WizardCancelledEvent}. This
     * method is called when user clicks the cancel button.
     */
    public void cancel() {
        fireEvent(new WizardCancelledEvent(this));
    }

    /**
     * Triggers a {@link WizardCompletedEvent} if the current step is the last
     * step and it allows advancing (see {@link WizardStep#onAdvance()}). This
     * method is called when user clicks the finish button.
     */
    public void finish() {
        if (isLastStep(currentStep) && currentStep.onAdvance()) {
            // next (finish) allowed -> fire complete event
            fireEvent(new WizardCompletedEvent(this));
        }
    }

    /**
     * Activates the next {@link WizardStep} if the current step allows
     * advancing (see {@link WizardStep#onAdvance()}) or calls the
     * {@link #finish()} method the current step is the last step. This method
     * is called when user clicks the next button.
     */
    public void next() {
        if (isLastStep(currentStep)) {
            finish();
        } else {
            int currentIndex = steps.indexOf(currentStep);
            activateStep(steps.get(currentIndex + 1));
        }
    }

    /**
     * Activates the previous {@link WizardStep} if the current step allows
     * going back (see {@link WizardStep#onBack()}) and the current step is not
     * the first step. This method is called when user clicks the back button.
     */
    public void back() {
        int currentIndex = steps.indexOf(currentStep);
        if (currentIndex > 0) {
            activateStep(steps.get(currentIndex - 1));
        }
    }

//    @Override
//    public void uriFragmentChanged(UriFragmentChangedEvent event) {
//        if (isUriFragmentEnabled()) {
//            String fragment = event.getUriFragment();
//            if ((fragment == null || fragment.equals("")) && !steps.isEmpty()) {
//                // empty fragment -> set the fragment of first step
//                Page.getCurrent().setUriFragment(getId(steps.get(0)));
//            } else {
//                activateStep(fragment);
//            }
//        }
//    }

    /**
     * Removes the given step from this Wizard. An {@link IllegalStateException}
     * is thrown if the given step is already completed or is the currently
     * active step.
     * 
     * @param stepToRemove
     *            the step to remove.
     * @see #isCompleted(WizardStep)
     * @see #isActive(WizardStep)
     */
    public void removeStep(WizardStep stepToRemove) {
        if (idMap.containsValue(stepToRemove)) {
            for (Map.Entry<String, WizardStep> entry : idMap.entrySet()) {
                if (entry.getValue().equals(stepToRemove)) {
                    // delegate the actual removal to the overloaded method
                    removeStep(entry.getKey());
                    return;
                }
            }
        }
    }

    /**
     * Removes the step with given id from this Wizard. An
     * {@link IllegalStateException} is thrown if the given step is already
     * completed or is the currently active step.
     * 
     * @param id
     *            identifier of the step to remove.
     * @see #isCompleted(WizardStep)
     * @see #isActive(WizardStep)
     */
    public void removeStep(String id) {
        if (idMap.containsKey(id)) {
            WizardStep stepToRemove = idMap.get(id);
            if (isCompleted(stepToRemove)) {
                throw new IllegalStateException(
                        "Already completed step cannot be removed.");
            }
            if (isActive(stepToRemove)) {
                throw new IllegalStateException(
                        "Currently active step cannot be removed.");
            }

            idMap.remove(id);
            steps.remove(stepToRemove);

            // notify listeners
            fireEvent(new WizardStepSetChangedEvent(this));
        }
    }

}
