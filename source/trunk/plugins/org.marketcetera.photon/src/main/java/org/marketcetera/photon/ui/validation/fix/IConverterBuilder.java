package org.marketcetera.photon.ui.validation.fix;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;

/**
 * Interface for classes that know how to produce converters and
 * validators for use with the Eclipse data binding libraries.
 * @author gmiller
 *
 */
public interface IConverterBuilder {

	public abstract IConverter newToTargetConverter();

	public abstract IConverter newToModelConverter();

	public abstract IValidator newTargetAfterGetValidator();

	public abstract IValidator newModelAfterGetValidator();

}