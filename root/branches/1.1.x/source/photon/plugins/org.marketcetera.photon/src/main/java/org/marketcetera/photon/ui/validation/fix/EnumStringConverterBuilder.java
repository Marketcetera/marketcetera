package org.marketcetera.photon.ui.validation.fix;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.validation.SetValidator;
import org.marketcetera.photon.ui.validation.StringSetValidator;

/**
 * A builder object for creating instances of {@link IConverter}
 * that can convert from one set of objects to another and back
 * based on a set of mappings.
 * 
 * The "from type" in the forward direction is specified by the
 * type parameter, and the "from type" is always String.  The backward
 * direction has the opposite type assignments.
 * @author gmiller
 * 
 * Note that this class is intended for use with case-insensitive conversions.
 *
 * @param <FROM_T>
 */
public class EnumStringConverterBuilder<FROM_T>
    implements IConverterBuilder, Messages
{

	protected final Map<FROM_T,String> map = new HashMap<FROM_T,String>();
	private final Object fromType;
	
	public EnumStringConverterBuilder(Object fromType) {
		this.fromType = fromType;
	}

	public void addMapping(FROM_T from, String to)
	{
		map.put(from, to);
	}
	
	
	/**
	 * @see org.marketcetera.photon.ui.validation.fix.IConverterBuilder#newToTargetConverter()
	 */
	public IConverter newToTargetConverter(){
		return new ForwardConverter(map);
	}
	
	/**
	 * The converter returned will match strings from the UI in a case-insensitive fashion.
	 * 
	 * @see org.marketcetera.photon.ui.validation.fix.IConverterBuilder#newToModelConverter()
	 */
	public IConverter newToModelConverter(){
		return new BackwardConverter(map);
	}
	
	/* (non-Javadoc)
	 * @see org.marketcetera.photon.ui.validation.fix.IConverterBuilder#newTargetAfterGetValidator()
	 */
	public IValidator newTargetAfterGetValidator(){
		return new StringSetValidator(map.values(),
		                              PhotonPlugin.ID,
		                              INVALID_VALUE.getText());
	}
	
	/* (non-Javadoc)
	 * @see org.marketcetera.photon.ui.validation.fix.IConverterBuilder#newModelAfterGetValidator()
	 */
	public IValidator newModelAfterGetValidator(){
		return new SetValidator<FROM_T>(map.keySet(),
		        PhotonPlugin.ID,
		        INVALID_VALUE.getText());
	}
	
	protected class ForwardConverter extends Converter
	{
		Map<FROM_T,String> map;

		public ForwardConverter(Map<FROM_T,String> sourceMap) {
			super(fromType, String.class);
			map = new HashMap<FROM_T, String>(sourceMap);
		}

		public Object convert(Object from) {
			return map.get(from);
		}
	}
	
	protected class BackwardConverter extends Converter
	{
		Map<String,FROM_T> map = new HashMap<String, FROM_T>();

		public BackwardConverter(Map<FROM_T,String> sourceMap) {
			super(String.class, fromType);
			for (FROM_T sourceKey : sourceMap.keySet()) {
				map.put( sourceMap.get(sourceKey), sourceKey);
			}
		}

		public Object convert(Object from) {
			if (map.containsKey(from)){
				return map.get(from);
			} else if (from != null){
				return map.get(from.toString().toUpperCase());
			} else {
				return null;
			}
		}
	}

	
	
}
