package org.marketcetera.metrics;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.FIXResponse;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.springframework.stereotype.Component;

import quickfix.Message;

/* $License$ */

/**
 * Manages Isotope injection for various types of data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class IsotopeService
{
    /**
     * Injects an isotope into the given message.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     */
    public void inject(TradeMessage inTradeMessage)
    {
        if(SLF4JLoggerProxy.isDebugEnabled(isotopeCategory)) {
            inject(((FIXMessageWrapper)inTradeMessage).getMessage());
        }
    }
    /**
     * Injects an isotope into the given message.
     *
     * @param inExecutionReport an <code>ExecutionReport</code> value
     */
    public void inject(ExecutionReport inExecutionReport)
    {
        if(SLF4JLoggerProxy.isDebugEnabled(isotopeCategory)) {
            inject(((FIXMessageWrapper)inExecutionReport).getMessage());
        }
    }
    /**
     * Injects an isotope into the given message.
     *
     * @param inOrderCancelReject an <code>OrderCancelReject</code> value
     */
    public void inject(OrderCancelReject inOrderCancelReject)
    {
        if(SLF4JLoggerProxy.isDebugEnabled(isotopeCategory)) {
            inject(((FIXMessageWrapper)inOrderCancelReject).getMessage());
        }
    }
    /**
     * Injects an isotope into the given message.
     *
     * @param inFixResponse a <code>FIXResponse</code> value
     */
    public void inject(FIXResponse inFixResponse)
    {
        if(SLF4JLoggerProxy.isDebugEnabled(isotopeCategory)) {
            inject(((FIXMessageWrapper)inFixResponse).getMessage());
        }
    }
    /**
     * Injects an isotope into the given message.
     *
     * @param inMessage a <code>Message</code> value
     */
    public void inject(Message inMessage)
    {
        if(SLF4JLoggerProxy.isDebugEnabled(isotopeCategory)) {
            long inTimestamp = System.nanoTime();
            StringBuilder builder = new StringBuilder();
            try {
                if(inMessage.isSetField(fixIsotopeTag)) {
                    builder.append(inMessage.getString(fixIsotopeTag)).append(',');
                }
                builder.append(inTimestamp);
                inMessage.setString(fixIsotopeTag,
                                    builder.toString());
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }
    /**
     * Injects a final isoptope into the message, logs the result, and removes the isoptope.
     *
     * @param inMessage a <code>Message</code> value
     */
    public void remove(Message inMessage)
    {
        try {
            if(SLF4JLoggerProxy.isDebugEnabled(isotopeCategory)) {
                inject(inMessage);
                SLF4JLoggerProxy.debug(isotopeCategory,
                                       inMessage.toString());
            }
            if(inMessage.isSetField(fixIsotopeTag)) {
                inMessage.removeField(fixIsotopeTag);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
    /**
     * Gets the isotopes recorded on this message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>SortedMap&lt;Integer,Long&gt;</code> value
     */
    public SortedMap<Integer,Long> getIsotopes(Message inMessage)
    {
        if(!inMessage.isSetField(fixIsotopeTag)) {
            return emptyMap;
        }
        SortedMap<Integer,Long> isotopes = new TreeMap<>();
        try {
            if(inMessage.isSetField(fixIsotopeTag)) {
                String rawValue = inMessage.getString(fixIsotopeTag);
                if(rawValue != null) {
                    int index = 0;
                    for(String value : rawValue.split(",")) {
                        isotopes.put(index++,
                                     Long.parseLong(value));
                    }
                }
            }
        } catch (Exception ignored) {}
        return isotopes;
    }
    /**
     * Returns a human-readable version of the isotopes recorded on this message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>String</code> value
     */
    public String displayIsotopes(Message inMessage)
    {
        if(!inMessage.isSetField(fixIsotopeTag)) {
            return null;
        }
        SortedMap<Integer,Long> isotopes = getIsotopes(inMessage);
        if(isotopes.isEmpty()) {
            return null;
        }
        Table table = new Table(3,
                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                ShownBorders.ALL,
                                false);
        for(String header : dividendHeaders) {
            table.addCell(header,
                          headerStyle);
        }
        long lastTimestamp = -1;
        for(Map.Entry<Integer,Long> entry : isotopes.entrySet()) {
            table.addCell(String.valueOf(entry.getKey()));
            long timestamp = entry.getValue();
            table.addCell(String.valueOf(timestamp));
            if(lastTimestamp == -1) {
                table.addCell("--");
            } else {
                table.addCell(String.valueOf(timestamp-lastTimestamp));
            }
            lastTimestamp = timestamp;
        }
        return table.render();
    }
    /**
     * Get the fixIsotopeTag value.
     *
     * @return an <code>int</code> value
     */
    public int getFixIsotopeTag()
    {
        return fixIsotopeTag;
    }
    /**
     * Sets the fixIsotopeTag value.
     *
     * @param an <code>int</code> value
     */
    public void setFixIsotopeTag(int inFixIsotopeTag)
    {
        fixIsotopeTag = inFixIsotopeTag;
    }
    /**
     * default FIX tag to use for isotopes
     */
    private int fixIsotopeTag = 20000;
    /**
     * isotope logging category
     */
    public static final String isotopeCategory = "metc.isotopes";
    /**
     * table artifacts used to render isotopes in a human-readable format
     */
    private static final CellStyle headerStyle = new CellStyle(HorizontalAlign.center);
    /**
     * table artifacts used to render isotopes in a human-readable format
     */
    private static final String[] dividendHeaders = new String[] { "Index","Timestamp","Difference" };
    /**
     * used to indicate no isotopes present
     */
    private static final SortedMap<Integer,Long> emptyMap = Collections.unmodifiableSortedMap(new TreeMap<Integer,Long>());
}
