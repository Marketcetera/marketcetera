package org.marketcetera.event.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.DeliveryType;

/* $License$ */

/**
 * Tests {@link CurrencyBean}.
 *
 */
public class CurrencyBeanTest
{
    /**
     * Tests {@link CurrencyBean#copy(CurrencyBean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void copy()
            throws Exception
    {
        doCopyTest(new CurrencyBean());
    }
    /**
     * Tests the ability to serialize a <code>CurrencyBean</code> value. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void serializationTest()
            throws Exception
    {
    	CurrencyBean bean = new CurrencyBean();
        // Serialize to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        ObjectOutput out = new ObjectOutputStream(bos) ;
        out.writeObject(bean);
        out.close();
    }
    /**
     * Performs one copy test. 
     *
     * @param inBean a <code>CurrencyBean</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doCopyTest(CurrencyBean inBean)
            throws Exception
    {
        verifyCurrencyBean(inBean,
                         null,
                         null);
        CurrencyBean newBean = CurrencyBean.copy(inBean);
        verifyCurrencyBean(newBean,
                         null,
                         null);
        Currency currency = new Currency("USD/GBP");
        inBean.setInstrument(currency);
        DeliveryType deliveryType = DeliveryType.PHYSICAL;
        int contractSize = Integer.MAX_VALUE;
        inBean.setContractSize(contractSize);
        verifyCurrencyBean(inBean,
                         currency,
                         deliveryType);
        newBean = CurrencyBean.copy(inBean);
        verifyCurrencyBean(inBean,
                         currency,
                         deliveryType);
    }
    /**
     * Verifies the given <code>CurrencyBean</code> has the given expected attributes.
     *
     * @param inActualBean a <code>CurrencyBean</code> value
     * @param inExpectedInstrument a <code>Currency</code> value
     * @param inExpectedDeliveryType a <code>DeliveryType</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyCurrencyBean(CurrencyBean inActualBean,
                                  Currency inExpectedInstrument,
                                  DeliveryType inExpectedDeliveryType)
            throws Exception
    {
        assertNotNull(inActualBean.toString());
        assertEquals(inExpectedInstrument,inActualBean.getInstrument());
    }
}
