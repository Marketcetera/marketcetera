package org.marketcetera.ui.view;

import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.TextField;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ValidatingTextField
        extends TextField
{
    public ValidatingTextField(Predicate<String> inValidation)
    {
        validation = inValidation;
        textProperty().addListener((observableValue, oldValue, newValue) -> {
            isValidPropertyWrapper.set(validation.test(newValue));
        });
        isValidPropertyWrapper.set(true);
    }
    public ReadOnlyBooleanWrapper isValidProperty()
    {
        return isValidPropertyWrapper;
    }
    private final ReadOnlyBooleanWrapper isValidPropertyWrapper = new ReadOnlyBooleanWrapper();
    private final Predicate<String> validation;

}
