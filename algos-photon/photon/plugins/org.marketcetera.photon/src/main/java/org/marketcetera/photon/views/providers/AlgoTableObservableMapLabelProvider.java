package org.marketcetera.photon.views.providers;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.marketcetera.algo.BrokerAlgoTag;

public class AlgoTableObservableMapLabelProvider extends LabelProvider
		implements ILabelProvider, ITableLabelProvider {

	private final IObservableMap[] attributeMaps;

	private IMapChangeListener mapChangeListener = new IMapChangeListener() {
		public void handleMapChange(MapChangeEvent event) {
			Set<?> affectedElements = event.diff.getChangedKeys();
			LabelProviderChangedEvent newEvent = new LabelProviderChangedEvent(
					AlgoTableObservableMapLabelProvider.this, affectedElements.toArray());
			fireLabelProviderChanged(newEvent);
		}
	};

	/**
	 * @param attributeMap
	 */
	public AlgoTableObservableMapLabelProvider(IObservableMap attributeMap) {
		this(new IObservableMap[] { attributeMap });
	}

	/**
	 * @param attributeMaps
	 */
	public AlgoTableObservableMapLabelProvider(IObservableMap[] attributeMaps) {
		System.arraycopy(attributeMaps, 0,
				this.attributeMaps = new IObservableMap[attributeMaps.length],
				0, attributeMaps.length);
		for (int i = 0; i < attributeMaps.length; i++) {
			attributeMaps[i].addMapChangeListener(mapChangeListener);
		}
	}

	public void dispose() {
		for (int i = 0; i < attributeMaps.length; i++) {
			attributeMaps[i].removeMapChangeListener(mapChangeListener);
		}
		super.dispose();
	}

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

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
