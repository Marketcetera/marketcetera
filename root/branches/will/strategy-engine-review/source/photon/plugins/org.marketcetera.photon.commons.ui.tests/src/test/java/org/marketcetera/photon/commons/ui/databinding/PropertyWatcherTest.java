package org.marketcetera.photon.commons.ui.databinding;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.commons.ui.databinding.PropertyWatcher.IPropertiesChangedListener;
import org.marketcetera.photon.test.DefaultRealm;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Test {@link PropertyWatcher}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class PropertyWatcherTest {

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
        final List<Object> affected = Lists.newArrayList();
        PropertyWatcher fixture = new PropertyWatcher(Arrays
                .<IValueProperty> asList(BeanProperties.value("prop1"),
                        BeanProperties.value("prop2")),
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
        fixture.watch(new WritableSet(Arrays.asList(bean1a, bean2), null));

        bean1a.setProp1("a");
        bean1b.setProp1("a");
        assertItems(affected, bean1a);
        affected.clear();
        bean1a.setProp2("b");
        bean2.setProp1("a");
        assertItems(affected, bean1a, bean2);
        affected.clear();
        bean2.setProp3("c");
        assertThat(affected.size(), is(0));
        final WritableSet temp = new WritableSet(Arrays.asList(bean1b), null);
        fixture.watch(temp);
        bean1a.setProp1("a");
        bean1b.setProp1("a2");
        assertItems(affected, bean1b);
        affected.clear();
        fixture.dispose();
        bean2.setProp1("d");
        assertThat(affected.size(), is(0));
    }

    private void assertItems(Collection<Object> collection, Object... objects) {
        assertThat(collection.size(), is(objects.length));
        assertThat(collection, hasItems(objects));
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
            mChangeSupport.firePropertyChange("prop1", mProp1, mProp1 = prop1);
        }

        public String getProp2() {
            return mProp2;
        }

        public void setProp2(String prop2) {
            mChangeSupport.firePropertyChange("prop2", mProp2, mProp2 = prop2);
        }
    }

    public static class Bean2 extends BeanBase {
        private String mProp1;
        private String mProp3;

        public String getProp1() {
            return mProp1;
        }

        public void setProp1(String prop1) {
            mChangeSupport.firePropertyChange("prop1", mProp1, mProp1 = prop1);
        }

        public String getProp3() {
            return mProp3;
        }

        public void setProp3(String prop3) {
            mChangeSupport.firePropertyChange("prop3", mProp3, mProp3 = prop3);
        }
    }

}
