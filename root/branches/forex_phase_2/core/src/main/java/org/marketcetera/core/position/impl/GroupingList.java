package org.marketcetera.core.position.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.AbstractEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventAssembler;
import ca.odell.glazedlists.impl.adt.Barcode;
import ca.odell.glazedlists.impl.adt.barcode2.Element;
import ca.odell.glazedlists.impl.adt.barcode2.SimpleTree;
import ca.odell.glazedlists.impl.adt.barcode2.SimpleTreeIterator;
import ca.odell.glazedlists.matchers.Matcher;

/* $License$ */

/**
 * Replacement for {@link ca.odell.glazedlists.GroupingList} that provides event notifications for
 * the groups.
 * <p>
 * A grouping list is initialized with a {@link GroupMatcherFactory}, which it uses to create a
 * unique {@link GroupMatcher} to define a group. The groups are ordered by the natural ordering of
 * the GroupMatchers.
 * <p>
 * Each element in the grouping list is itself an event list. These children group lists preserve
 * the ordering of their respective elements in the source list.
 * <p>
 * This class depends on internal classes in the glazed lists library so changing/upgrading the
 * library should be done with care.
 * <p>
 * See <a href="http://www.nabble.com/GroupList-notification-td21879305.html">http://www.nabble.com/
 * GroupList-notification-td21879305.html</a>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class GroupingList<E> extends TransformedList<E, EventList<E>> {

    /**
     * The GroupLists that make up this GroupingList in sorted order (sorted by their
     * GroupMatchers).
     */
    private SimpleTree<GroupList> groupLists = new SimpleTree<GroupList>(GlazedLists
            .comparableComparator());

    private GroupMatcherFactory<E, GroupMatcher<E>> factory;

    /**
     * Constructor.
     * 
     * @param source
     *            the source list
     * @param factory
     *            factory to create group matchers
     */
    public GroupingList(EventList<E> source, GroupMatcherFactory<E, GroupMatcher<E>> factory) {
        super(source);
        this.factory = factory;
        initTree();
        source.addListEventListener(this);
    }

    private void initTree() {
        // create groups for source elements and build tree
        int index = 0;
        for (Iterator<E> i = source.iterator(); i.hasNext(); index++) {
            processInsert(index, i.next(), false);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void listChanged(ListEvent<E> listChanges) {
        beginEvent();
        if (listChanges.isReordering()) {
            throw new UnsupportedOperationException();
        }
        while (listChanges.next()) {
            final int changeIndex = listChanges.getIndex();
            final int changeType = listChanges.getType();
            if (changeType == ListEvent.INSERT) {
                E inserted = source.get(changeIndex);
                processInsert(changeIndex, inserted, true);
            } else if (changeType == ListEvent.UPDATE) {
                E updated = source.get(changeIndex);
                E oldValue = listChanges.getOldValue();
                GroupMatcher<E> oldMatcher = factory.createGroupMatcher(oldValue);
                if (oldMatcher.matches(updated)) {
                    // same group
                    GroupList oldGroup = new GroupList(oldMatcher);
                    int oldIndex = groupLists.indexOfValue(oldGroup, true, false, (byte) 1);
                    GroupList oldList = groupLists.get(oldIndex).get();
                    oldList.getListEventAssembler().elementUpdated(
                            oldList.barcode.getBlackIndex(changeIndex), oldValue, updated);
                    updates.elementUpdated(oldIndex, oldList, oldList);
                } else {
                    // new group, treat as a delete and insert
                    processDelete(changeIndex, oldValue);
                    processInsert(changeIndex, updated, true);
                }
            } else if (changeType == ListEvent.DELETE) {
                processDelete(changeIndex, listChanges.getOldValue());
            }
        }
        commitEventAndDelete();
    }

    private void beginEvent() {
        updates.beginEvent();
        int index = 0;
        for (SimpleTreeIterator<GroupList> i = new SimpleTreeIterator<GroupList>(groupLists); i
                .hasNext(); index++) {
            i.next();
            GroupList group = i.value();
            group.getListEventAssembler().beginEvent();
        }
    }

    private void processInsert(final int changeIndex, E inserted, boolean notify) {
        // This is a bit clunky - to find the group for an element, I am
        // creating a new GroupList and looking for it in the tree. GroupLists
        // are considered equal if their matchers compare equal.
        GroupMatcher<E> matcher = factory.createGroupMatcher(inserted);
        GroupList newGroup = new GroupList(matcher);
        int groupIndex = groupLists.indexOfValue(newGroup, true, false, (byte) 1);
        if (groupIndex >= 0) {
            insertHelper(changeIndex, groupIndex);
            if (notify) {
                GroupList groupingList = groupLists.get(groupIndex).get();
                // an insert on the existing list
                groupingList.getListEventAssembler().elementInserted(
                        groupingList.barcode.getBlackIndex(changeIndex), inserted);
                // an update on the grouping list
                updates.elementUpdated(groupIndex, groupingList, groupingList);
            }
        } else {
            Element<GroupList> newList = groupLists.addInSortedOrder((byte) 1, newGroup, 1);
            groupIndex = groupLists.indexOfNode(newList, (byte) 1);
            insertHelper(changeIndex, groupIndex);
            if (notify) {
                // begin event so later inserts, updates, deletes will work
                newGroup.getListEventAssembler().beginEvent();
                // an update on the grouping list
                updates.elementInserted(groupIndex, newGroup);
            }
        }
    }

    /**
     * Update GroupLists barcodes to reflect an insert.
     * 
     * @param insertIndex
     *            the source index of the insert
     * @param groupIndex
     *            the index of the matching group for the new element
     */
    private void insertHelper(int insertIndex, int groupIndex) {
        int index = 0;
        for (SimpleTreeIterator<GroupList> i = new SimpleTreeIterator<GroupList>(groupLists); i
                .hasNext(); index++) {
            i.next();
            GroupList group = i.value();
            if (index == groupIndex) {
                group.barcode.addBlack(insertIndex, 1);
            } else {
                group.barcode.addWhite(insertIndex, 1);
            }
        }
    }

    private void processDelete(int changeIndex, E oldValue) {
        int index = 0;
        for (SimpleTreeIterator<GroupList> i = new SimpleTreeIterator<GroupList>(groupLists); i
                .hasNext(); index++) {
            i.next();
            GroupList group = i.value();
            int blackIndex = group.barcode.getBlackIndex(changeIndex);
            if (blackIndex != -1) {
                // this will happen on one of the groups (the matching group)
                group.getListEventAssembler().elementDeleted(blackIndex, oldValue);
            }
            group.barcode.remove(changeIndex, 1);
        }
    }

    private void commitEventAndDelete() {
        int index = 0;
        // parallel lists of index-group pairs for delete notification
        List<Integer> toRemove = new LinkedList<Integer>();
        List<GroupList> toRemoveGroups = new LinkedList<GroupList>();
        for (SimpleTreeIterator<GroupList> i = new SimpleTreeIterator<GroupList>(groupLists); i
                .hasNext(); index++) {
            i.next();
            GroupList group = i.value();
            ListEventAssembler<E> listEventAssembler = group.getListEventAssembler();
            if (listEventAssembler.isEventEmpty()) {
                listEventAssembler.discardEvent();
            } else {
                listEventAssembler.commitEvent();
            }
            if (group.barcode.blackSize() == 0) {
                // don't remove now since we are iterating,
                // but mark for later
                toRemove.add(index--);
                toRemoveGroups.add(group);
            }
        }
        Iterator<Integer> i = toRemove.iterator();
        Iterator<GroupList> j = toRemoveGroups.iterator();
        while (i.hasNext()) {
            Integer remIndex = i.next();
            groupLists.remove(remIndex, 1);
            updates.elementDeleted(remIndex, j.next());
        }
        updates.commitEvent();
    }

    @Override
    public EventList<E> get(int index) {
        return groupLists.get(index).get();
    }

    @Override
    protected boolean isWritable() {
        return false;
    }

    @Override
    protected int getSourceIndex(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(EventList<E> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, EventList<E> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends EventList<E>> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends EventList<E>> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventList<E> remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object toRemove) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventList<E> set(int index, EventList<E> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return groupLists.size();
    }

    /**
     * EventList implementation used for groups.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ClassVersion("$Id$")
    private class GroupList extends AbstractEventList<E> implements Comparable<GroupList> {

        private GroupMatcher<E> matcher;

        private Barcode barcode = new Barcode();

        private ListEventAssembler<E> getListEventAssembler() {
            return updates;
        }

        public GroupList(GroupMatcher<E> matcher) {
            this.readWriteLock = GroupingList.this.readWriteLock;
            this.matcher = matcher;
        }

        @Override
        public E get(int index) {
            return source.get(barcode.getIndex(index, Barcode.BLACK));
        }

        @Override
        public int size() {
            return barcode.blackSize();
        }

        @Override
        public void dispose() {
        }

        @Override
        public int compareTo(GroupList o) {
            return matcher.compareTo(o.matcher);
        }

    }

    /**
     * A matcher that determines if an element should be in a group. One of these GroupMatchers
     * defines a group in an immutable way. It is comparable to impose an ordering on the groups.
     */
    public interface GroupMatcher<T> extends Matcher<T>, Comparable<GroupMatcher<T>> {
    }

    /**
     * Creates GroupMatchers for elements in the GroupingList. This factory must create identical
     * matchers for elements that belong in the same group. Essentially, this factory is responsible
     * for extracting the immutable grouping criteria from an element.
     */
    public interface GroupMatcherFactory<S, T extends GroupMatcher<S>> {
        T createGroupMatcher(S element);
    }
}
