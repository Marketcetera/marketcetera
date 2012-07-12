package org.marketcetera.photon.commons.ui.databinding;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Factory for creating databinding UpdateValueStrategies.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public final class UpdateStrategyFactory {

    /**
     * Creates an {@link UpdateValueStrategy} that behaves exactly like
     * {@link EMFUpdateValueStrategy} except that empty strings are converted to
     * null before doing the EMF type conversion.
     * 
     * @return an UpdateValueStrategy that converts empty strings to null
     */
    public static UpdateValueStrategy createEMFUpdateValueStrategyWithEmptyStringToNull() {
        return new EMFUpdateValueStrategy() {
            @Override
            protected IConverter createConverter(Object fromType, Object toType) {
                // get the default EMF converter
                final IConverter defaultConverter = super.createConverter(
                        fromType, toType);
                return new Converter(fromType, toType) {
                    @Override
                    public Object convert(Object fromObject) {
                        // use the default converter, but replace empty string
                        // will null first
                        if (fromObject instanceof String
                                && ((String) fromObject).isEmpty()) {
                            fromObject = null;
                        }
                        return defaultConverter.convert(fromObject);
                    }
                };
            }
        };
    }

    /**
     * Adds and "afterGet" validator to the update value strategy that attempts
     * conversion and fails if the conversion fails. This is needed since
     * conversion failures often do not produce readable messages.
     * 
     * @param strategy
     *            the update value strategy to enhance
     * @param message
     *            the error message for conversion failures
     * @return the strategy passed in for method chaining
     */
    public static UpdateValueStrategy withConvertErrorMessage(
            final UpdateValueStrategy strategy, final String message) {
        return strategy.setAfterGetValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                try {
                    strategy.convert(value);
                    return ValidationStatus.ok();
                } catch (Exception e) {
                    return ValidationStatus.error(message, e);
                }
            }
        });
    }

    private UpdateStrategyFactory() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
