package org.marketcetera.util.ws.wrappers;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class RemoteI18NBoundMessageTest
    extends WrapperTestBase
{
    private static final String EXPECTED_MESSAGE=
        "Bound message text is '"+TEST_MESSAGE+"'";
    private static final String EXPECTED_MESSAGE_FR=
        "Bound message text in French is '"+TEST_MESSAGE+"'";

    private static final String EXPECTED_NONSER_MESSAGE=
        "Bound message text is 'I am 1'";
    private static final String EXPECTED_NONSER_MESSAGE_FR=
        "Bound message text in French is 'I am 1'";

    private static final String EXPECTED_NONDESER_MESSAGE=
        "provider 'nonexistent_prv'; id 'any'; entry 'msg'; parameters ()";


    private void singleBase
        (RemoteI18NBoundMessage m,
         SerWrapper<I18NBoundMessage> wrapper,
         boolean wrapperSerFailure,
         boolean wrapperDeSerFailure,
         String string,
         String text,
         String textFr)
    {
        assertEquals(wrapper,m.getWrapper());
        if (wrapperSerFailure) {
            assertSerWrapperSerFailure(m.getWrapper());
        } else if (m.getWrapper()!=null) {
            assertNull(m.getWrapper().getSerializationException());
        }
        if (wrapperDeSerFailure) {
            assertSerWrapperDeSerFailure(m.getWrapper());
        } else if (m.getWrapper()!=null) {
            assertNull(m.getWrapper().getDeserializationException());
        }

        assertEquals(string,m.getString());
        assertEquals(text,m.getText());
        assertEquals(text,m.toString());

        ActiveLocale.setProcessLocale(Locale.FRENCH);
        assertEquals(textFr,m.getText());
        assertEquals(textFr,m.toString());
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }

    private void singleNonSerializable
        (RemoteI18NBoundMessage server,
         RemoteI18NBoundMessage client)
    {
        singleBase(client,
                   server.getWrapper(),
                   false,
                   false,
                   server.getString(),
                   server.getText(),
                   server.getText());
    }

    private void singleNonDeserializable
        (RemoteI18NBoundMessage server,
         RemoteI18NBoundMessage client)
    {
        singleBase(client,
                   new SerWrapper<I18NBoundMessage>(),
                   false,
                   true,
                   server.getString(),
                   server.getText(),
                   server.getText());
    }

    private void single
        (RemoteI18NBoundMessage server,
         SerWrapper<I18NBoundMessage> wrapper,
         String string,
         String text,
         String textFr)
        throws Exception
    {
        singleBase
            (server,wrapper,
             false,false,string,text,textFr);
        singleBase
            (assertRoundTripJAXB(server),wrapper,
             false,false,string,text,textFr);
        singleBase
            (assertRoundTripJava(server),wrapper,
             false,false,string,text,textFr);
    }


    @Test
    public void basics()
        throws Exception
    {
        assertEquality
            (new RemoteI18NBoundMessage(null),
             new RemoteI18NBoundMessage(null),
             new RemoteI18NBoundMessage(TEST_I18N_MESSAGE));
        single(new RemoteI18NBoundMessage(null),
               null,
               null,
               null,
               null);

        assertEquality
            (new RemoteI18NBoundMessage(TEST_I18N_MESSAGE),
             new RemoteI18NBoundMessage(TEST_I18N_MESSAGE),
             new RemoteI18NBoundMessage(null),
             new RemoteI18NBoundMessage
             (new I18NBoundMessage1P(TestMessages.BOUND,TEST_MESSAGE+"d")));
        single(new RemoteI18NBoundMessage(TEST_I18N_MESSAGE),
               new SerWrapper<I18NBoundMessage>(TEST_I18N_MESSAGE),
               EXPECTED_MESSAGE,
               EXPECTED_MESSAGE,
               EXPECTED_MESSAGE_FR);
    }

    @Test
    public void setters()
    {
        RemoteI18NBoundMessage m=new RemoteI18NBoundMessage(TEST_I18N_MESSAGE);

        SerWrapper<I18NBoundMessage> wrapper=new SerWrapper<I18NBoundMessage>();
        m.setWrapper(wrapper);
        assertEquals(wrapper,m.getWrapper());

        m.setString(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE,m.getString());

        m.setWrapper(null);
        assertNull(m.getWrapper());

        m.setString(null);
        assertNull(m.getString());
    }

    @Test
    public void nonSerializableThrowable()
        throws Exception
    {
        prepareSerWrapperFailure();
        RemoteI18NBoundMessage server=
            new RemoteI18NBoundMessage(TEST_NONSER_MESSAGE);
        assertEquality(server,
                       new RemoteI18NBoundMessage(TEST_NONSER_MESSAGE),
                       new RemoteI18NBoundMessage(null),
                       new RemoteI18NBoundMessage(TEST_I18N_MESSAGE));
        singleBase(server,
                   new SerWrapper<I18NBoundMessage>(),
                   true,
                   false,
                   EXPECTED_NONSER_MESSAGE,
                   EXPECTED_NONSER_MESSAGE,
                   EXPECTED_NONSER_MESSAGE_FR);

        singleNonSerializable(server,assertRoundTripJAXB(server));
        singleNonSerializable(server,assertRoundTripJava(server));
    }

    @Test
    public void nonDeserializableThrowable()
        throws Exception
    {
        RemoteI18NBoundMessage server=
            new RemoteI18NBoundMessage(TEST_NONDESER_MESSAGE);
        assertEquality(server,
                       new RemoteI18NBoundMessage(TEST_NONDESER_MESSAGE),
                       new RemoteI18NBoundMessage(null),
                       new RemoteI18NBoundMessage(TEST_I18N_MESSAGE));
        singleBase(server,
                   new SerWrapper<I18NBoundMessage>(TEST_NONDESER_MESSAGE),
                   false,
                   false,
                   EXPECTED_NONDESER_MESSAGE,
                   EXPECTED_NONDESER_MESSAGE,
                   EXPECTED_NONDESER_MESSAGE);

        singleNonDeserializable(server,assertRoundTripJAXB(server));
        singleNonDeserializable(server,assertRoundTripJava(server));
    }
}
