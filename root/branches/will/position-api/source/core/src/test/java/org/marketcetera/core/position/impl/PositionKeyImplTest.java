package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.PositionKeyFactory.createEquityKey;
import static org.marketcetera.core.position.PositionKeyFactory.createOptionKey;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.marketcetera.core.position.Equity;
import org.marketcetera.core.position.Option;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.core.position.PositionKeyTestBase;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.log.ActiveLocale;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link PositionKeyImpl}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class PositionKeyImplTest extends PositionKeyTestBase {

    @Override
    protected PositionKey<?> createFixture() {
        return PositionKeyFactory.createEquityKey("ABC", "MyAccount", "Me");
    }

    @Override
    protected PositionKey<?> createEqualFixture() {
        return PositionKeyFactory.createEquityKey("ABC", "MyAccount", "Me");
    }

    @Override
    protected List<PositionKey<?>> createDifferentFixtures() {
        return ImmutableList.<PositionKey<?>> of(

        createEquityKey("ABC", null, null),

        createEquityKey("ABC", "Account", null),

        createEquityKey("IBM", null, null),

        createOptionKey("ABC", Option.Type.CALL, "20090101", BigDecimal.ONE,
                null, null),

        createOptionKey("ABC", Option.Type.CALL, "20090101", BigDecimal.ONE,
                "Account", null),

        createOptionKey("ABC", Option.Type.PUT, "20090101", BigDecimal.ONE,
                null, null),

        createEquityKey("ABC", null, "Me"),

        createEquityKey("ABC", "Account", "Me"),

        createOptionKey("ABC", Option.Type.CALL, "20090101", BigDecimal.ONE,
                "Account", "Me"));
    }

    @Test
    public void testNullInstrument() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new PositionKeyImpl<Equity>(null, "abc", "abc");
            }
        };
    }

    @Test
    public void testToString() throws Exception {
        ActiveLocale.pushLocale(Locale.ROOT);
        try {
            assertThat(
                    createFixture().toString(),
                    is("PositionKeyImpl[symbol=ABC,account=MyAccount,traderId=Me]"));
        } finally {
            ActiveLocale.popLocale();
        }
    }

    @Test
    public void testJAXBSerialize() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(PositionKeyImpl.class,
                EquityImpl.class, OptionImpl.class);
        Marshaller m = jc.createMarshaller();
        Unmarshaller u = jc.createUnmarshaller();

        for (Object o : ImmutableList.of(

        createEquityKey("ABC", "Account", "Me"),

        createOptionKey("ABC", Option.Type.CALL, "20090101", BigDecimal.ONE,
                "Account", "Me"))

        ) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            m.marshal(o, out);
            Object object = u.unmarshal(new ByteArrayInputStream(out
                    .toByteArray()));
            assertThat(object, is(o));
        }
    }
}
