package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.marketcetera.trade.DeliveryType;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;
import org.marketcetera.trade.FutureType;
import org.marketcetera.trade.FutureUnderlyingAssetType;
import org.marketcetera.trade.Spread;
import org.marketcetera.trade.StandardType;

/* $License$ */

/**
 * Tests {@link SpreadBean}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SpreadBeanTest.java 16994 2015-03-09 21:18:25Z colin $
 * @since 2.1.4
 */
public class SpreadBeanTest
{
    /**
     * Tests {@link SpreadBean#copy(SpreadBean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void copy()
            throws Exception
    {
        doCopyTest(new SpreadBean());
    }
    /**
     * Tests the ability to serialize a <code>SpreadBean</code> value. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void serializationTest()
            throws Exception
    {
        SpreadBean bean = new SpreadBean();
        // Serialize to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        ObjectOutput out = new ObjectOutputStream(bos) ;
        out.writeObject(bean);
        out.close();
    }
    /**
     * Performs one copy test. 
     *
     * @param inBean a <code>SpreadBean</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doCopyTest(SpreadBean inBean)
            throws Exception
    {
        verifySpreadBean(inBean,
                         null,
                         null,
                         null,
                         null,
                         null,
                         null,
                         1);
        SpreadBean newBean = SpreadBean.copy(inBean);
        verifySpreadBean(newBean,
                         null,
                         null,
                         null,
                         null,
                         null,
                         null,
                         1);
        Spread spread = new Spread(new Future("METC",
                                              FutureExpirationMonth.NOVEMBER,
                                              2011),
                                   new Future("METC",
                                              FutureExpirationMonth.DECEMBER,
                                              2011));
        inBean.setInstrument(spread);
        DeliveryType deliveryType = DeliveryType.PHYSICAL;
        inBean.getLeg1Bean().setDeliveryType(deliveryType);
        inBean.getLeg2Bean().setDeliveryType(deliveryType);
        String providerSymbol = "METC/11X-11Z";
        inBean.setProviderSymbol(providerSymbol);
        StandardType standardType = StandardType.STANDARD;
        inBean.getLeg1Bean().setStandardType(standardType);
        inBean.getLeg2Bean().setStandardType(standardType);
        FutureType futureType = FutureType.COMMODITY;
        inBean.getLeg1Bean().setFutureType(futureType);
        inBean.getLeg2Bean().setFutureType(futureType);
        FutureUnderlyingAssetType futureUnderlyingAssetType = new FutureUnderlyingAssetType() {};
        inBean.getLeg1Bean().setUnderlyingAssetType(futureUnderlyingAssetType);
        inBean.getLeg2Bean().setUnderlyingAssetType(futureUnderlyingAssetType);
        int contractSize = Integer.MAX_VALUE;
        inBean.getLeg1Bean().setContractSize(contractSize);
        inBean.getLeg2Bean().setContractSize(contractSize);
        verifySpreadBean(inBean,
                         spread,
                         deliveryType,
                         providerSymbol,
                         standardType,
                         futureType,
                         futureUnderlyingAssetType,
                         contractSize);
        newBean = SpreadBean.copy(inBean);
        verifySpreadBean(inBean,
                         spread,
                         deliveryType,
                         providerSymbol,
                         standardType,
                         futureType,
                         futureUnderlyingAssetType,
                         contractSize);
    }
    /**
     * Verifies the given <code>SpreadBean</code> has the given expected attributes.
     *
     * @param inActualBean a <code>SpreadBean</code> value
     * @param inExpectedInstrument a <code>Spread</code> value
     * @param inExpectedDeliveryType a <code>DeliveryType</code> value
     * @param inExpectedProviderSymbol a <code>String</code> value
     * @param inExpectedStandardType a <code>StandardType</code> value
     * @param inExpectedFutureType a <code>FutureType</code> value
     * @param inExpectedFutureUnderlyingAssetType a <code>FutureUnderlyingAssetType</code> value
     * @param inExpectedContractSize an <code>int</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifySpreadBean(SpreadBean inActualBean,
                                  Spread inExpectedInstrument,
                                  DeliveryType inExpectedDeliveryType,
                                  String inExpectedProviderSymbol,
                                  StandardType inExpectedStandardType,
                                  FutureType inExpectedFutureType,
                                  FutureUnderlyingAssetType inExpectedFutureUnderlyingAssetType,
                                  int inExpectedContractSize)
            throws Exception
    {
        assertNotNull(inActualBean.toString());
        assertEquals(inExpectedInstrument,
                     inActualBean.getInstrument());
        assertEquals(inExpectedDeliveryType,
                     inActualBean.getLeg1Bean().getDeliveryType());
        assertEquals(inExpectedDeliveryType,
                     inActualBean.getLeg2Bean().getDeliveryType());
        assertEquals(inExpectedProviderSymbol,
                     inActualBean.getProviderSymbol());
        assertEquals(inExpectedStandardType,
                     inActualBean.getLeg1Bean().getStandardType());
        assertEquals(inExpectedStandardType,
                     inActualBean.getLeg2Bean().getStandardType());
        assertEquals(inExpectedFutureType,
                     inActualBean.getLeg1Bean().getFutureType());
        assertEquals(inExpectedFutureType,
                     inActualBean.getLeg2Bean().getFutureType());
        assertEquals(inExpectedFutureUnderlyingAssetType,
                     inActualBean.getLeg1Bean().getUnderlyingAssetType());
        assertEquals(inExpectedFutureUnderlyingAssetType,
                     inActualBean.getLeg2Bean().getUnderlyingAssetType());
        assertEquals(inExpectedContractSize,
                     inActualBean.getLeg1Bean().getContractSize());
        assertEquals(inExpectedContractSize,
                     inActualBean.getLeg2Bean().getContractSize());
    }
}
