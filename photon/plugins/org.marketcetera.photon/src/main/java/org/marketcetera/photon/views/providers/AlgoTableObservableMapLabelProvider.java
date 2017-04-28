package org.marketcetera.photon.views.providers;

import java.util.Map;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.marketcetera.photon.views.ObservableAlgoTag;

/**
 * Label provider for BrokerAlgoTags table.
 * 
 * @author Milos Djuric
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
public class AlgoTableObservableMapLabelProvider
        extends ObservableMapLabelProvider
{

	public AlgoTableObservableMapLabelProvider(IObservableMap attributeMap) {
		super(attributeMap);
	}
	
	public AlgoTableObservableMapLabelProvider(IObservableMap[] attributeMaps) {
		super(attributeMaps);
	}
    /* (non-Javadoc)
     * @see org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider#getColumnText(java.lang.Object, int)
     */
    @Override
    public String getColumnText(Object inElement,
                                int inColumnIndex)
    {
        if(inElement instanceof ObservableAlgoTag){
            ObservableAlgoTag observableAlgoTag = (ObservableAlgoTag)inElement;
            switch(inColumnIndex){
                case 0:
                    return observableAlgoTag.getAlgoTag().getLabel();
                case 1:
                    if(observableAlgoTag.getAlgoTag().getTagSpec().getOptions() == null || observableAlgoTag.getAlgoTag().getTagSpec().getOptions().isEmpty()) {
                        return observableAlgoTag.getValueString();
                    }
                    Map<String,String> options = observableAlgoTag.getAlgoTag().getTagSpec().getOptions();
                    for(String key : options.keySet()){
                        if(options.get(key).equals(observableAlgoTag.getValueString()))
                            return key;
                    }
                    return "";
                case 2:
                    return observableAlgoTag.getAlgoTag().getTagSpec().getDescription();
            }
        }
        return null;
    }
}
