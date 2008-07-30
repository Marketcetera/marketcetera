package org.marketcetera.photon.ui.validation.fix;

import java.math.BigDecimal;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.validation.SetValidator;

import quickfix.DataDictionary;
import quickfix.field.OrdType;

/**
 * Builder that knows how to produce converters and validators for price values.
 * Price values can be represented as a string that is a valid decimal, or the special
 * string "MKT" meaning "market order" or "no price".
 * 
 * @author gmiller
 *
 */
public class PriceConverterBuilder
    extends EnumStringConverterBuilder<Character>
    implements IConverterBuilder, Messages
{
	protected DataDictionary dictionary;

	public PriceConverterBuilder(DataDictionary dictionary) {
		super(Character.class);
		this.dictionary = dictionary;
	}

	/**
	 * Produce a validator that succeeds if the field from model is 
	 * either {@link OrdType#MARKET} or a String representing a decimal value 
	 * 
	 * @return the validator
	 */
	@Override
	public IValidator newModelAfterGetValidator() {
		return new IValidator(){
			public IStatus validate(Object obj) {
			    IStatus errorStatus = new Status(IStatus.ERROR,
			                                     PhotonPlugin.ID,
			                                     IStatus.OK,
			                                     INVALID_VALUE.getText(),
			                                     null);
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

	/**
	 * Produce a validator that succeeds if the value is 
	 * either {@link OrdType#MARKET} or a String representing a decimal value 
	 * 
	 * @return the validator
	 */
	public IValidator newTargetAfterGetValidator() {
		return new SetValidator<String>(map.values(),
		                                PhotonPlugin.ID,
		                                INVALID_VALUE.getText()) {
			@Override
			public IStatus validate(Object obj) {
				if (obj == null){
					return super.validate(obj);
				} else {
					try {
						new BigDecimal((String) obj);
						return Status.OK_STATUS;
					} catch (Throwable t){
						return (obj instanceof String) ? super.validate(((String)obj).toUpperCase()) : super.validate(obj);
					}
				}
			}
		};
	}

	public IConverter newToModelConverter() {
		return new BackwardConverter(map){
			@Override
			public Object convert(Object from) {
				if (from == null){
					return super.convert(from);
				} else {
					try {
						return new BigDecimal((String)from);
					} catch (Throwable t) {
						return (from instanceof String) ? super.convert(((String)from).toUpperCase()) : super.convert(from);
					}
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
