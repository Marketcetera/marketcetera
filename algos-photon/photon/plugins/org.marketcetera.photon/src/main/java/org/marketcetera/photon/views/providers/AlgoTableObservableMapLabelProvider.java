package org.marketcetera.photon.views.providers;

import java.util.Map;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.marketcetera.algo.BrokerAlgoTag;

/**
 * Label provider for BrokerAlgoTags table.
 * @author Milos Djuric
 *
 */
public class AlgoTableObservableMapLabelProvider extends ObservableMapLabelProvider{

	public AlgoTableObservableMapLabelProvider(IObservableMap attributeMap) {
		super(attributeMap);
	}
	
	public AlgoTableObservableMapLabelProvider(IObservableMap[] attributeMaps) {
		super(attributeMaps);
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element instanceof BrokerAlgoTag){
			BrokerAlgoTag brokerAlgoTag = (BrokerAlgoTag)element;
			switch(columnIndex){
				case 0:
					return brokerAlgoTag.getLabel();
				case 1:
					if(brokerAlgoTag.getTagSpec().getOptions() == null)
						return brokerAlgoTag.getValue();
					Map<String, String> options = brokerAlgoTag.getTagSpec().getOptions();
					for(String key : options.keySet()){
						if(options.get(key).equals(brokerAlgoTag.getValue()))
							return key;
					}
					return "";
					
			}
		}
		return null;
	}

}
