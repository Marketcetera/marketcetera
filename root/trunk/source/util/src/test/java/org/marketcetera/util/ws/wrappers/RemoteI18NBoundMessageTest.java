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
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class RemoteI18NBoundMessageTest
    extends WrapperTestBase
{
    private static final String TEST_MESSAGE=
        "testMessage";
    private static final I18NBoundMessage1P TEST_I18N_MESSAGE=
        new I18NBoundMessage1P(TestMessages.BOUND,TEST_MESSAGE);
    private static final String EXPECTED_MESSAGE=
        "Bound message text is '"+TEST_MESSAGE+"'";
    private static final String EXPECTED_MESSAGE_FR=
        "Bound message text in French is '"+TEST_MESSAGE+"'";
    private static final String EXPECTED_MISSING_MESSAGE=
        "provider 'nonexistent_prv'; id 'any'; entry 'msg'; parameters ()";


    private void singleBase
        (RemoteI18NBoundMessage m,
         I18NBoundMessage message,
         String string,
         String text,
         String textFr,
         boolean proxyUsed)
    {
        if (message==null) {
            assertNull(m.getWrapper());
        } else if (proxyUsed) {
            assertSerWrapperFailure(m.getWrapper());
        } else {
            assertEquals(message,m.getWrapper().getRaw());
        }

        assertEquals(string,m.getString());
        assertEquals(text,m.getText());
        assertEquals(text,m.toString());

        ActiveLocale.setProcessLocale(Locale.FRENCH);
        assertEquals(textFr,m.getText());
        assertEquals(textFr,m.toString());
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }

    private void single
        (RemoteI18NBoundMessage m,
         I18NBoundMessage message,
         String string,
         String text,
         String textFr)
        throws Exception
    {
        singleBase(m,message,string,text,textFr,false);
        singleBase(assertRoundTripJAXB(m),message,string,text,textFr,false);
        singleBase(assertRoundTripJava(m),message,string,text,textFr,false);
    }

    private void singleMissingResources
        (RemoteI18NBoundMessage server,
         RemoteI18NBoundMessage client)
    {
        singleBase(client,
                   server.getWrapper().getRaw(),
                   server.getString(),
                   server.getText(),
                   server.getText(),
                   true);
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
             (new I18NBoundMessage1P(TestMessages.BOUND,TEST_MESSAGE+"d")),
             new RemoteI18NBoundMessage
             (new I18NBoundMessage1P(TestMessages.EXCEPTION,TEST_MESSAGE)));
        single(new RemoteI18NBoundMessage(TEST_I18N_MESSAGE),
               TEST_I18N_MESSAGE,
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
    public void missingResources()
        throws Exception
    {
        I18NBoundMessage m=createBadProviderMessage();

        RemoteI18NBoundMessage server=new RemoteI18NBoundMessage(m);
        assertEquality(server,
                       new RemoteI18NBoundMessage(m),
                       new RemoteI18NBoundMessage(null),
                       new RemoteI18NBoundMessage(TEST_I18N_MESSAGE));
        singleBase(server,
                   m,
                   EXPECTED_MISSING_MESSAGE,
                   EXPECTED_MISSING_MESSAGE,
                   EXPECTED_MISSING_MESSAGE,
                   false);

        singleMissingResources(server,assertRoundTripJAXB(server));
        singleMissingResources(server,assertRoundTripJava(server));
    }
}
