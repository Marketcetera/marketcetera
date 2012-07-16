package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import junit.framework.TestCase;
import junit.framework.Test;
import quickfix.Message;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;
import org.marketcetera.quickfix.FIXMessageUtilTest;

import java.math.BigDecimal;
/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageAugmentor_42Test extends TestCase {
    public FIXMessageAugmentor_42Test(String inName) {
        super(inName);
        fixVersion = FIXVersion.FIX42;
        msgFactory=FIXVersion.getFIXVersion(FIXVersion.FIX42.toString()).getMessageFactory();
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXMessageAugmentor_42Test.class);
    }

    public void testCountTT_applicableTypes() throws Exception {
        assertEquals(18, new FIXMessageAugmentor_42().getApplicableMsgTypes().size());
    }

    public void testOrderWithTIFAtTheClose() throws Exception{
        
        
        Message lmtOrder=fixVersion.getMessageFactory().getMsgAugmentor().newOrderSingleAugment(createNewLimitOrder());
         assertEquals(OrdType.LIMIT, lmtOrder.getChar(OrdType.FIELD)); 
         assertEquals(TimeInForce.AT_THE_CLOSE, lmtOrder.getChar(TimeInForce.FIELD));
         
        Message mrktOrder=fixVersion.getMessageFactory().getMsgAugmentor().newOrderSingleAugment(createNewMarketOrder());
         assertEquals(OrdType.MARKET, mrktOrder.getChar(OrdType.FIELD)); 
         assertEquals(TimeInForce.AT_THE_CLOSE, mrktOrder.getChar(TimeInForce.FIELD));
         
         Message replaceLmtOrder = fixVersion.getMessageFactory().getMsgAugmentor().cancelReplaceRequestAugment(createNewLimitOrder());
          assertEquals(OrdType.LIMIT, replaceLmtOrder.getChar(OrdType.FIELD));
         assertEquals(TimeInForce.AT_THE_CLOSE, replaceLmtOrder.getChar(TimeInForce.FIELD));
         
         
         Message replaceMrktOrder = fixVersion.getMessageFactory().getMsgAugmentor().cancelReplaceRequestAugment(createNewMarketOrder());
         assertEquals(OrdType.MARKET, replaceMrktOrder.getChar(OrdType.FIELD)); 
         assertEquals(TimeInForce.AT_THE_CLOSE, replaceMrktOrder.getChar(TimeInForce.FIELD));
         
         
      }
    
    private Message createNewLimitOrder(){
        Message message=FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("123"), new BigDecimal("100"), Side.BUY, msgFactory);
        message.setChar(TimeInForce.FIELD, TimeInForce.AT_THE_CLOSE);
        return message;
    }
    
    private Message createNewMarketOrder(){
        Message message=FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("100"), Side.BUY, msgFactory);
        message.setChar(TimeInForce.FIELD, TimeInForce.AT_THE_CLOSE);
        return message;
    }
    protected FIXMessageFactory msgFactory;
    protected FIXVersion fixVersion;
}
