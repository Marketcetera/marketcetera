package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.file.CopyCharsUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.orderloader.fix.FIXProcessorTest;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

import java.io.File;
import java.util.Set;
import java.util.EnumSet;

/* $License$ */
/**
 * Tests {@link OrderLoader}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class OrderLoaderTest {
    @BeforeClass
    public static void setupLogger() {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void nullArguments() throws Exception {
        //null order processor
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                new OrderLoader("",new BrokerID("y"), null,
                        new File("don'tmatter"));
            }
        };
        //null file
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                new OrderLoader("",new BrokerID("y"),
                        new MockOrderProcessor(), null);
            }
        };
    }
    @Test
    public void invalidModeValues() throws Exception {
        Set<FIXVersion> supportedValues = EnumSet.allOf(FIXVersion.class);
        supportedValues.remove(FIXVersion.FIX_SYSTEM);
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_FIX_VERSION, "why", supportedValues.toString()){
            protected void run() throws Exception {
                new OrderLoader("why", null, new MockOrderProcessor(),
                        new File("don'tmatter"));
            }
        };
        new ExpectedFailure<OrderParsingException>(
                Messages.INVALID_FIX_VERSION, FIXVersion.FIX_SYSTEM.toString(),
                supportedValues.toString()){
            protected void run() throws Exception {
                new OrderLoader(FIXVersion.FIX_SYSTEM.toString(), null,
                        new MockOrderProcessor(), new File("don'tmatter"));
            }
        };
    }
    @Test
    public void brokerIDRequiredForFIX() throws Exception {
        new ExpectedFailure<OrderParsingException>(Messages.BROKER_ID_REQUIRED){
            protected void run() throws Exception {
                new OrderLoader(FIXVersion.FIX42.toString(), null,
                        new MockOrderProcessor(), new File("don'tmatter"));
            }
        };
    }
    @Test
    public void fixParse() throws Exception {
        File tmpFile = File.createTempFile("ordloader",".csv");
        tmpFile.deleteOnExit();
        CopyCharsUtils.copy(FIXProcessorTest.ORDER_EXAMPLE.toCharArray(),
                tmpFile.getAbsolutePath());
        assertLoader(FIXVersion.FIX42.toString(), new BrokerID("yo"),
                tmpFile, 20, 2, 0, 9, 8);
        tmpFile.delete();
    }
    @Test
    public void sysParse() throws Exception {
        File tmpFile = File.createTempFile("ordloader",".csv");
        tmpFile.deleteOnExit();
        CopyCharsUtils.copy(OrderParserTest.arrayToLines(SYSTEM_ORDER_EXAMPLE).toCharArray(),
                tmpFile.getAbsolutePath());
        assertLoader(null, null, tmpFile, 22, 5, 5, 4, 7);
        assertLoader(OrderLoader.MODE_SYSTEM, null, tmpFile, 22, 5, 5, 4, 7);
        assertLoader(OrderLoader.MODE_SYSTEM, new BrokerID("yes"),
                tmpFile, 22, 5, 5, 4, 7);
        tmpFile.delete();
    }
    private OrderLoader assertLoader(String inMode, BrokerID inBrokerID,
                              File inFile, int inNumLines, int inBlankLines,
                              int inNumComments, int inNumSuccess,
                              int inNumFailed) throws Exception {
        OrderLoader loader = new OrderLoader(inMode, inBrokerID,
                new MockOrderProcessor(), inFile);
        assertEquals(inNumLines, loader.getNumLines());
        assertEquals(inBlankLines, loader.getNumBlankLines());
        assertEquals(inNumComments, loader.getNumComments());
        assertEquals(inNumSuccess, loader.getNumSuccess());
        assertEquals(inNumFailed, loader.getNumFailed());
        assertEquals(inNumFailed, loader.getFailedOrders().size());
        return loader;
    }
    public static final String[] SYSTEM_ORDER_EXAMPLE = new String[]{
            "#Sample system order input",
            "",
            "Account,OrderCapacity,OrderType,PositionEffect,Price,Quantity,Side,Symbol,SecurityType,TimeInForce,1001",
            "",
            "#ubm equity orders",
            "AC1,,Market,,,11,Buy,ubm,,Day,right",
            "AC1,,Limit,,25.56,32.09,Sell,ubm,,FillOrKill,left",
            "  ",
            "#Option orders",
            "AC2,Agency,Limit,Open,90,50,SellShort,zoog,Option,AtTheClose,up",
            "AC2,Individual,Market,Close,,101,SellShortExempt,moog,Option,Day,down",
            "  ",
            "#Equity Orders with errors",
            "AC1,,OrderType?,,,11,Buy,ubm,,Day,right",
            "AC1,,Market,,,eleven,Buy,ubm,,Day,right",
            "AC1,,Market,,,11,Buy,ubm,,Night,right",
            "  ",
            "#Option Orders with errors",
            "AC2,MyCapacity,Limit,Open,90,50,SellShort,zoog,Option,AtTheClose,up",
            "AC2,Agency,Limit,Open,ninety,50,SellShort,zoog,Option,AtTheClose,up",
            "AC2,Agency,Limit,Open,90,50,BuyShort,zoog,Option,AtTheClose,up",
            "AC2,Agency,Limit,Open,90,50,SellShort,zoog,Future,AtTheClose,up",
    };
}

