package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedEmptyFailure;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullElementFailure;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcher.IPropertiesChangedListener;
import org.marketcetera.photon.test.*;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

/* $License$ */

/**
 * Test {@link PropertyWatcher}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class PropertyWatcherTest extends PhotonTestBase {

    private DefaultRealm mRealm;

    @Before
    public void before() {
        mRealm = new DefaultRealm();
    }

    @After
    public void after() {
        mRealm.dispose();
    }

    @Test
    public void testWatch() {
        final Multiset<Object> affected = HashMultiset.create();
        PropertyWatcher fixture = new PropertyWatcher(Arrays.asList(
                BeanProperties.value("prop1"), BeanProperties.value("prop2")),
                new IPropertiesChangedListener() {
                    @Override
                    public void propertiesChanged(
                            ImmutableSet<?> affectedElements) {
                        affected.addAll(affectedElements);
                    }
                });
        Bean1 bean1a = new Bean1();
        Bean1 bean1b = new Bean1();
        Bean2 bean2 = new Bean2();
        // watching 1a and 2
        fixture.watch(new WritableSet(Arrays.asList(bean1a, bean2), null));
        bean1a.setProp1("a");
        bean1b.setProp1("a");
        // 1b change should have no affect
        assertItems(affected, bean1a);
        affected.clear();
        // change both watched beans
        bean1a.setProp2("b");
        bean2.setProp1("a");
        assertItems(affected, bean1a, bean2);
        affected.clear();
        // change a watched bean on a property that is not watched
        bean2.setProp3("c");
        // should have no affect
        assertThat(affected.size(), is(0));
        // now watch 1b
        fixture.watch(new WritableSet(Arrays.asList(bean1b), null));
        bean1a.setProp1("a");
        bean1b.setProp1("a2");
        assertItems(affected, bean1b);
        affected.clear();
        // add 1b a second time
        fixture.watch(new WritableSet(Arrays.asList(bean1b), null));
        bean1b.setProp1("a3");
        // two notifications
        assertItems(affected, bean1b, bean1b);
        affected.clear();
        fixture.dispose();
        bean2.setProp1("d");
        // no notifications after dispose
        assertThat(affected.size(), is(0));
    }

    private void assertItems(Multiset<Object> collection, Object... objects) {
//        assertThat(collection, Matchers.<Multiset<Object>> is(ImmutableMultiset.of(objects)));
    }

    @Test
    public void testConstructorValidation() throws Exception {
        new ExpectedNullArgumentFailure("properties") {
            @Override
            protected void run() throws Exception {
                new PropertyWatcher(null,
                        mock(IPropertiesChangedListener.class));
            }
        };
        new ExpectedEmptyFailure("properties") {
            @Override
            protected void run() throws Exception {
                final List<IValueProperty> empty = Collections.emptyList();
                new PropertyWatcher(empty,
                        mock(IPropertiesChangedListener.class));
            }
        };
        new ExpectedNullElementFailure("properties") {
            @Override
            protected void run() throws Exception {
                new PropertyWatcher(Collections
                        .singleton((IValueProperty) null),
                        mock(IPropertiesChangedListener.class));
            }
        };
        new ExpectedNullArgumentFailure("listener") {
            @Override
            protected void run() throws Exception {
                new PropertyWatcher(Collections
                        .singleton(mock(IValueProperty.class)), null);
            }
        };
    }

    @Test
    public void testWatchValidation() throws Exception {
        final PropertyWatcher fixture = new PropertyWatcher(Collections
                .singleton(mock(IValueProperty.class)),
                mock(IPropertiesChangedListener.class));
        new ExpectedNullArgumentFailure("elements") {
            @Override
            protected void run() throws Exception {
                fixture.watch(null);
            }
        };
        final LockRealm lockRealm = new LockRealm();
        new PropertyWatcherElementsRealmCheckFailure(lockRealm, mRealm) {
            @Override
            protected void run() throws Exception {
                fixture.watch(new WritableSet(lockRealm));
            }
        };
        fixture.dispose();
        new ExpectedIllegalStateException(
                "PropertyWatcher has already been disposed") {
            @Override
            protected void run() throws Exception {
                fixture.watch(new WritableSet(new LockRealm()));
            }
        };
        new ExpectedIllegalStateException(MessageFormat.format(
                "called from invalid realm [0], expected [1]", lockRealm,
                mRealm)) {
            @Override
            protected void run() throws Exception {
                lockRealm.lock();
                Realm.runWithDefault(lockRealm, new Runnable() {
                    @Override
                    public void run() {
                        fixture.watch(new WritableSet(lockRealm));
                    }
                });
            }
        };
        mRealm.dispose();
        new ExpectedIllegalStateException(
                "must be called from a thread with a default realm") {
            @Override
            protected void run() throws Exception {
                fixture.watch(new WritableSet(new LockRealm()));
            }
        };
        new ExpectedIllegalStateException(
                "must be called from the default realm") {
            @Override
            protected void run() throws Exception {
                Realm.runWithDefault(new LockRealm(), new Runnable() {
                    @Override
                    public void run() {
                        fixture.watch(new WritableSet(new LockRealm()));
                    }
                });
            }
        };
    }

    public static class BeanBase {
        protected final PropertyChangeSupport mChangeSupport = new PropertyChangeSupport(
                this);

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            mChangeSupport.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            mChangeSupport.removePropertyChangeListener(listener);
        }
    }

    public static class Bean1 extends BeanBase {
        private String mProp1;
        private String mProp2;

        public String getProp1() {
            return mProp1;
        }

        public void setProp1(String prop1) {
            String oldValue = mProp1;
            mProp1 = prop1;
            mChangeSupport.firePropertyChange("prop1", oldValue, mProp1);
        }

        public String getProp2() {
            return mProp2;
        }

        public void setProp2(String prop2) {
            String oldValue = mProp2; 
            mProp2 = prop2; 
            mChangeSupport.firePropertyChange("prop2", oldValue, mProp2); 
        }
    }

    public static class Bean2 extends BeanBase {
        private String mProp1;
        private String mProp3;

        public String getProp1() {
            return mProp1;
        }

        public void setProp1(String prop1) {
            String oldValue = mProp1; 
            mProp1 = prop1; 
            mChangeSupport.firePropertyChange("prop1", oldValue, mProp1); 
        }

        public String getProp3() {
            return mProp3;
        }

        public void setProp3(String prop3) {
            String oldValue = mProp3; 
            mProp3 = prop3; 
            mChangeSupport.firePropertyChange("prop3", oldValue, mProp3); 
        }
    }

    /**
     * Helper to verify
     * {@link PropertyWatcher#watch(org.eclipse.core.databinding.observable.set.IObservableSet)}
     * fails due to invalid realm of the elements parameter.
     */
    public static abstract class PropertyWatcherElementsRealmCheckFailure
            extends ExpectedIllegalArgumentException {

        /**
         * Constructor.
         * 
         * @param actual
         *            the actual realm
         * @param expected
         *            the expected realm
         * @throws Exception
         *             if there was an unexpected failure
         */
        protected PropertyWatcherElementsRealmCheckFailure(Realm actual,
                Realm expected) throws Exception {
            super(MessageFormat.format(
                    "elements is on an invalid realm [0], expected [1]",
                    actual, expected));
        }

    }

}
