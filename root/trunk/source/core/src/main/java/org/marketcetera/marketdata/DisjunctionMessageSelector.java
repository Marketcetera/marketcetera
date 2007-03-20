package org.marketcetera.marketdata;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import quickfix.Message;

public class DisjunctionMessageSelector implements IMessageSelector, Iterable<IMessageSelector> {
	List<IMessageSelector> subSelectors = new LinkedList<IMessageSelector>();
	
	public DisjunctionMessageSelector(IMessageSelector ... messageSelectors){
		Arrays.asList(messageSelectors);
	}

	public boolean select(Message aMessage) {
		for (IMessageSelector selector : subSelectors) {
			if (selector.select(aMessage)){
				return true;
			}
		}
		return false;
	}

	public Iterator<IMessageSelector> iterator() {
		return subSelectors.iterator();
	}
}
