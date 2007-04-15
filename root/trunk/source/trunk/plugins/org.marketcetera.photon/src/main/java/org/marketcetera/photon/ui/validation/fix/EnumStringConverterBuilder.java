package org.marketcetera.photon.ui.validation.fix;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.validation.SetValidator;

public class EnumStringConverterBuilder<FROM_T> implements IConverterBuilder {

	protected Map<FROM_T,String> map = new HashMap<FROM_T,String>();
	private final Object fromType;
	
	public EnumStringConverterBuilder(Object fromType) {
		this.fromType = fromType;
	}

	public void addMapping(FROM_T from, String to)
	{
		map.put(from, to);
	}
	
	
	/* (non-Javadoc)
	 * @see org.marketcetera.photon.ui.validation.fix.IConverterBuilder#newToTargetConverter()
	 */
	public IConverter newToTargetConverter(){
		return new ForwardConverter(map);
	}
	
	/* (non-Javadoc)
	 * @see org.marketcetera.photon.ui.validation.fix.IConverterBuilder#newToModelConverter()
	 */
	public IConverter newToModelConverter(){
		return new BackwardConverter(map);
	}
	
	/* (non-Javadoc)
	 * @see org.marketcetera.photon.ui.validation.fix.IConverterBuilder#newTargetAfterGetValidator()
	 */
	public IValidator newTargetAfterGetValidator(){
		return new SetValidator<String>(map.values(), PhotonPlugin.ID, "Not a valid value");
	}
	
	/* (non-Javadoc)
	 * @see org.marketcetera.photon.ui.validation.fix.IConverterBuilder#newModelAfterGetValidator()
	 */
	public IValidator newModelAfterGetValidator(){
		return new SetValidator<FROM_T>(map.keySet(), PhotonPlugin.ID, "Not a valid value");
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
			return map.get(from);
		}
	}

	
	
}
