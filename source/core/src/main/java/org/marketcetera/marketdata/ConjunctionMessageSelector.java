package org.marketcetera.marketdata;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import quickfix.Message;

public class ConjunctionMessageSelector implements IMessageSelector, Iterable<IMessageSelector> {
	List<IMessageSelector> subSelectors ;
	
	public ConjunctionMessageSelector(IMessageSelector ... messageSelectors){
		List<IMessageSelector> tmpList = Arrays.asList(messageSelectors);
		subSelectors = new LinkedList<IMessageSelector>(tmpList);
	}

	public boolean select(Message aMessage) {
		for (IMessageSelector selector : subSelectors) {
			if (!selector.select(aMessage)){
				return false;
			}
		}
		return true;
	}

	public Iterator<IMessageSelector> iterator() {
		return subSelectors.iterator();
	}
	
	
}
