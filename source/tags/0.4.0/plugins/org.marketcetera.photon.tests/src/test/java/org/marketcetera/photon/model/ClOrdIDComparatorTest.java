package org.marketcetera.photon.model;

import junit.framework.TestCase;

import org.marketcetera.photon.core.ClOrdIDComparator;
import org.marketcetera.photon.core.MessageHolder;

import quickfix.Message;
import quickfix.field.ClOrdID;

public class ClOrdIDComparatorTest extends TestCase {

	private ClOrdIDComparator comparator;

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		comparator = new ClOrdIDComparator();
	}


	public void testCompare() {
		Message m1 = new Message();
		Message m2 = new Message();
		MessageHolder mh1 = new MessageHolder(m1);
		MessageHolder mh2 = new MessageHolder(m2);
		
		m1.setField(new ClOrdID("asdf"));
		m2.setField(new ClOrdID("qwer"));

		assertTrue(comparator.compare(mh1, mh2) < 0);
		assertTrue(comparator.compare(mh2, mh1) > 0);
		assertTrue(comparator.compare(mh1, mh1) == 0);
		assertTrue(comparator.compare(mh2, mh2) == 0);

		m1.removeField(ClOrdID.FIELD);

		
		assertTrue(""+comparator.compare(mh1, mh2), comparator.compare(mh1, mh2) < 0);
		assertTrue(comparator.compare(mh2, mh1) > 0);
		assertTrue(comparator.compare(mh1, mh1) == 0);
		assertTrue(comparator.compare(mh2, mh2) == 0);

		m2.removeField(ClOrdID.FIELD);
		
		assertTrue(comparator.compare(mh1, mh2) == 0);
		assertTrue(comparator.compare(mh2, mh1) == 0);
		assertTrue(comparator.compare(mh1, mh1) == 0);
		assertTrue(comparator.compare(mh2, mh2) == 0);
	}

}
