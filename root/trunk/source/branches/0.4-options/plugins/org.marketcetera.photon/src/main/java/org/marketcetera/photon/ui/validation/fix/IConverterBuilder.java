package org.marketcetera.photon.ui.validation.fix;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;

public interface IConverterBuilder {

	public abstract IConverter newToTargetConverter();

	public abstract IConverter newToModelConverter();

	public abstract IValidator newTargetAfterGetValidator();

	public abstract IValidator newModelAfterGetValidator();

}