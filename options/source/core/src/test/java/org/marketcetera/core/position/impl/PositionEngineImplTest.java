package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.core.position.impl.BigDecimalMatchers.comparesEqualTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.core.instruments.UnderlyingSymbolSupport;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.ImmutablePositionSupport;
import org.marketcetera.core.position.MarketDataSupport;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionEngineFactory;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.Side;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Tests {@link PositionEngineImpl}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class PositionEngineImplTest {

    private abstract class PositionEngineTestTemplate implements Runnable {

        EventList<ReportHolder> reports = new BasicEventList<ReportHolder>();
        long tradeCounter = 0;

        @Override
        public void run() {
            initReports();
            PositionEngine engine = PositionEngineFactory.createFromReportHolders(reports,
                    new ImmutablePositionSupport(getIncomingPositions()),
                    mock(MarketDataSupport.class), createUnderlyingSymbolSupport());
            EventList<PositionRow> positions = getPositionData(engine);
            positions.addListEventListener(new ExpectedListChanges<PositionRow>("Positions",
                    getExpectedPositionListChanges()));
            validatePositions(positions);
        }

        protected UnderlyingSymbolSupport createUnderlyingSymbolSupport() {
            return new UnderlyingSymbolSupport() {
                @Override
                public String getUnderlying(Instrument instrument) {
                    return instrument.getSymbol();
                }
            };
        }

        protected Map<? extends PositionKey<?>, BigDecimal> getIncomingPositions() {
            return Maps.newHashMap();
        }

        protected EventList<PositionRow> getPositionData(PositionEngine engine) {
            return engine.getFlatData().getPositions();
        }

        protected void initReports() {
            // default is no-op
        }

        protected void validatePositions(EventList<PositionRow> positions) {
            // default is no-op
        }

        protected int[] getExpectedPositionListChanges() {
            return new int[] {};
        }

        protected void clearReports() {
            reports.clear();
        }

        protected void addTrade(Instrument instrument, String account, long traderId, Side side,
                String quantity, String price, long sequence) {
            add(new MockExecutionReport(account, instrument, traderId, side, price, quantity, sequence,
                    OrderStatus.Filled));
        }

        protected void addEquityTrade(String symbol, String account, Side side, String quantity,
                String price) {
            addEquityTrade(symbol, account, "1", side, quantity, price);
        }

        protected void addEquityTrade(String symbol, String account, String traderId, Side side,
                String quantity, String price) {
            addTrade(new Equity(symbol), account, traderId, side, quantity, price);
        }

        protected void addTrade(Instrument instrument, String account, String traderId, Side side,
                String quantity, String price) {
            addTrade(instrument, account, Long.valueOf(traderId), side, quantity, price, ++tradeCounter);
        }

        private void add(ReportBase report) {
            reports.add(new ReportHolder(report, null));
        }

        protected void assertEquityPosition(PositionRow position, String symbol, String account,
                String traderId, String amount) {
            assertPosition(position, new Equity(symbol), symbol, account, traderId, amount);
        }

        protected void assertPosition(PositionRow position, Instrument instrument, String underlying, String account,
                String traderId, String amount) {
            assertThat(position.getInstrument(), is(instrument));
            assertThat(position.getUnderlying(), is(underlying));
            assertThat(position.getAccount(), is(account));
            assertThat(position.getTraderId(), is(traderId));
            assertThat(position.getPositionMetrics().getPosition(), comparesEqualTo(amount));
        }

        protected void assertPositions(EventList<PositionRow> positions, String[]... values) {
            assertThat(positions.size(), is(values.length));
            for (int i = 0; i < values.length; i++) {
                String[] strings = values[i];
                assertEquityPosition(positions.get(i), strings[0], strings[1], strings[2], strings[3]);
            }
        }
    }

    @Test
    public void simpleInit() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addEquityTrade("METC", "personal", Side.Buy, "1000", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "1000");
            }
        }.run();
    }

    @Test
    public void complexInit() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addEquityTrade("METC", "personal", Side.Buy, "1000", "1");
                addEquityTrade("METC", "personal", Side.Buy, "104", "1");
                addEquityTrade("METC", "work", Side.Buy, "70", "1");
                addEquityTrade("GOOG", "personal", Side.Buy, "45.5", "1");
                addEquityTrade("YHOO", "work", Side.Buy, "20", "1");
                addEquityTrade("YHOO", "work", Side.Sell, "6", "1");
                addEquityTrade("ABC", "work", Side.Sell, "100", "1");
                addEquityTrade("ABC", "work", Side.Buy, "20", "1");
                addEquityTrade("ABC", "work", "2", Side.Buy, "20", "1");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(6));
                assertEquityPosition(positions.get(0), "ABC", "work", "1", "-80");
                assertEquityPosition(positions.get(1), "GOOG", "personal", "1", "45.5");
                assertEquityPosition(positions.get(2), "METC", "personal", "1", "1104");
                assertEquityPosition(positions.get(3), "METC", "work", "1", "70");
                assertEquityPosition(positions.get(4), "YHOO", "work", "1", "14");
                assertEquityPosition(positions.get(5), "ABC", "work", "2", "20");
            }
        }.run();
    }

    @Test
    public void simpleLiveUpdate() {
        new PositionEngineTestTemplate() {

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.INSERT, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                addEquityTrade("METC", "personal", Side.Buy, "1000", "1");
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "1000");
            }
        }.run();
    }

    @Test
    public void complexLiveUpdate() {
        new PositionEngineTestTemplate() {

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.INSERT, 0, ListEvent.UPDATE, 0, ListEvent.INSERT, 1,
                        ListEvent.INSERT, 0, ListEvent.INSERT, 3, ListEvent.UPDATE, 3,
                        ListEvent.INSERT, 0, ListEvent.UPDATE, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                addEquityTrade("METC", "personal", Side.Buy, "1000", "1");
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "1000");
                addEquityTrade("METC", "personal", Side.Buy, "104", "1");
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "1104");
                addEquityTrade("METC", "work", Side.Buy, "70", "1");
                assertThat(positions.size(), is(2));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "1104");
                assertEquityPosition(positions.get(1), "METC", "work", "1", "70");
                addEquityTrade("GOOG", "personal", Side.Buy, "45.5", "1");
                assertThat(positions.size(), is(3));
                assertEquityPosition(positions.get(0), "GOOG", "personal", "1", "45.5");
                assertEquityPosition(positions.get(1), "METC", "personal", "1", "1104");
                assertEquityPosition(positions.get(2), "METC", "work", "1", "70");
                addEquityTrade("YHOO", "work", Side.Buy, "20", "1");
                assertThat(positions.size(), is(4));
                assertEquityPosition(positions.get(0), "GOOG", "personal", "1", "45.5");
                assertEquityPosition(positions.get(1), "METC", "personal", "1", "1104");
                assertEquityPosition(positions.get(2), "METC", "work", "1", "70");
                assertEquityPosition(positions.get(3), "YHOO", "work", "1", "20");
                addEquityTrade("YHOO", "work", Side.Sell, "6", "1");
                assertThat(positions.size(), is(4));
                assertEquityPosition(positions.get(0), "GOOG", "personal", "1", "45.5");
                assertEquityPosition(positions.get(1), "METC", "personal", "1", "1104");
                assertEquityPosition(positions.get(2), "METC", "work", "1", "70");
                assertEquityPosition(positions.get(3), "YHOO", "work", "1", "14");
                addEquityTrade("ABC", "work", Side.Sell, "100", "1");
                assertThat(positions.size(), is(5));
                assertEquityPosition(positions.get(0), "ABC", "work", "1", "-100");
                assertEquityPosition(positions.get(1), "GOOG", "personal", "1", "45.5");
                assertEquityPosition(positions.get(2), "METC", "personal", "1", "1104");
                assertEquityPosition(positions.get(3), "METC", "work", "1", "70");
                assertEquityPosition(positions.get(4), "YHOO", "work", "1", "14");
                addEquityTrade("ABC", "work", Side.Buy, "20", "1");
                assertThat(positions.size(), is(5));
                assertEquityPosition(positions.get(0), "ABC", "work", "1", "-80");
                assertEquityPosition(positions.get(1), "GOOG", "personal", "1", "45.5");
                assertEquityPosition(positions.get(2), "METC", "personal", "1", "1104");
                assertEquityPosition(positions.get(3), "METC", "work", "1", "70");
                assertEquityPosition(positions.get(4), "YHOO", "work", "1", "14");
            }
        }.run();
    }

    @Test
    public void unknowns() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addEquityTrade("METC", null, Side.Buy, "1000", "500");
                addEquityTrade("METC", null, Side.Buy, "1000", "500");
                addEquityTrade("METC", null, Side.Sell, "200", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", null, "1", "1800");
            }
        }.run();
    }

    @Test
    @Ignore // clearing not currently supported
    public void clear() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addEquityTrade("METC", "personal", Side.Buy, "1000", "500");
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.DELETE, 0, ListEvent.INSERT, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                clearReports();
                assertThat(positions.size(), is(0));
                addEquityTrade("METC", "personal", Side.Buy, "2000", "500");
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "2000");
            }
        }.run();
    }

    @Test
    @Ignore // clearing not currently supported
    public void complexClear() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addEquityTrade("METC", "personal", Side.Buy, "1000", "500");
                addEquityTrade("METC", "personal", Side.Buy, "100", "500");
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.UPDATE, 0, ListEvent.DELETE, 0, ListEvent.INSERT, 0,
                        ListEvent.UPDATE, 0 };
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "1100");
                addEquityTrade("METC", "personal", Side.Buy, "200", "500");
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "1300");
                clearReports();
                assertThat(positions.size(), is(0));
                addEquityTrade("METC", "personal", Side.Buy, "2000", "500");
                addEquityTrade("METC", "personal", Side.Buy, "2000", "500");
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "4000");
            }
        }.run();
    }

    @Test
    public void validation() {
        new PositionEngineTestTemplate() {

            @Override
            protected void initReports() {
                addTrade(null, "personal", "1", Side.Buy, "1000", "500");
                addEquityTrade("METC", "personal", Side.Buy, "1000", "0");
                addEquityTrade("METC", "personal", Side.Buy, "0", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(0));
            }
        }.run();
    }

    @Test
    public void grouping() {
        new PositionEngineTestTemplate() {

            @Override
            protected EventList<PositionRow> getPositionData(PositionEngine engine) {
                return engine.getGroupedData(Grouping.Underlying, Grouping.Account).getPositions();
            }

            @Override
            protected void initReports() {
                addEquityTrade("METC", "personal", Side.Buy, "1000", "500");
                addEquityTrade("METC", "personal", Side.Buy, "100", "500");
                addEquityTrade("METC", "business", Side.Buy, "1050", "500");
                addEquityTrade("METC", "business", Side.Buy, "100", "500");
                addEquityTrade("IBM", "business", Side.Buy, "300", "500");
                addEquityTrade("IBM", "personal", Side.Buy, "200", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                // root
                assertPositions(positions, new String[] { "IBM", "business", "1", "500" },
                        new String[] { "METC", "business", "1", "2250" });

                // first level
                PositionRow ibm = positions.get(0);
                assertPositions(ibm.getChildren(), new String[] { "IBM", "business", "1", "300" },
                        new String[] { "IBM", "personal", "1", "200" });
                PositionRow metc = positions.get(1);
                assertPositions(metc.getChildren(),
                        new String[] { "METC", "business", "1", "1150" }, new String[] { "METC",
                                "personal", "1", "1100" });

                // second level
                PositionRow metcBusiness = metc.getChildren().get(0);
                assertPositions(metcBusiness.getChildren(), new String[] { "METC", "business", "1",
                        "1150" });
                PositionRow metcPersonal = metc.getChildren().get(1);
                assertPositions(metcPersonal.getChildren(), new String[] { "METC", "personal", "1",
                        "1100" });
                PositionRow ibmBusiness = ibm.getChildren().get(0);
                assertPositions(ibmBusiness.getChildren(), new String[] { "IBM", "business", "1",
                        "300" });
                PositionRow ibmPersonal = ibm.getChildren().get(1);
                assertPositions(ibmPersonal.getChildren(), new String[] { "IBM", "personal", "1",
                        "200" });

                // some new trades
                addEquityTrade("IBM", "business", Side.Sell, "45", "500");
                addEquityTrade("GOOG", "abc", Side.Sell, "100", "500");
                addEquityTrade("METC", "personal", Side.Sell, "90.3", "500");

                // root
                assertPositions(positions, new String[] { "GOOG", "abc", "1", "-100" },
                        new String[] { "IBM", "business", "1", "455" }, new String[] { "METC",
                                "business", "1", "2159.7" });

                // first level
                PositionRow goog = positions.get(0);
                assertPositions(goog.getChildren(), new String[] { "GOOG", "abc", "1", "-100" });
                ibm = positions.get(1);
                assertPositions(ibm.getChildren(), new String[] { "IBM", "business", "1", "255" },
                        new String[] { "IBM", "personal", "1", "200" });
                metc = positions.get(2);
                assertPositions(metc.getChildren(),
                        new String[] { "METC", "business", "1", "1150" }, new String[] { "METC",
                                "personal", "1", "1009.7" });

                // second level
                metcBusiness = metc.getChildren().get(0);
                assertPositions(metcBusiness.getChildren(), new String[] { "METC", "business", "1",
                        "1150" });
                metcPersonal = metc.getChildren().get(1);
                assertPositions(metcPersonal.getChildren(), new String[] { "METC", "personal", "1",
                        "1009.7" });
                ibmBusiness = ibm.getChildren().get(0);
                assertPositions(ibmBusiness.getChildren(), new String[] { "IBM", "business", "1",
                        "255" });
                ibmPersonal = ibm.getChildren().get(1);
                assertPositions(ibmPersonal.getChildren(), new String[] { "IBM", "personal", "1",
                        "200" });
                PositionRow googAbc = goog.getChildren().get(0);
                assertPositions(googAbc.getChildren(), new String[] { "GOOG", "abc", "1", "-100" });
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.UPDATE, 0, ListEvent.INSERT, 0, ListEvent.UPDATE, 2 };
            }

        }.run();
    }

    @Test
    public void grouping2() {
        new PositionEngineTestTemplate() {

            @Override
            protected EventList<PositionRow> getPositionData(PositionEngine engine) {
                return engine.getGroupedData(Grouping.Trader, Grouping.Account).getPositions();
            }

            @Override
            protected void initReports() {
                addEquityTrade("METC", "personal", "1", Side.Buy, "1000", "500");
                addEquityTrade("METC", "personal", "2", Side.Buy, "100", "500");
                addEquityTrade("IBM", "personal", "2", Side.Sell, "200", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                // root
                assertPositions(positions, new String[] { "METC", "personal", "1", "1000" },
                        new String[] { "IBM", "personal", "2", "-100" });

                // first level
                PositionRow t1 = positions.get(0);
                assertPositions(t1.getChildren(), new String[] { "METC", "personal", "1", "1000" });
                PositionRow t2 = positions.get(1);
                assertPositions(t2.getChildren(), new String[] { "IBM", "personal", "2", "-100" });

                // second level
                PositionRow t1Personal = t1.getChildren().get(0);
                assertPositions(t1Personal.getChildren(), new String[] { "METC", "personal", "1",
                        "1000" });
                PositionRow t2Personal = t2.getChildren().get(0);
                assertPositions(t2Personal.getChildren(), new String[] { "IBM", "personal", "2",
                        "-200" }, new String[] { "METC", "personal", "2", "100" });

                // some new trades
                addEquityTrade("IBM", "business", "2", Side.Sell, "45", "500");
                addEquityTrade("GOOG", "abc", "1", Side.Sell, "100", "500");

                // root
                assertPositions(positions, new String[] { "METC", "personal", "1", "900" },
                        new String[] { "IBM", "personal", "2", "-145" });

                // first level
                t1 = positions.get(0);
                assertPositions(t1.getChildren(), new String[] { "GOOG", "abc", "1", "-100" },
                        new String[] { "METC", "personal", "1", "1000" });
                t2 = positions.get(1);
                assertPositions(t2.getChildren(), new String[] { "IBM", "business", "2", "-45" },
                        new String[] { "IBM", "personal", "2", "-100" });

                // second level
                PositionRow t1Abc = t1.getChildren().get(0);
                assertPositions(t1Abc.getChildren(), new String[] { "GOOG", "abc", "1", "-100" });
                t1Personal = t1.getChildren().get(1);
                assertPositions(t1Personal.getChildren(), new String[] { "METC", "personal", "1",
                        "1000" });
                PositionRow t2Business = t2.getChildren().get(0);
                assertPositions(t2Business.getChildren(), new String[] { "IBM", "business", "2",
                        "-45" });
                t2Personal = t2.getChildren().get(1);
                assertPositions(t2Personal.getChildren(), new String[] { "IBM", "personal", "2",
                        "-200" }, new String[] { "METC", "personal", "2", "100" });
            }

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.UPDATE, 1, ListEvent.UPDATE, 0 };
            }

        }.run();
    }

    @Test
    public void incomingPosition() {
        new PositionEngineTestTemplate() {

            @Override
            protected int[] getExpectedPositionListChanges() {
                return new int[] { ListEvent.UPDATE, 0 };
            }

            @Override
            protected Map<? extends PositionKey<?>, BigDecimal> getIncomingPositions() {
                return ImmutableMap.of(PositionKeyFactory.createEquityKey("METC", "personal", "1"),
                        new BigDecimal("100"));
            }
            
            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "100");
                addEquityTrade("METC", "personal", Side.Buy, "1000", "1");
                assertThat(positions.size(), is(1));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "1100");
            }
        }.run();
    }

    @Test
    public void equityAndOptions() {
        new PositionEngineTestTemplate() {

            private UnderlyingSymbolSupport mUnderlyingSymbolSupport;
            private final Equity equity = new Equity("METC");
            private final Option option1 = new Option("MTC", "20090910", BigDecimal.ONE,
                    OptionType.Call);
            private final Option option2 = new Option("MEC", "20090910", BigDecimal.ONE,
                    OptionType.Call);
            private final Option option3 = new Option("PXR", "20090910", BigDecimal.ONE,
                    OptionType.Call);
            
            protected UnderlyingSymbolSupport createUnderlyingSymbolSupport() {
                mUnderlyingSymbolSupport = mock(UnderlyingSymbolSupport.class);
                when(mUnderlyingSymbolSupport.getUnderlying(equity)).thenReturn("METC");
                when(mUnderlyingSymbolSupport.getUnderlying(option1)).thenReturn("METC");
                when(mUnderlyingSymbolSupport.getUnderlying(option2)).thenReturn("METC");
                when(mUnderlyingSymbolSupport.getUnderlying(option3)).thenReturn("YHOO");
                return mUnderlyingSymbolSupport;
            };
            
            @Override
            protected void initReports() {
                addTrade(option3, "personal", "2", Side.Buy, "20", "1");
                addTrade(option1, "personal", "1", Side.Buy, "104", "1");
                addTrade(option1, "personal", "1", Side.Sell, "104", "1");
                addTrade(equity, "personal", "1", Side.Buy, "1000", "1");
                addTrade(equity, "personal", "1", Side.Buy, "104", "1");
                addTrade(option2, "personal", "1", Side.Buy, "20", "1");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                assertThat(positions.size(), is(4));
                assertEquityPosition(positions.get(0), "METC", "personal", "1", "1104");
                assertPosition(positions.get(1), option2, "METC", "personal", "1", "20");
                assertPosition(positions.get(2), option1, "METC", "personal", "1", "0");
                assertPosition(positions.get(3), option3, "YHOO", "personal", "2", "20");
            }
        }.run();
    }

    @Test
    public void groupingWithOptions() {
        new PositionEngineTestTemplate() {

            private UnderlyingSymbolSupport mUnderlyingSymbolSupport;
            private final Equity metcEquity = new Equity("METC");
            private final Equity ibmEquity = new Equity("IBM");
            private final Equity yhooEquity = new Equity("YHOO");
            private final Option metcOption = new Option("MTC", "20090910",
                    BigDecimal.ONE, OptionType.Call);
            private final Option yhooOption = new Option("PXR", "20090910",
                    BigDecimal.ONE, OptionType.Call);

            protected UnderlyingSymbolSupport createUnderlyingSymbolSupport() {
                mUnderlyingSymbolSupport = mock(UnderlyingSymbolSupport.class);
                when(mUnderlyingSymbolSupport.getUnderlying(metcEquity))
                        .thenReturn("METC");
                when(mUnderlyingSymbolSupport.getUnderlying(ibmEquity))
                        .thenReturn("IBM");
                when(mUnderlyingSymbolSupport.getUnderlying(yhooEquity))
                        .thenReturn("YHOO");
                when(mUnderlyingSymbolSupport.getUnderlying(metcOption))
                        .thenReturn("METC");
                when(mUnderlyingSymbolSupport.getUnderlying(yhooOption))
                        .thenReturn("YHOO");
                return mUnderlyingSymbolSupport;
            };

            @Override
            protected EventList<PositionRow> getPositionData(
                    PositionEngine engine) {
                return engine.getGroupedData(Grouping.Account,
                        Grouping.Underlying).getPositions();
            }

            @Override
            protected void initReports() {
                addEquityTrade("METC", "personal", Side.Buy, "1000", "500");
                addEquityTrade("METC", "personal", Side.Buy, "100", "500");
                addEquityTrade("METC", "business", Side.Buy, "1050", "500");
                addEquityTrade("METC", "business", Side.Buy, "100", "500");
                addEquityTrade("IBM", "business", Side.Buy, "300", "500");
                addEquityTrade("YHOO", "personal", Side.Buy, "200", "500");
                addEquityTrade("YHOO", "personal", Side.Buy, "200", "500");
                addTrade(metcOption, "business", "1", Side.Buy, "300", "500");
                addTrade(yhooOption, "personal", "1", Side.Buy, "400", "500");
            }

            @Override
            protected void validatePositions(EventList<PositionRow> positions) {
                // root
                assertPositions(positions, new String[] { "IBM", "business",
                        "1", "1750" }, new String[] { "METC", "personal", "1",
                        "1900" });

                // first level
                PositionRow business = positions.get(0);
                assertPositions(business.getChildren(), new String[] { "IBM",
                        "business", "1", "300" }, new String[] { "METC",
                        "business", "1", "1450" });
                PositionRow personal = positions.get(1);
                assertPositions(personal.getChildren(), new String[] { "METC",
                        "personal", "1", "1100" }, new String[] { "YHOO",
                        "personal", "1", "800" });

                // second level
                PositionRow ibmBusiness = business.getChildren().get(0);
                assertPositions(ibmBusiness.getChildren(), new String[] {
                        "IBM", "business", "1", "300" });
                PositionRow metcBusiness = business.getChildren().get(1);
                assertPositions(metcBusiness.getChildren(), new String[] {
                        "METC", "business", "1", "1450" });
                PositionRow metcPersonal = personal.getChildren().get(0);
                assertPositions(metcPersonal.getChildren(), new String[] {
                        "METC", "personal", "1", "1100" });
                PositionRow yhooPersonal = personal.getChildren().get(1);
                assertPositions(yhooPersonal.getChildren(), new String[] {
                        "YHOO", "personal", "1", "800" });

                // third level
                PositionRow ibmBusiness1 = ibmBusiness.getChildren().get(0);
                assertPositions(ibmBusiness1.getChildren(), new String[] {
                        "IBM", "business", "1", "300" });
                PositionRow metcBusiness1 = metcBusiness.getChildren().get(0);
                assertPosition(metcBusiness1.getChildren().get(0), metcEquity,
                        "METC", "business", "1", "1150");
                assertPosition(metcBusiness1.getChildren().get(1), metcOption,
                        "METC", "business", "1", "300");
                PositionRow metcPersonal1 = metcPersonal.getChildren().get(0);
                assertPositions(metcPersonal1.getChildren(), new String[] {
                        "METC", "personal", "1", "1100" });
                PositionRow yhooPersonal1 = yhooPersonal.getChildren().get(0);
                assertPosition(yhooPersonal1.getChildren().get(0), yhooEquity,
                        "YHOO", "personal", "1", "400");
                assertPosition(yhooPersonal1.getChildren().get(1), yhooOption,
                        "YHOO", "personal", "1", "400");
            }

        }.run();
    }
}
