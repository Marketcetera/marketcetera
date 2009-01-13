package org.marketcetera.photon.ui.databinding;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class ObservableEventListTest extends TestCase {

	public void testObservableEventList() throws Exception {
		EventList<String> source = new BasicEventList<String>();
		ObservableEventList observableList = new ObservableEventList(source);
		source.add("a");
		source.add("b");
		source.add("C");
		assertEquals(3, observableList.size());
		assertEquals("a", observableList.get(0));
		assertEquals("b", observableList.get(1));
		assertEquals("C", observableList.get(2));
	}
}
