package org.marketcetera.photon.ui.validation.fix;

import org.eclipse.core.databinding.conversion.IConverter;
import org.marketcetera.photon.ui.validation.IToggledValidator;

public interface IConverterBuilder {

	public abstract IConverter newToTargetConverter();

	public abstract IConverter newToModelConverter();

	public abstract IToggledValidator newTargetAfterGetValidator();

	public abstract IToggledValidator newModelAfterGetValidator();

}