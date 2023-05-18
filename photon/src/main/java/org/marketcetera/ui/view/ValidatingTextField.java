package org.marketcetera.ui.view;

import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.TextField;

/* $License$ */

/**
 * Provides a {@link TextField} implementation that provides built-in validation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ValidatingTextField
        extends TextField
{
    /**
     * Create a new ValidatingTextField instance.
     *
     * @param inValidation a <code>Predicate&lt;String&gt;</code> value
     */
    public ValidatingTextField(Predicate<String> inValidation)
    {
        validation = inValidation;
        textProperty().addListener((observableValue, oldValue, newValue) -> {
            isValidPropertyWrapper.set(validation.test(newValue));
        });
        isValidPropertyWrapper.set(true);
    }
    /**
     * Get the predicate of the field.
     *
     * @return a <code>Predicate&lt;String&gt;</code> value
     */
    public Predicate<String> getPredicate()
    {
        return validation;
    }
    /**
     * Provides access to the is-valid property of the field.
     *
     * @return a <code>ReadOnlyBooleanWrapper</code> value
     */
    public ReadOnlyBooleanWrapper isValidProperty()
    {
        return isValidPropertyWrapper;
    }
    /**
     * observable value of the is-valid property
     */
    private final ReadOnlyBooleanWrapper isValidPropertyWrapper = new ReadOnlyBooleanWrapper();
    /**
     * validation predicate to be executed to determine if the field is valid
     */
    private final Predicate<String> validation;

}
