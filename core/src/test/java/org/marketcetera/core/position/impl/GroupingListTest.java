package org.marketcetera.core.position.impl;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcher;
import org.marketcetera.core.position.impl.GroupingList.GroupMatcherFactory;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TransactionList;
import ca.odell.glazedlists.event.ListEvent;

/* $License$ */

/**
 * Test {@link GroupingList}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class GroupingListTest {
    GroupMatcherFactory<String, GroupMatcher<String>> factory = new GroupMatcherFactory<String, GroupMatcher<String>>() {

        @Override
        public GroupMatcher<String> createGroupMatcher(final String element) {
            class MyMatcher implements GroupMatcher<String> {
                String key;

                MyMatcher(String key) {
                    this.key = key;
                }

                @Override
                public boolean matches(String item) {
                    return key.equals(item);
                }

                @Override
                public int compareTo(GroupMatcher<String> o) {
                    MyMatcher my = (MyMatcher) o;
                    return key.compareTo(my.key);
                }
            }
            ;
            return new MyMatcher(element);
        }
    };

    abstract class TestTemplate implements Runnable {
        @Override
        public void run() {
            EventList<String> base = GlazedLists.eventListOf();
            TransactionList<String> trans = new TransactionList<String>(base);
            initList(trans);

            GroupingList<String> groupingList = new GroupingList<String>(trans, factory);
            List<ExpectedListChanges<?>> listeners = new LinkedList<ExpectedListChanges<?>>();
            for (int i = 0; i < groupingList.size(); i++) {
                ExpectedListChanges<String> listChangeListener = new ExpectedListChanges<String>(
                        "Group " + Integer.toString(i), getGroupsExpected(i));
                listeners.add(listChangeListener);
                groupingList.get(i).addListEventListener(listChangeListener);
            }

            ExpectedListChanges<EventList<String>> listChangeListener = new ExpectedListChanges<EventList<String>>(
                    "Root List", getExpected());
            listeners.add(listChangeListener);
            groupingList.addListEventListener(listChangeListener);
            modifyBaseList(base);
            trans.beginEvent();
            modifyInTransaction(trans);
            trans.commitEvent();
            for (ExpectedListChanges<?> i : listeners) {
                i.exhausted();
            }
        }

        protected void modifyBaseList(EventList<String> base) {
        }

        protected void modifyInTransaction(EventList<String> list) {
        }

        protected abstract void initList(EventList<String> list);

        protected int[] getExpected() {
            return new int[] {};
        }

        protected int[] getGroupsExpected(int i) {
            return new int[] {};
        }

    }

    @Test
    public void AB_iAAB() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("B");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(0, "A");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 0 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 0) {
                    return new int[] { ListEvent.INSERT, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void AB_AiAB() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("B");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(1, "A");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 0 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 0) {
                    return new int[] { ListEvent.INSERT, 1 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void BA_ABA() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("B");
                list.add("A");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(0, "A");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 0 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 0) {
                    return new int[] { ListEvent.INSERT, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void AB_ABiB() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("B");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(2, "B");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 1 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 1) {
                    return new int[] { ListEvent.INSERT, 1 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void ABC_AiBBC() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("B");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(1, "B");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 1 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 1) {
                    return new int[] { ListEvent.INSERT, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void ABC_ABiBC() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("B");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(2, "B");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 1 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 1) {
                    return new int[] { ListEvent.INSERT, 1 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void B_AB() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("B");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(0, "A");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.INSERT, 0 };
            }
        }.run();

    }

    @Test
    public void AC_ABC() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(1, "B");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.INSERT, 1 };
            }
        }.run();

    }

    @Test
    public void AC_EBAC() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(0, "B");
                list.add(0, "E");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.INSERT, 1, ListEvent.INSERT, 3 };
            }
        }.run();

    }

    @Test
    public void AC_CEBABC() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add(0, "B");
                list.add(0, "C");
                list.add(3, "B");
                list.add(1, "E");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.INSERT, 1, ListEvent.UPDATE, 2, ListEvent.INSERT, 3 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 1) {
                    return new int[] { ListEvent.INSERT, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void AC_CEBABC_no_transaction() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("C");
            }

            @Override
            protected void modifyBaseList(EventList<String> list) {
                list.add(0, "B");
                list.add(0, "C");
                list.add(3, "B");
                list.add(1, "E");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.INSERT, 1, ListEvent.UPDATE, 2, ListEvent.UPDATE, 1,
                        ListEvent.INSERT, 3 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 1) {
                    return new int[] { ListEvent.INSERT, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void AC_AuC() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.set(1, "C");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 1 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 1) {
                    return new int[] { ListEvent.UPDATE, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void AC_AuB() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.set(1, "B");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.INSERT, 1, ListEvent.DELETE, 2 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 1) {
                    return new int[] { ListEvent.DELETE, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void ABC_ABuB() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("B");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.set(2, "B");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 1, ListEvent.DELETE, 2 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 1) {
                    return new int[] { ListEvent.INSERT, 1 };
                }
                if (i == 2) {
                    return new int[] { ListEvent.DELETE, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void A_dAiA() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.remove(0);
                list.add(0, "A");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 0 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 0) {
                    return new int[] { ListEvent.DELETE, 0, ListEvent.INSERT, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void ABC_AdBiBiCC() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("B");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.remove(1);
                list.add(1, "C");
                list.add(1, "B");
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.UPDATE, 1, ListEvent.UPDATE, 2 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 1) {
                    return new int[] { ListEvent.INSERT, 0, ListEvent.DELETE, 1 };
                }
                if (i == 2) {
                    return new int[] { ListEvent.INSERT, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void clear() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("B");
                list.add("C");
            }

            @Override
            protected void modifyInTransaction(EventList<String> list) {
                list.add("D");
                list.add("E");
                list.clear();
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.DELETE, 0, ListEvent.DELETE, 0, ListEvent.DELETE, 0 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 0) {
                    return new int[] { ListEvent.DELETE, 0 };
                }
                if (i == 1) {
                    return new int[] { ListEvent.DELETE, 0 };
                }
                if (i == 2) {
                    return new int[] { ListEvent.DELETE, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }

    @Test
    public void clear_no_transaction() {
        new TestTemplate() {

            @Override
            protected void initList(EventList<String> list) {
                list.add("A");
                list.add("B");
                list.add("C");
            }

            @Override
            protected void modifyBaseList(EventList<String> list) {
                list.add("D");
                list.add("E");
                list.clear();
            }

            @Override
            protected int[] getExpected() {
                return new int[] { ListEvent.INSERT, 3, ListEvent.INSERT, 4, ListEvent.DELETE, 0,
                        ListEvent.DELETE, 0, ListEvent.DELETE, 0, ListEvent.DELETE, 0,
                        ListEvent.DELETE, 0 };
            }

            @Override
            protected int[] getGroupsExpected(int i) {
                if (i == 0) {
                    return new int[] { ListEvent.DELETE, 0 };
                }
                if (i == 1) {
                    return new int[] { ListEvent.DELETE, 0 };
                }
                if (i == 2) {
                    return new int[] { ListEvent.DELETE, 0 };
                }
                return super.getGroupsExpected(i);
            }
        }.run();

    }
}
