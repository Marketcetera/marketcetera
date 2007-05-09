package org.marketcetera.photon.ui.validation.fix;

import java.math.BigDecimal;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.validation.SetValidator;

import quickfix.DataDictionary;
import quickfix.field.OrdType;

public class PriceConverterBuilder extends EnumStringConverterBuilder<Character> implements IConverterBuilder {
	

	protected DataDictionary dictionary;
	protected IStatus errorStatus = new Status(IStatus.ERROR,PhotonPlugin.ID, IStatus.OK, "Invalid value", null);

	public PriceConverterBuilder(DataDictionary dictionary) {
		super(Character.class);
		this.dictionary = dictionary;
	}


	public IValidator newModelAfterGetValidator() {
		return new IValidator(){
			public IStatus validate(Object obj) {
				if (obj == null){
					return Status.OK_STATUS;
				} else {
					if (obj instanceof Character){
						if (dictionary.isFieldValue(OrdType.FIELD, obj.toString())){
							return Status.OK_STATUS;
						} else {
							return errorStatus;
						}
					} else if (obj instanceof String){
						try {
							new BigDecimal((String)obj);
							return Status.OK_STATUS;
						} catch (Exception e) {
							return errorStatus;
						}
					} else {
						return errorStatus;
					}
				}
			}
		};
	}

	public IValidator newTargetAfterGetValidator() {
		return new SetValidator<String>(map.values(), PhotonPlugin.ID, "Not a valid value") {
			@Override
			public IStatus validate(Object obj) {
				if (!isEnabled()) {
					return Status.OK_STATUS;
				}
				try {
					new BigDecimal((String) obj);
					return Status.OK_STATUS;
				} catch (Throwable t){
					return super.validate(obj);
				}
			}
		};
	}

	public IConverter newToModelConverter() {
		return new BackwardConverter(map){
			@Override
			public Object convert(Object from) {
				try {
					return new BigDecimal((String)from);
				} catch (Throwable t) {
					return super.convert(from);
				}
			}
			@Override
			public Object getToType() {
				return Object.class;
			}

		};
	}

	public IConverter newToTargetConverter() {
		return new ForwardConverter(map){
			@Override
			public Object convert(Object from) {
				if (from instanceof String) {
					BigDecimal bd = new BigDecimal(from.toString());
					return bd.toString();
				} else {
					return super.convert(from);
				}
			}
			@Override
			public Object getFromType() {
				return Object.class;
			}
		};
	}

}
