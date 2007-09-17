package org.marketcetera.photon.model;

import java.util.TreeSet;

import junit.framework.TestCase;

import org.marketcetera.photon.core.ClOrdIDComparator;
import org.marketcetera.photon.core.MessageHolder;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GroupingList;
import ca.odell.glazedlists.SortedList;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.fix42.Heartbeat;

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
		assertEquals(0, comparator.compare(mh1, mh1));
		assertEquals(0, comparator.compare(mh2, mh2));

		m2.removeField(ClOrdID.FIELD);
		
		assertEquals(0, comparator.compare(mh1, mh2));
		assertEquals(0, comparator.compare(mh2, mh1));
		assertEquals(0, comparator.compare(mh1, mh1));
		assertEquals(0, comparator.compare(mh2, mh2));
	}
	
	public void testMissingField() throws Exception {
		Message m1 = new Message();
		Message m2 = new Message();
		MessageHolder mh1 = new MessageHolder(m1);
		MessageHolder mh2 = new MessageHolder(m2);
		
		m1.setField(new ClOrdID("asdf"));
		TreeSet<MessageHolder> sortedSet = new TreeSet<MessageHolder>(comparator);
		sortedSet.add(mh1);
		sortedSet.add(mh2);
		assertSame(mh1, sortedSet.last());
	}

	public void testCorrelateOrigClOrdID() throws Exception {
		Message m1 = new Message();
		Message m2 = new Message();
		MessageHolder mh1 = new MessageHolder(m1);
		MessageHolder mh2 = new MessageHolder(m2);
		
		m1.setField(new ClOrdID("asdf"));
		m2.setField(new ClOrdID("qwer"));
		m2.setField(new OrigClOrdID("asdf"));
		
		ClOrdIDComparator statefulComparator = new ClOrdIDComparator();
		statefulComparator.addIDMap("asdf", "qwer");
		assertEquals(0, statefulComparator.compare(mh1, mh2));
		assertEquals(0, statefulComparator.compare(mh2, mh1));
		assertEquals(0, statefulComparator.compare(mh1, mh1));
		assertEquals(0, statefulComparator.compare(mh2, mh2));
	}

	public void testWithGroupingList() throws Exception {
		ClOrdIDComparator statefulComparator = new ClOrdIDComparator();

		EventList<MessageHolder> sourceList  = new BasicEventList<MessageHolder>();
		GroupingList<MessageHolder> groups = new GroupingList<MessageHolder>(sourceList, statefulComparator);
		SortedList<MessageHolder> sorted = new SortedList<MessageHolder>(sourceList, statefulComparator);

		statefulComparator.addIDMap("asdf", "qwer");

		Message m1 = new Message();
		Message m2 = new Message();
		MessageHolder mh1 = new MessageHolder(m1);
		MessageHolder mh2 = new MessageHolder(m2);
		
		m1.setField(new ClOrdID("asdf"));
		m2.setField(new ClOrdID("qwer"));
		m2.setField(new OrigClOrdID("asdf"));

		MessageHolder heartbeatHolder = new MessageHolder(new Heartbeat());

		sourceList.add(mh1);
		sourceList.add(heartbeatHolder);
		sourceList.add(mh2);
		
		assertSame(heartbeatHolder, sorted.get(0));
		assertSame(heartbeatHolder, sourceList.get(1));
		
		assertEquals(2, groups.size());
	}
}
