package org.marketcetera.util.quickfix;

import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.junit.Test;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.Issuer;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MsgSeqNum;
import quickfix.field.NoMDEntries;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.QuoteEntryID;
import quickfix.field.QuoteSetID;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TransactTime;
import quickfix.field.UnderlyingIssuer;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import quickfix.fix42.MassQuote;
import quickfix.fix42.NewOrderSingle;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class AnalyzedMessageTest
    extends AnalyzerTestBase
{
    @Test
    public void empty()
        throws Exception
    {
        NewOrderSingle msg=new NewOrderSingle();
        msg.toString();
        AnalyzedMessage msgA=new AnalyzedMessage(TEST_DICTIONARY,msg);
        assertNoEvents();
        assertEquals(3,msgA.getHeader().size());
        assertEquals(0,msgA.getBody().size());
        assertEquals(1,msgA.getTrailer().size());
        assertEquals
            (TEST_HEADER+"5"+
             SystemUtils.LINE_SEPARATOR+
             " MsgType [35R] = NewOrderSingle [D]"+
             TEST_FOOTER+"181",msgA.toString());
    }

    @Test
    public void valid()
        throws Exception
    {

        NewOrderSingle msg=new NewOrderSingle();
        // Required header fields.
        msg.getHeader().setField(new SenderCompID("me"));
        msg.getHeader().setField(new TargetCompID("you"));
        msg.getHeader().setField(new MsgSeqNum(1));
        msg.getHeader().setField(new SendingTime(new Date(2)));
        // Required body fields.
        msg.set(new ClOrdID("a"));
        msg.set(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE));
        msg.set(new TransactTime(new Date(3)));
        msg.set(new OrdType(OrdType.LIMIT));
        msg.set(new Symbol("METC"));
        // A required enumeration.
        msg.set(new Side(Side.BUY));
        // An optional non-enumeration.
        msg.set(new Account("metc"));
        msg.toString();
        AnalyzedMessage msgA=new AnalyzedMessage(TEST_DICTIONARY,msg);
        assertNoEvents();
        assertEquals(7,msgA.getHeader().size());
        assertEquals(7,msgA.getBody().size());
        assertEquals(1,msgA.getTrailer().size());
        assertEquals
            (SystemUtils.LINE_SEPARATOR+
             "Header"+
             SystemUtils.LINE_SEPARATOR+
             " BeginString [8R] = FIX.4.2"+
             SystemUtils.LINE_SEPARATOR+
             " BodyLength [9R] = 108"+
             SystemUtils.LINE_SEPARATOR+
             " MsgSeqNum [34R] = 1"+
             SystemUtils.LINE_SEPARATOR+
             " MsgType [35R] = NewOrderSingle [D]"+
             SystemUtils.LINE_SEPARATOR+
             " SenderCompID [49R] = me"+
             SystemUtils.LINE_SEPARATOR+
             " SendingTime [52R] = 19700101-00:00:00.002"+
             SystemUtils.LINE_SEPARATOR+
             " TargetCompID [56R] = you"+
             SystemUtils.LINE_SEPARATOR+
             "Body"+
             SystemUtils.LINE_SEPARATOR+
             " Account [1] = metc"+
             SystemUtils.LINE_SEPARATOR+
             " ClOrdID [11R] = a"+
             SystemUtils.LINE_SEPARATOR+
             " HandlInst [21R] = "+
             "AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION [1]"+
             SystemUtils.LINE_SEPARATOR+
             " OrdType [40R] = LIMIT [2]"+
             SystemUtils.LINE_SEPARATOR+
             " Side [54R] = BUY [1]"+
             SystemUtils.LINE_SEPARATOR+
             " Symbol [55R] = METC"+
             SystemUtils.LINE_SEPARATOR+
             " TransactTime [60R] = 19700101-00:00:00.003"+
             TEST_FOOTER+"076",msgA.toString());
    }

    @Test
    public void group()
        throws Exception
    {
        MarketDataSnapshotFullRefresh msg=
            new MarketDataSnapshotFullRefresh(new Symbol("QF"));
        MarketDataSnapshotFullRefresh.NoMDEntries group=
            new MarketDataSnapshotFullRefresh.NoMDEntries();
        group.set(new MDEntryType(MDEntryType.BID));
        group.set(new MDEntryPx(2));
        group.set(new MDEntrySize(16));
        group.set(new OrderID("ID1"));
        msg.addGroup(group);
        group=new MarketDataSnapshotFullRefresh.NoMDEntries();
        group.set(new MDEntryType('?'));
        group.set(new MDEntryPx(4));
        group.set(new MDEntrySize(32));
        group.set(new OrderID("ID2"));
        msg.addGroup(group);
        msg.toString();
        AnalyzedMessage msgA=new AnalyzedMessage(TEST_DICTIONARY,msg);
        assertNoEvents();
        assertEquals(3,msgA.getHeader().size());
        assertEquals(2,msgA.getBody().size());
        assertEquals(4,msgA.getBody().get(1).getGroups().
                     get(0).getFields().size());
        assertEquals(4,msgA.getBody().get(1).getGroups().
                     get(1).getFields().size());
        assertEquals(1,msgA.getTrailer().size());
        assertEquals
            (TEST_HEADER+"69"+
             SystemUtils.LINE_SEPARATOR+
             " MsgType [35R] = MarketDataSnapshotFullRefresh [W]"+
             SystemUtils.LINE_SEPARATOR+
             "Body"+
             SystemUtils.LINE_SEPARATOR+
             " Symbol [55R] = QF"+
             SystemUtils.LINE_SEPARATOR+
             " NoMDEntries [268R] = 2"+
             SystemUtils.LINE_SEPARATOR+
             "  Group 1"+
             SystemUtils.LINE_SEPARATOR+
             "   MDEntryType [269R] = BID [0]"+
             SystemUtils.LINE_SEPARATOR+
             "   OrderID [37] = ID1"+
             SystemUtils.LINE_SEPARATOR+
             "   MDEntryPx [270R] = 2"+
             SystemUtils.LINE_SEPARATOR+
             "   MDEntrySize [271] = 16"+
             SystemUtils.LINE_SEPARATOR+
             "  Group 2"+
             SystemUtils.LINE_SEPARATOR+
             "   MDEntryType [269R] = ? [invalid]"+
             SystemUtils.LINE_SEPARATOR+
             "   OrderID [37] = ID2"+
             SystemUtils.LINE_SEPARATOR+
             "   MDEntryPx [270R] = 4"+
             SystemUtils.LINE_SEPARATOR+
             "   MDEntrySize [271] = 32"+
             TEST_FOOTER+"219",msgA.toString());
    }

    @Test
    public void nestedGroup()
        throws Exception
    {
        MassQuote msg=new MassQuote();
        MassQuote.NoQuoteSets topGroup=new MassQuote.NoQuoteSets();
        topGroup.set(new QuoteSetID("TID1"));
        topGroup.set(new UnderlyingIssuer("UI1"));
        MassQuote.NoQuoteSets.NoQuoteEntries nestedGroup=
            new MassQuote.NoQuoteSets.NoQuoteEntries(); 
        nestedGroup.set(new QuoteEntryID("NID1.1"));
        nestedGroup.set(new Issuer("I1.1"));
        topGroup.addGroup(nestedGroup);
        nestedGroup=new MassQuote.NoQuoteSets.NoQuoteEntries(); 
        nestedGroup.set(new QuoteEntryID("NID1.2"));
        nestedGroup.set(new Issuer("I1.2"));
        topGroup.addGroup(nestedGroup);
        msg.addGroup(topGroup);
        topGroup=new MassQuote.NoQuoteSets();
        topGroup.set(new QuoteSetID("TID2"));
        topGroup.set(new UnderlyingIssuer("UI2"));
        nestedGroup=new MassQuote.NoQuoteSets.NoQuoteEntries(); 
        nestedGroup.set(new QuoteEntryID("NID2.1"));
        nestedGroup.set(new Issuer("I2.1"));
        topGroup.addGroup(nestedGroup);
        nestedGroup=new MassQuote.NoQuoteSets.NoQuoteEntries(); 
        nestedGroup.set(new QuoteEntryID("NID2.2"));
        nestedGroup.set(new Issuer("I2.2"));
        topGroup.addGroup(nestedGroup);
        msg.addGroup(topGroup);
        msg.toString();
        AnalyzedMessage msgA=new AnalyzedMessage(TEST_DICTIONARY,msg);
        assertNoEvents();
        assertEquals(3,msgA.getHeader().size());
        assertEquals(1,msgA.getBody().size());
        assertEquals(3,msgA.getBody().get(0).getGroups().
                     get(0).getFields().size());
        assertEquals(2,msgA.getBody().get(0).getGroups().
                     get(0).getFields().get(1).getGroups().
                     get(0).getFields().size());
        assertEquals(2,msgA.getBody().get(0).getGroups().
                     get(0).getFields().get(1).getGroups().
                     get(1).getFields().size());
        assertEquals(3,msgA.getBody().get(0).getGroups().
                     get(1).getFields().size());
        assertEquals(2,msgA.getBody().get(0).getGroups().
                     get(1).getFields().get(1).getGroups().
                     get(0).getFields().size());
        assertEquals(2,msgA.getBody().get(0).getGroups().
                     get(1).getFields().get(1).getGroups().
                     get(1).getFields().size());
        assertEquals(1,msgA.getTrailer().size());
        assertEquals
            (TEST_HEADER+"137"+
             SystemUtils.LINE_SEPARATOR+
             " MsgType [35R] = MassQuote [i]"+
             SystemUtils.LINE_SEPARATOR+
             "Body"+
             SystemUtils.LINE_SEPARATOR+
             " NoQuoteSets [296R] = 2"+
             SystemUtils.LINE_SEPARATOR+
             "  Group 1"+
             SystemUtils.LINE_SEPARATOR+
             "   QuoteSetID [302R] = TID1"+
             SystemUtils.LINE_SEPARATOR+
             "   NoQuoteEntries [295R] = 2"+
             SystemUtils.LINE_SEPARATOR+
             "    Group 1"+
             SystemUtils.LINE_SEPARATOR+
             "     QuoteEntryID [299R] = NID1.1"+
             SystemUtils.LINE_SEPARATOR+
             "     Issuer [106] = I1.1"+
             SystemUtils.LINE_SEPARATOR+
             "    Group 2"+
             SystemUtils.LINE_SEPARATOR+
             "     QuoteEntryID [299R] = NID1.2"+
             SystemUtils.LINE_SEPARATOR+
             "     Issuer [106] = I1.2"+
             SystemUtils.LINE_SEPARATOR+
             "   UnderlyingIssuer [306] = UI1"+
             SystemUtils.LINE_SEPARATOR+
             "  Group 2"+
             SystemUtils.LINE_SEPARATOR+
             "   QuoteSetID [302R] = TID2"+
             SystemUtils.LINE_SEPARATOR+
             "   NoQuoteEntries [295R] = 2"+
             SystemUtils.LINE_SEPARATOR+
             "    Group 1"+
             SystemUtils.LINE_SEPARATOR+
             "     QuoteEntryID [299R] = NID2.1"+
             SystemUtils.LINE_SEPARATOR+
             "     Issuer [106] = I2.1"+
             SystemUtils.LINE_SEPARATOR+
             "    Group 2"+
             SystemUtils.LINE_SEPARATOR+
             "     QuoteEntryID [299R] = NID2.2"+
             SystemUtils.LINE_SEPARATOR+
             "     Issuer [106] = I2.2"+
             SystemUtils.LINE_SEPARATOR+
             "   UnderlyingIssuer [306] = UI2"+
             TEST_FOOTER+"125",msgA.toString());
    }

    @Test
    public void missingType()
        throws Exception
    {
        Message msg=new Message();
        msg.toString();
        AnalyzedMessage msgA=new AnalyzedMessage(TEST_DICTIONARY,msg);
        assertSingleEvent
            (Level.ERROR,TEST_MESSAGE_CATEGORY,
             "Message type is missing from message '9=0"+SOH+"10=167"+SOH+"'",
             TEST_MESSAGE_CATEGORY);
        assertEquals(0,msgA.getHeader().size());
        assertEquals(0,msgA.getBody().size());
        assertEquals(0,msgA.getTrailer().size());
        assertEquals(StringUtils.EMPTY,msgA.toString());
    }

    @Test
    public void badEnum()
        throws Exception
    {
        NewOrderSingle msg=new NewOrderSingle();
        msg.set(new Side('?'));
        msg.toString();
        AnalyzedMessage msgA=new AnalyzedMessage(TEST_DICTIONARY,msg);
        assertNoEvents();
        assertEquals(3,msgA.getHeader().size());
        assertEquals(1,msgA.getBody().size());
        assertEquals(1,msgA.getTrailer().size());
        assertEquals
            (TEST_HEADER+"10"+
             SystemUtils.LINE_SEPARATOR+
             " MsgType [35R] = NewOrderSingle [D]"+
             SystemUtils.LINE_SEPARATOR+
             "Body"+
             SystemUtils.LINE_SEPARATOR+
             " Side [54R] = ? [invalid]"+
             TEST_FOOTER+"199",msgA.toString());
    }

    @Test
    public void missingGroup()
        throws Exception
    {
        MarketDataSnapshotFullRefresh msg=
            new MarketDataSnapshotFullRefresh(new Symbol("QF"));
        msg.set(new NoMDEntries(1));
        msg.toString();
        AnalyzedMessage msgA=new AnalyzedMessage(TEST_DICTIONARY,msg);
        assertSingleEvent
            (Level.ERROR,TEST_FIELD_CATEGORY,
             "Group 1 is missing from message scope '8=FIX.4.2"+SOH+"9=17"+SOH+
             "35=W"+SOH+"55=QF"+SOH+"268=0"+SOH+"10=072"+SOH+"'",
             TEST_FIELD_CATEGORY);
        assertEquals(3,msgA.getHeader().size());
        assertEquals(2,msgA.getBody().size());
        assertEquals(1,msgA.getTrailer().size());
        assertEquals
            (TEST_HEADER+"17"+
             SystemUtils.LINE_SEPARATOR+
             " MsgType [35R] = MarketDataSnapshotFullRefresh [W]"+
             SystemUtils.LINE_SEPARATOR+
             "Body"+
             SystemUtils.LINE_SEPARATOR+
             " Symbol [55R] = QF"+
             SystemUtils.LINE_SEPARATOR+
             " NoMDEntries [268R] = 1"+
             TEST_FOOTER+"072",msgA.toString());
    }
}
